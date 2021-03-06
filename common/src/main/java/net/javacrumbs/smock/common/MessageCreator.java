/**
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.javacrumbs.smock.common;

import static net.javacrumbs.smock.common.XmlUtil.transform;
import static net.javacrumbs.smock.common.XmlUtil.getEnvelopeSource;
import static net.javacrumbs.smock.common.XmlUtil.getSourceAsStream;
import static net.javacrumbs.smock.common.XmlUtil.isSoap;
import static net.javacrumbs.smock.common.XmlUtil.serialize;

import java.io.IOException;
import java.net.URI;

import javax.xml.transform.Source;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.test.client.ResponseCreator;
import org.springframework.ws.test.server.RequestCreator;

/**
 * Class that is able to create a message for both client and server.
 * @author Lukas Krecan
 *
 */
public class MessageCreator implements ResponseCreator, RequestCreator{

	private final Source source;
	
	protected final Log logger = LogFactory.getLog(getClass());

	public MessageCreator(Source sourceDocument) {
		this.source = sourceDocument;
	}
	

	/**
	 * Creates a message. If source document is a SOAP message a message is created as it is (including SOAP faults), if it
	 * contains only a payload, it's wrapped in a SOAP envelope.
	 * @param response
	 * @param messageFactory
	 * @return
	 * @throws IOException
	 */
	protected final WebServiceMessage createMessage(URI uri, WebServiceMessage input, WebServiceMessageFactory messageFactory) throws IOException {
		Source source = preprocessSource(uri, input, messageFactory);
		WebServiceMessage result;
		if (isSoap(source))
		{
			result = messageFactory.createWebServiceMessage(getSourceAsStream(source));
		}
		else
		{
			WebServiceMessage webServiceMessage = messageFactory.createWebServiceMessage();
			transform(source, webServiceMessage.getPayloadResult());
			result = webServiceMessage;
		}
		if (logger.isDebugEnabled())
		{
			logger.debug("Generated message: "+serialize(getEnvelopeSource(result)));
		}
		return result;
	}
	
	public WebServiceMessage createResponse(URI uri, WebServiceMessage request, WebServiceMessageFactory messageFactory) throws IOException {
		return createMessage(uri, request, messageFactory);
	}

	public WebServiceMessage createRequest(WebServiceMessageFactory messageFactory) throws IOException {
		return createMessage(null, null, messageFactory);
	}

	/**
	 * To be overriden by subclasses.
	 * @param uri
	 * @param input
	 * @param messageFactory
	 * @return
	 */
	protected Source preprocessSource(URI uri, WebServiceMessage input, WebServiceMessageFactory messageFactory) {
		return getSource();
	}

	public final Source getSource() {
		return source;
	}

}
