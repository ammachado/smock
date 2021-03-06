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
package net.javacrumbs.smock.http.client.connection.threadlocal.http;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * URLStreamHandler implementation that creates ThreadLocalMockHttpUrlConnection.
 * @author Lukas Krecan
 */
public class Handler extends URLStreamHandler {
	
	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		return openConnection(u, null);
	}
	
	@Override
	protected URLConnection openConnection(URL u, Proxy p) throws IOException {
		return new ThreadLocalMockHttpUrlConnection(u);
	}

}
