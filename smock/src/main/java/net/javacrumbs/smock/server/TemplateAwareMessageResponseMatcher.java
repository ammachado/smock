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

package net.javacrumbs.smock.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;

import net.javacrumbs.smock.common.TemplateProcessor;

import org.springframework.util.Assert;
import org.springframework.ws.WebServiceMessage;
import org.w3c.dom.Document;

public class TemplateAwareMessageResponseMatcher extends MessageResponseMatcher implements ParametrizableResponseMatcher {

	private final Map<String, Object> parameters;
	
	private final TemplateProcessor templateProcessor;
	
	public TemplateAwareMessageResponseMatcher(Document controlMessage, Map<String, Object> parameters, TemplateProcessor templateProcessor) {
		super(controlMessage);
		Assert.notNull(templateProcessor,"TemplateProcessor can not be null");
		this.parameters = Collections.unmodifiableMap(new HashMap<String, Object>(parameters));
		this.templateProcessor = templateProcessor;
	}
	
	@Override
	protected Document preprocessControlMessage(WebServiceMessage input)
	{
		Source inputSource = input!=null?input.getPayloadSource():null;
		return templateProcessor.processTemplate(getControlMessage(), inputSource, parameters);
	}

	public TemplateAwareMessageResponseMatcher withParameter(String name, Object value) {
		return withParameters(Collections.singletonMap(name, value));
	}

	public TemplateAwareMessageResponseMatcher withParameters(Map<String, Object> additionalParameters) {
		Map<String, Object> newParameters = new HashMap<String, Object>(parameters);
		newParameters.putAll(additionalParameters);
		return new TemplateAwareMessageResponseMatcher(getControlMessage(), newParameters, templateProcessor);
	}

	Map<String, Object> getParameters() {
		return parameters;
	}


}
