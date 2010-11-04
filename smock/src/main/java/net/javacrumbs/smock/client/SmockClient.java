package net.javacrumbs.smock.client;
import java.util.Collections;

import javax.xml.transform.Source;

import net.javacrumbs.smock.common.SmockCommon;
import net.javacrumbs.smock.common.TemplateAwareMessageCompareMatcher;
import net.javacrumbs.smock.common.TemplateAwareMessageCreator;

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.w3c.dom.Document;

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

/**
 * Adds extra features to {@link WebServiceMock}. 
 */
public abstract class SmockClient extends SmockCommon {
			
	/**
	 * Expects the given XML message loaded from resource with given name. Message can either be whole SOAP message or just a payload.
	 * If only payload is passed in, only payloads will be compared, otherwise whole message will be compared.
	 *
	 * @param location of the resource where the message is stored.
	 * @return the request matcher
	 */
	public static ParametrizableRequestMatcher message(String location) {
		Assert.notNull(location, "'location' must not be null");
		return message(fromResource(location));
	}	
	/**
	 * Expects the given {@link Resource} XML message. Message can either be whole SOAP message or just a payload.
	 * If only payload is passed in, only payloads will be compared, otherwise whole message will be compared.
	 *
	 * @param message the XML message
	 * @return the request matcher
	 */
	public static ParametrizableRequestMatcher message(Resource message) {
		Assert.notNull(message, "'message' must not be null");
		Document document = loadDocument(createResourceSource(message));
		return message(document);
	}	
    /**
     * Expects the given {@link Source} XML message. Message can either be whole SOAP message or just a payload.
     * If only payload is passed in, only payloads will be compared, otherwise whole message will be compared.
     *
     * @param message the XML message
     * @return the request matcher
     */
    public static ParametrizableRequestMatcher message(Source message) {
        Assert.notNull(message, "'message' must not be null");
       	Document document = loadDocument(message);
		return message(document);
    }

    /**
     * Expects the given {@link Source} XML message. Message can either be whole SOAP message or just a payload.
     * If only payload is passed in, only payloads will be compared, otherwise whole message will be compared.
     *
     * @param message the XML message
     * @return the request matcher
     */
    public static ParametrizableRequestMatcher message(Document message) {
    	Assert.notNull(message, "'message' must not be null");
    	return new TemplateAwareMessageCompareMatcher(message, Collections.<String,Object>emptyMap(), getTemplateProcessor());
    }	
    
    /**
     * Respond with the given XML loaded from resource as response. If message is SOAP, it will be returned as response, if message is payload, 
     * it will be wrapped into a SOAP.
     *
     * @param loaction of the resource
     * @return the response callback
     */
    public static ParametrizableResponseCreator withMessage(String location) {
    	Assert.notNull(location, "'location' must not be null");
    	return withMessage(fromResource(location));
    }
    /**
     * Respond with the given {@link Source} XML as response. If message is SOAP, it will be returned as response, if message is payload, 
     * it will be wrapped into a SOAP.
     *
     * @param payload the response message
     * @return the response callback
     */
    public static ParametrizableResponseCreator withMessage(Source message) {
        Assert.notNull(message, "'message' must not be null");
        return withMessage(loadDocument(message));
    }
    /**
     * Respond with the given {@link Source} XML as response. If message is SOAP, it will be returned as response, if message is payload, 
     * it will be wrapped into a SOAP.
     *
     * @param payload the response message
     * @return the response callback
     */
    public static ParametrizableResponseCreator withMessage(Document message) {
    	Assert.notNull(message, "'message' must not be null");
    	return new TemplateAwareMessageCreator(message, Collections.<String, Object>emptyMap(), getTemplateProcessor());
    }
}
