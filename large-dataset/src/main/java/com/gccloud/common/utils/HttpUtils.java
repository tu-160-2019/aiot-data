/*
 * Copyright 2023 http://gcpaas.gccloud.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gccloud.common.utils;

import com.gccloud.common.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.Request.Builder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
/**
 * http工具类
 */

/**
 * 封装http调用，使用okHttp
 *
 * @author xiaoka
 */
@Slf4j
public class HttpUtils {

    private static SSLContext SSL_CONTEXT = null;

    private static final String DEFAULT_CONTENT_TYPE = "application/json;charset=UTF-8";

    /**
     * 默认读取超时时间、单位秒
     */
    private static final int READ_TIME_OUT = 30;
    /**
     * 默认连接超时时间、单位秒
     */
    private static final int CONNECT_TIME_OUT = 30;

    public static final X509TrustManager X509_TRUST_MANAGER = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            X509Certificate[] x509Certificates = new X509Certificate[0];
            return x509Certificates;
        }
    };

    private static final HostnameVerifier HOSTNAME_VERIFIER = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * 添加header
     *
     * @param builder
     * @param header
     */
    private static void addHeader(Request.Builder builder, Map<String, String> header) {
        // 添加header
        if (header != null && header.size() > 0) {
            Set<Entry<String, String>> entrySet = header.entrySet();
            for (Entry<String, String> entry : entrySet) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    static {
        try {
            SSL_CONTEXT = SSLContext.getInstance("SSL");
            SSL_CONTEXT.init(null, new TrustManager[]{X509_TRUST_MANAGER}, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建builder
     *
     * @param url
     * @param header
     * @return
     */
    private static Request.Builder createBuilder(String url, Map<String, String> header) {
        Request.Builder builder = new Request.Builder().url(url);
        addHeader(builder, header);
        return builder;
    }

    /**
     * 创建客户端
     *
     * @param url
     * @param readTimeOut    读取超时，单位秒
     * @param connectTimeOut 连接超时，单位秒
     * @return
     */
    private static OkHttpClient createClient(String url, int readTimeOut, int connectTimeOut) {
        if (url.startsWith("https")) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(readTimeOut, TimeUnit.SECONDS)
                    .connectTimeout(connectTimeOut, TimeUnit.SECONDS)
                    .sslSocketFactory(SSL_CONTEXT.getSocketFactory(), X509_TRUST_MANAGER)
                    .hostnameVerifier(HOSTNAME_VERIFIER).build();
            return client;
        }
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(readTimeOut, TimeUnit.SECONDS).connectTimeout(connectTimeOut, TimeUnit.SECONDS).build();
        return client;
    }

    /**
     * post请求、支持https
     *
     * @param url
     * @param contentType
     * @param header
     * @param body
     * @return
     */
    public static Response post(String url, String contentType, Map<String, String> header, String body) {
        MediaType type = MediaType.parse(StringUtils.isBlank(contentType) ? DEFAULT_CONTENT_TYPE : contentType);
        return post(url, type, header, body);
    }

    public static Response post(String url, String contentType, int readTimeOut, int connectTimeOut, Map<String, String> header, String body) {
        MediaType type = MediaType.parse(StringUtils.isBlank(contentType) ? DEFAULT_CONTENT_TYPE : contentType);
        return post(url, type, readTimeOut, connectTimeOut, header, body);
    }

    public static Response post(String url, MediaType mediaType, Map<String, String> header, String body) {
        return post(url, mediaType, READ_TIME_OUT, CONNECT_TIME_OUT, header, body);
    }

    public static Response post(String url, MediaType mediaType, int readTimeOut, int connectTimeOut, Map<String, String> header, String body) {
        try {
            OkHttpClient client = createClient(url, readTimeOut, connectTimeOut);
            Builder builder = createBuilder(url, header);
            Request request = builder.post(RequestBody.create(mediaType, body)).build();
            Response response = client.newCall(request).execute();
            return response;
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new GlobalException("发送请求失败: " + url);
        }
    }

    public static void postAsync(String url, String contentType, Map<String, String> header, String body) {
        MediaType type = MediaType.parse(StringUtils.isBlank(contentType) ? DEFAULT_CONTENT_TYPE : contentType);
        postAsync(url, type, header, body);
    }

    public static void postAsync(String url, String contentType, int readTimeOut, int connectTimeOut, Map<String, String> header, String body) {
        MediaType type = MediaType.parse(StringUtils.isBlank(contentType) ? DEFAULT_CONTENT_TYPE : contentType);
        postAsync(url, type, readTimeOut, connectTimeOut, header, body);
    }

    public static void postAsync(String url, MediaType mediaType, Map<String, String> header, String body) {
        postAsync(url, mediaType, READ_TIME_OUT, CONNECT_TIME_OUT, header, body);
    }

    public static void postAsync(String url, MediaType mediaType, int readTimeOut, int connectTimeOut, Map<String, String> header, String body) {
        try {
            OkHttpClient client = createClient(url, readTimeOut, connectTimeOut);
            Builder builder = createBuilder(url, header);
            Request request = builder.post(RequestBody.create(mediaType, body)).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // 不处理
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // 不处理
                }
            });
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new GlobalException("发送请求失败: " + url);
        }
    }


    public static Response get(String url, int readTimeOut, int connectTimeOut, Map<String, String> header) {
        try {
            OkHttpClient client = createClient(url, readTimeOut, connectTimeOut);
            Builder builder = new Request.Builder().url(url);
            addHeader(builder, header);
            Request request = builder.build();
            return client.newCall(request).execute();
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new GlobalException("发送请求失败: " + url);
        }
    }

    /**
     * get请求，支持https
     *
     * @param url
     * @param header
     * @return
     */
    public static Response get(String url, Map<String, String> header) {
        return get(url, READ_TIME_OUT, CONNECT_TIME_OUT, header);
    }

    public static void getAsync(String url, int readTimeOut, int connectTimeOut, Map<String, String> header) {
        try {
            OkHttpClient client = createClient(url, readTimeOut, connectTimeOut);
            Builder builder = new Request.Builder().url(url);
            addHeader(builder, header);
            Request request = builder.build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // 不处理
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // 不处理
                }
            });
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new GlobalException("发送请求失败: " + url);
        }
    }

    /**
     * get请求、异步请求
     *
     * @param url
     * @param header
     */
    public static void getAsync(String url, Map<String, String> header) {
        getAsync(url, READ_TIME_OUT, CONNECT_TIME_OUT, header);
    }
}