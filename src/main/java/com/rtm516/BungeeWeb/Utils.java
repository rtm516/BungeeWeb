package com.rtm516.BungeeWeb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;



public class Utils {

	/*

	 Derby - Class org.apache.derby.iapi.util.PropertyUtil

	 Licensed to the Apache Software Foundation (ASF) under one or more
	 contributor license agreements.  See the NOTICE file distributed with
	 this work for additional information regarding copyright ownership.
	 The ASF licenses this file to you under the Apache License, Version 2.0
	 (the "License"); you may not use this file except in compliance with
	 the License.  You may obtain a copy of the License at

	 http://www.apache.org/licenses/LICENSE-2.0

	 Unless required by applicable law or agreed to in writing, software
	 distributed under the License is distributed on an "AS IS" BASIS,
	 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 See the License for the specific language governing permissions and
	 limitations under the License.

	 */
	/**
	 * Array containing the safe characters set as defined by RFC 1738
	 */
	private static BitSet safeCharacters;

	private static final char[] hexadecimal = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	static {
		safeCharacters = new BitSet(256);
		int i;
		// 'lowalpha' rule
		for (i = 'a'; i <= 'z'; i++) {
			safeCharacters.set(i);
		}
		// 'hialpha' rule
		for (i = 'A'; i <= 'Z'; i++) {
			safeCharacters.set(i);
		}
		// 'digit' rule
		for (i = '0'; i <= '9'; i++) {
			safeCharacters.set(i);
		}

		// 'safe' rule
		safeCharacters.set('$');
		safeCharacters.set('-');
		safeCharacters.set('_');
		safeCharacters.set('.');
		safeCharacters.set('+');

		// 'extra' rule
		safeCharacters.set('!');
		safeCharacters.set('*');
		safeCharacters.set('\'');
		safeCharacters.set('(');
		safeCharacters.set(')');
		safeCharacters.set(',');

		// special characters common to http: file: and ftp: URLs ('fsegment' and
		// 'hsegment' rules)
		safeCharacters.set('/');
		safeCharacters.set(':');
		safeCharacters.set('@');
		safeCharacters.set('&');
		safeCharacters.set('=');
	}

	/**
	 * Encode a path as required by the URL specification
	 * (<a href="http://www.ietf.org/rfc/rfc1738.txt"> RFC 1738</a>). This differs
	 * from <code>java.net.URLEncoder.encode()</code> which encodes according to the
	 * <code>x-www-form-urlencoded</code> MIME format.
	 *
	 * @param path the path to encode
	 * @return the encoded path
	 */
	public static String encodePath(String path) {
		// stolen from org.apache.catalina.servlets.DefaultServlet ;)

		/**
		 * Note: Here, ' ' should be encoded as "%20" and '/' shouldn't be encoded.
		 */

		int maxBytesPerChar = 10;
		StringBuffer rewrittenPath = new StringBuffer(path.length());
		ByteArrayOutputStream buf = new ByteArrayOutputStream(maxBytesPerChar);
		OutputStreamWriter writer;
		try {
			writer = new OutputStreamWriter(buf, "UTF8");
		} catch (Exception e) {
			e.printStackTrace();
			writer = new OutputStreamWriter(buf);
		}

		for (int i = 0; i < path.length(); i++) {
			int c = path.charAt(i);
			if (safeCharacters.get(c)) {
				rewrittenPath.append((char) c);
			} else {
				// convert to external encoding before hex conversion
				try {
					writer.write(c);
					writer.flush();
				} catch (IOException e) {
					buf.reset();
					continue;
				}
				byte[] ba = buf.toByteArray();
				for (int j = 0; j < ba.length; j++) {
					// Converting each byte in the buffer
					byte toEncode = ba[j];
					rewrittenPath.append('%');
					int low = (toEncode & 0x0f);
					int high = ((toEncode & 0xf0) >> 4);
					rewrittenPath.append(hexadecimal[high]);
					rewrittenPath.append(hexadecimal[low]);
				}
				buf.reset();
			}
		}
		return rewrittenPath.toString();
	}
	
	
	
	
	public static String getFileExtension(File file) {
        String extension = "";
 
        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                extension = name.substring(name.lastIndexOf("."));
            }
        } catch (Exception e) {
            extension = "";
        }
 
        return extension; 
	}

}
