package net.srt.framework.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * HTTP工具类
 */
public class HttpUtil {

	public static final String DEFAULT_CHARSET = "UTF-8";

	private static final RequestConfig CONFIG = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(30000).build();

	private static final PoolingHttpClientConnectionManager POOLING_HTTP_CLIENT_CONNECTION_MANAGER = new PoolingHttpClientConnectionManager();
	private static final ConnectionKeepAliveStrategy KEEP_ALIVE_STRATEGY = (response, context) -> {
		HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
		while (it.hasNext()) {
			HeaderElement he = it.nextElement();
			String param = he.getName();
			String value = he.getValue();
			if (value != null && "timeout".equalsIgnoreCase(param)) {
				try {
					return Long.parseLong(value) * 1000;
				} catch (NumberFormatException e) {
					e.printStackTrace();
					break;
				}
			}
		}
		return 30 * 1000;
	};

	static {
		POOLING_HTTP_CLIENT_CONNECTION_MANAGER.setMaxTotal(200);
		POOLING_HTTP_CLIENT_CONNECTION_MANAGER.setDefaultMaxPerRoute(20);
		new Timer(true).schedule(new IdleConnectionMonitor(POOLING_HTTP_CLIENT_CONNECTION_MANAGER), 60000, 60000);
	}

	private static CloseableHttpClient getHttpClient() {
		return HttpClients.custom().setKeepAliveStrategy(KEEP_ALIVE_STRATEGY).setConnectionManager(POOLING_HTTP_CLIENT_CONNECTION_MANAGER).setConnectionManagerShared(true).build();
	}

	public static HttpResponse get(String url, Header... headers) {
		return get(url, (Map<String, Object>) null, headers);
	}

	public static HttpResponse get(String url, int timeout, Header... headers) {
		return get(url, null, timeout, headers);
	}

	public static HttpResponse get(String url, NameValuePair[] nameValuePair, Header... headers) {
		Map<String, Object> paramsMap = new HashMap<>(nameValuePair.length);
		for (NameValuePair t : nameValuePair) {
			paramsMap.put(t.getName(), t.getValue());
		}

		return get(url, paramsMap, headers);
	}

	public static HttpResponse get(String url, Map<String, Object> paramsMap, Header... headers) {
		StringBuilder builder = new StringBuilder();
		if (paramsMap != null && paramsMap.size() > 0) {
			for (Map.Entry<String, Object> param : paramsMap.entrySet()) {
				if (param.getValue() != null) {
					builder.append("&").append(param.getKey()).append("=").append(param.getValue());
				}
			}
			url = url + "?" + builder.toString().substring(1);
		}

		HttpGet method = new HttpGet(url);
		method.setConfig(CONFIG);
		if (null != headers && headers.length > 0) {
			method.setHeaders(headers);
		}

		try (CloseableHttpResponse response = getHttpClient().execute(method)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return new HttpResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, DEFAULT_CHARSET));
			}

			return new HttpResponse(response.getStatusLine().getStatusCode());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static HttpResponse get(String url, Map<String, Object> paramsMap, int timeout, Header... headers) {
		StringBuilder builder = new StringBuilder();
		if (paramsMap != null && paramsMap.size() > 0) {
			for (Map.Entry<String, Object> param : paramsMap.entrySet()) {
				if (param.getValue() != null) {
					builder.append("&").append(param.getKey()).append("=").append(param.getValue());
				}
			}
			url = url + "?" + builder.toString().substring(1);
		}

		HttpGet method = new HttpGet(url);
		RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		method.setConfig(config);
		if (null != headers && headers.length > 0) {
			method.setHeaders(headers);
		}

		try (CloseableHttpResponse response = getHttpClient().execute(method)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return new HttpResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, DEFAULT_CHARSET));
			}

			return new HttpResponse(response.getStatusLine().getStatusCode());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static int head(String url) {
		HttpHead method = new HttpHead(url);
		method.setConfig(CONFIG);

		try (CloseableHttpResponse response = getHttpClient().execute(method)) {
			return response.getStatusLine().getStatusCode();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static HttpResponse post(String url, NameValuePair[] nameValuePair, Header... headers) {
		Map<String, String> paramsMap = new HashMap<>(nameValuePair.length);
		for (NameValuePair t : nameValuePair) {
			paramsMap.put(t.getName(), t.getValue());
		}
		return post(url, paramsMap, headers);
	}

	public static HttpResponse post(String url, Map<String, String> paramsMap, Header... headers) {
		HttpPost method = new HttpPost(url);
		method.setConfig(CONFIG);

		if (paramsMap != null) {
			try {
				List<org.apache.http.NameValuePair> paramList = new ArrayList<>();
				for (Map.Entry<String, String> param : paramsMap.entrySet()) {
					paramList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
				}
				method.setEntity(new UrlEncodedFormEntity(paramList, DEFAULT_CHARSET));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		if (null != headers && headers.length > 0) {
			method.setHeaders(headers);
		}

		try (CloseableHttpResponse response = getHttpClient().execute(method)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return new HttpResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, DEFAULT_CHARSET));
			}

			return new HttpResponse(response.getStatusLine().getStatusCode());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static HttpResponse post(String url, String body, String contentType, Header... headers) {
		StringEntity requestBody = new StringEntity(body, DEFAULT_CHARSET);
		requestBody.setContentType(StringUtils.isNotBlank(contentType) ? contentType : "application/octet-stream");

		HttpPost method = new HttpPost(url);
		method.setConfig(CONFIG);
		method.setEntity(requestBody);
		if (null != headers && headers.length > 0) {
			method.setHeaders(headers);
		}

		try (CloseableHttpResponse response = getHttpClient().execute(method)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return new HttpResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, DEFAULT_CHARSET));
			}

			return new HttpResponse(response.getStatusLine().getStatusCode());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static HttpResponse postFormData(String url, Map<String, String> paramsMap, Header... headers) {
		HttpPost method = new HttpPost(url);
		method.setConfig(CONFIG);

		if (paramsMap != null) {
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			for (Map.Entry<String, String> param : paramsMap.entrySet()) {
				builder.addTextBody(param.getKey(), param.getValue());
			}
			method.setEntity(builder.build());
		}
		if (null != headers && headers.length > 0) {
			method.setHeaders(headers);
		}

		try (CloseableHttpResponse response = getHttpClient().execute(method)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return new HttpResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, DEFAULT_CHARSET));
			}

			return new HttpResponse(response.getStatusLine().getStatusCode());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static HttpResponse put(String url, NameValuePair[] nameValuePair, Header... headers) {
		Map<String, String> paramsMap = new HashMap<>(nameValuePair.length);
		for (NameValuePair t : nameValuePair) {
			paramsMap.put(t.getName(), t.getValue());
		}
		return put(url, paramsMap, headers);
	}

	public static HttpResponse put(String url, Map<String, String> paramsMap, Header... headers) {
		HttpPut method = new HttpPut(url);
		method.setConfig(CONFIG);

		if (paramsMap != null) {
			try {
				List<org.apache.http.NameValuePair> paramList = new ArrayList<>();
				for (Map.Entry<String, String> param : paramsMap.entrySet()) {
					paramList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
				}
				method.setEntity(new UrlEncodedFormEntity(paramList, DEFAULT_CHARSET));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		if (null != headers && headers.length > 0) {
			method.setHeaders(headers);
		}

		try (CloseableHttpResponse response = getHttpClient().execute(method)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return new HttpResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, DEFAULT_CHARSET));
			}

			return new HttpResponse(response.getStatusLine().getStatusCode());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static HttpResponse put(String url, String body, String contentType, Header... headers) {
		StringEntity requestBody = new StringEntity(body, DEFAULT_CHARSET);
		requestBody.setContentType(StringUtils.isNotBlank(contentType) ? contentType : "application/octet-stream");

		HttpPut method = new HttpPut(url);
		method.setConfig(CONFIG);
		method.setEntity(requestBody);
		if (null != headers && headers.length > 0) {
			method.setHeaders(headers);
		}

		try (CloseableHttpResponse response = getHttpClient().execute(method)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return new HttpResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, DEFAULT_CHARSET));
			}

			return new HttpResponse(response.getStatusLine().getStatusCode());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static HttpResponse delete(String url, Header... headers) {
		HttpDelete method = new HttpDelete(url);
		method.setConfig(CONFIG);
		if (null != headers && headers.length > 0) {
			method.setHeaders(headers);
		}

		try (CloseableHttpResponse response = getHttpClient().execute(method)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return new HttpResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, DEFAULT_CHARSET));
			}

			return new HttpResponse(response.getStatusLine().getStatusCode());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static class IdleConnectionMonitor extends TimerTask {

		private final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

		private IdleConnectionMonitor(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
			this.poolingHttpClientConnectionManager = poolingHttpClientConnectionManager;
		}

		@Override
		public void run() {
			// 关闭失效的连接
			poolingHttpClientConnectionManager.closeExpiredConnections();
			// 可选的, 关闭30秒内不活动的连接
			poolingHttpClientConnectionManager.closeIdleConnections(30, TimeUnit.SECONDS);
		}
	}

}
