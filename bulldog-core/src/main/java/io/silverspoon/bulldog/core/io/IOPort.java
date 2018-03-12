/*******************************************************************************
 * Copyright (c) 2016 Silverspoon.io (silverspoon@silverware.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.silverspoon.bulldog.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IOPort {

   String getName();

   String getAlias();

   void setAlias(String alias);

   void open() throws IOException;

   boolean isOpen();

   void close() throws IOException;

   void writeByte(int b) throws IOException;

   void writeBytes(byte[] bytes) throws IOException;

   void writeString(String string) throws IOException;

   byte readByte() throws IOException;

   int readBytes(byte[] buffer) throws IOException;

   String readString() throws IOException;

   OutputStream getOutputStream() throws IOException;

   InputStream getInputStream() throws IOException;

}
