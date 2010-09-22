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

package net.javacrumbs.smock.client;

import java.util.Map;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.mock.client.RequestMatcher;

/**
 * {@link RequestMatcher} that accepts parameters. 
 * @author Lukas Krecan
 *
 * @param <T>
 */
public interface ParametrizableRequestMatcher<T extends WebServiceMessage> extends RequestMatcher<T> {

	/**
	 * Adds parameter to the {@link RequestMatcher} 
	 * @param name
	 * @param value
	 * @return
	 */
	ParametrizableRequestMatcher<T> withParameter(String name, Object value);

	/**
	 * Adds parameters to the {@link RequestMatcher} 
	 * @param name
	 * @param value
	 * @return
	 */
	ParametrizableRequestMatcher<T> withParameters(Map<String, Object> parameters);
}
