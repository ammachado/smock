/*
 * Copyright 2005-2010 the original author or authors.
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

package net.javacrumbs.smock.common.server;

import static net.javacrumbs.smock.common.XmlUtil.getEnvelopeSource;
import static net.javacrumbs.smock.common.XmlUtil.serialize;

import java.nio.charset.Charset;

import javax.servlet.http.HttpServlet;

import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.util.Assert;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.test.server.RequestCreator;
import org.springframework.ws.test.server.ResponseActions;
import org.springframework.ws.test.support.MockStrategiesHelper;


public class ServletBasedMockWebServiceClient {

	private final HttpServlet servlet;
	
	private final WebServiceMessageFactory messageFactory;
	
	private static final Charset UTF8 = (Charset)Charset.availableCharsets().get("UTF-8");  
	
	public ServletBasedMockWebServiceClient(String servletClassName, ApplicationContext applicationContext) {
		Assert.notNull(applicationContext, "ApplicationContext has to be set");
        messageFactory =   new MockStrategiesHelper(applicationContext).getStrategy(WebServiceMessageFactory.class, SaajSoapMessageFactory.class);
		
        MockServletConfig config = new MockServletConfig();
        config.getServletContext().setAttribute("org.springframework.web.context.WebApplicationContext.ROOT", applicationContext);
		try {
			servlet = (HttpServlet) Class.forName(servletClassName).newInstance();
			servlet.init(config);
		} catch (Exception e) {
			throw new IllegalArgumentException("Error when creating servlet "+servletClassName,e);
		}
	}
	
	public ResponseActions sendRequestTo(String path, RequestCreator requestCreator) {
		try {
			WebServiceMessage message = requestCreator.createRequest(messageFactory);
			MockHttpServletRequest   request = createRequest(path, message);
			MockHttpServletResponse response = new MockHttpServletResponse();
			servlet.service(request, response);
			
		} catch (Exception e) {
			throw new IllegalStateException("Error when sending request",e);
		}
		return null;
	}

	protected MockHttpServletRequest createRequest(String path, WebServiceMessage message) {
		String requestXml = serialize(getEnvelopeSource(message));
		MockHttpServletRequest request = new ExtendedMockHttpServletRequest();
		request.setMethod("POST");
		request.setPathInfo(path);
		request.setContent(requestXml.getBytes(UTF8));
		request.setContentType("text/xml;charset=UTF-8");
		return request;
	}
	
	private static class ExtendedMockHttpServletRequest extends MockHttpServletRequest
	{
		@Override
		public String getRealPath(String path) {
			return super.getPathInfo();
		}
	}
}
