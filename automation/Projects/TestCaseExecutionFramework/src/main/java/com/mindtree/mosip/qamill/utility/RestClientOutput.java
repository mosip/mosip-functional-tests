package com.mindtree.mosip.qamill.utility;

import java.util.List;
import java.util.Map;

public class RestClientOutput {
	int responseCode;
	String responseBody;
	Map<String, List<String>> headers;

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getHeader(String key) {
		String value = null;
		List<String> values = null;
		if ( (null != headers) && (0 < headers.size()) ) {
			values = headers.get(key);
			if ((null != values) && (0 < values.size())) {
				value = values.get(0);
			}
		}
		return value;
	}

}
