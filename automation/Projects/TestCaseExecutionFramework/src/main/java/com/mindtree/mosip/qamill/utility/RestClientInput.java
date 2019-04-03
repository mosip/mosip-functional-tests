package com.mindtree.mosip.qamill.utility;

import java.io.File;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RestClientInput {
	String url;
	
	String requestMethod;
	Map<String, String> params;
	Map<String, String> headers;
	
	Boolean isRequestBodyFileAttachment;
	String requestBody;
	
	String fieldName;
	String filePath;
	String mimeType;
	
	
	
	public RestClientInput(String url, String requestMethod, Map<String, String> params, Map<String, String> headers,
			Boolean isRequestBodyFileAttachment, String requestBody, String fieldName, String filePath,
			String mimeType) {
		this.url = url;
		this.requestMethod = requestMethod;
		this.params = params;
		this.headers = headers;
		this.isRequestBodyFileAttachment = isRequestBodyFileAttachment;
		this.requestBody = requestBody;
		this.fieldName = fieldName;
		this.filePath = filePath;
		this.mimeType = mimeType;
	}
	
	public RestClientInput() {
		this.isRequestBodyFileAttachment = false;
		this.requestBody = null;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRequestBody() {
		return requestBody;
	}
	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	public String getRequestMethod() {
		return requestMethod;
	}
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}
	public Boolean getIsRequestBodyFileAttachment() {
		return isRequestBodyFileAttachment;
	}
	public void setIsRequestBodyFileAttachment(Boolean isRequestBodyFileAttachment) {
		this.isRequestBodyFileAttachment = isRequestBodyFileAttachment;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}	
}
