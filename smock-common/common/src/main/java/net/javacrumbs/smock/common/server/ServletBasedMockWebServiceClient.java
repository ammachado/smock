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
import static org.springframework.ws.test.support.AssertionErrors.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.WeakHashMap;

import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.context.DefaultMessageContext;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.test.server.RequestCreator;
import org.springframework.ws.test.server.ResponseActions;
import org.springframework.ws.test.server.ResponseMatcher;
import org.springframework.ws.test.support.MockStrategiesHelper;


public class ServletBasedMockWebServiceClient {

	private HttpServlet servlet;
	
	private final WebServiceMessageFactory messageFactory;
	
	private static final WeakHashMap<ApplicationContext, HttpServlet> servletCache = new WeakHashMap<ApplicationContext, HttpServlet>(); 
	
	private static final Charset UTF8 = (Charset)Charset.availableCharsets().get("UTF-8");  
	
	private static final Log LOG = LogFactory.getLog(ServletBasedMockWebServiceClient.class);
	
	public ServletBasedMockWebServiceClient(String servletClassName, ApplicationContext applicationContext) {
		Assert.notNull(applicationContext, "ApplicationContext has to be set");
        messageFactory =   new MockStrategiesHelper(applicationContext).getStrategy(WebServiceMessageFactory.class, SaajSoapMessageFactory.class);
		
        createServlet(servletClassName, applicationContext);
	}

	private void createServlet(String servletClassName, ApplicationContext applicationContext) {
		if (servletCache.containsKey(applicationContext))
		{
			servlet = servletCache.get(applicationContext);
			return;
		}
		MockServletConfig config = new MockServletConfig();
        GenericWebApplicationContext webApplicationContext = new GenericWebApplicationContext();
        webApplicationContext.setParent(applicationContext);
        webApplicationContext.setServletContext(config.getServletContext());
        webApplicationContext.refresh();
		config.getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext );
		try {
			servlet = (HttpServlet) Class.forName(servletClassName).newInstance();
			servlet.init(config);
			servletCache.put(applicationContext, servlet);
		} catch (Exception e) {
			throw new IllegalArgumentException("Error when creating servlet "+servletClassName,e);
		}
	}
	
	public ResponseActions sendRequestTo(String path, RequestCreator requestCreator) {
		try {
			WebServiceMessage requestMessage = requestCreator.createRequest(messageFactory);
			MockHttpServletRequest   request = createRequest(path, requestMessage);
			MockHttpServletResponse response = new MockHttpServletResponse();
			servlet.service(request, response);
			MessageContext messageContext = new DefaultMessageContext(requestMessage, messageFactory);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Received response:"+response.getContentAsString());
			}
			messageContext.setResponse(messageFactory.createWebServiceMessage(new ByteArrayInputStream(response.getContentAsByteArray())));
			return new MockWebServiceClientResponseActions(messageContext);
		} catch (Exception e) {
			throw new IllegalStateException("Error when sending request",e);
		}
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
	
	// ResponseActions

    private static class MockWebServiceClientResponseActions implements ResponseActions {

        private final MessageContext messageContext;

        private MockWebServiceClientResponseActions(MessageContext messageContext) {
            Assert.notNull(messageContext, "'messageContext' must not be null");
            this.messageContext = messageContext;
        }

        public ResponseActions andExpect(ResponseMatcher responseMatcher) {
            WebServiceMessage request = messageContext.getRequest();
            WebServiceMessage response = messageContext.getResponse();
            if (response == null) {
                fail("No response received");
                return null;
            }
            try {
                responseMatcher.match(request, response);
                return this;
            }
            catch (IOException ex) {
                fail(ex.getMessage());
                return null;
            }
        }
    }
}
