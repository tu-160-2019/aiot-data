/**
 * Copyright [2019-Present] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.gitee.starblues.loader.utils;

import java.io.*;
import java.util.function.Consumer;

/**
 * io utils
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.0.0
 */
public class IOUtils {

    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        if(inputStream == null){
            throw new IllegalArgumentException("参数inputStream不能为空");
        }
        if(outputStream == null){
            throw new IllegalArgumentException("参数inputStream不能为空");
        }
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        byte[] arr = new byte[1024];
        int len ;
        while ((len = bufferedInputStream.read(arr)) != -1) {
            bufferedOutputStream.write(arr, 0, len);
        }
        bufferedOutputStream.flush();
    }

    public static byte[] read(InputStream inputStream) throws IOException {
        if(inputStream == null){
            throw new IllegalArgumentException("参数inputStream不能为空");
        }
        if(!(inputStream instanceof BufferedInputStream)){
            inputStream = new BufferedInputStream(inputStream);
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            int len = 0;
            byte[] buffer = new byte[1024];
            while (-1 != (len = inputStream.read(buffer))) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
            return outputStream.toByteArray();
        }
    }

    public static void closeQuietly(final AutoCloseable closeable) {
        closeQuietly(closeable, null);
    }

    public static void closeQuietly(final AutoCloseable closeable, final Consumer<Exception> consumer) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final Exception e) {
                if (consumer != null) {
                    consumer.accept(e);
                }
            }
        }
    }

}
