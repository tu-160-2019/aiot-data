package net.srt.framework.common.utils;


/**
 * HTTP响应封装类
 *
 * @see HttpUtil
 */
public class HttpResponse {

	private Integer code;
	private String responseText;
	private String cookie;
	private byte[] entity;

	public HttpResponse() {
	}

	public HttpResponse(Integer code) {
		this.code = code;
	}

	public HttpResponse(String responseText) {
		this.responseText = responseText;
	}

	public HttpResponse(Integer code, String responseText) {
		this.code = code;
		this.responseText = responseText;
	}

	public HttpResponse(Integer code, byte[] entity) {
		this.code = code;
		this.entity = entity;
	}

	public HttpResponse(Integer code, String responseText, String cookie) {
		this.code = code;
		this.responseText = responseText;
		this.cookie = cookie;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public byte[] getEntity() {
		return entity;
	}

	public void setEntity(byte[] entity) {
		this.entity = entity;
	}
}
