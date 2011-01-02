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

package net.javacrumbs.smock.http.client.connection;

import org.springframework.context.ApplicationContext;
import org.springframework.ws.server.EndpointInterceptor;

import net.javacrumbs.smock.common.client.CommonSmockClient;
import net.javacrumbs.smock.http.client.connection.threadlocal.http.ThreadLocalMockWebServiceServer;

public class SmockClient extends CommonSmockClient {

	public static MockWebServiceServer createServer(ApplicationContext applicationContext)
	{
		return createServer(applicationContext, null);
	}
	public static MockWebServiceServer createServer(ApplicationContext applicationContext, EndpointInterceptor[] interceptors)
	{
		return new ThreadLocalMockWebServiceServer(applicationContext, interceptors);
	}
}
