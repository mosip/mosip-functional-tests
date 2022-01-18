package io.mosip.device.register.dto;

import java.io.Serializable;

public class RequestBuilder<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public T request;

	public String metadata;

	public String requesttime;

	public String id;

	public String version;

	public RequestBuilder(T request, String metadata, String requesttime, String id, String version) {
		super();
		this.request = request;
		this.metadata = metadata;
		this.requesttime = requesttime;
		this.id = id;
		this.version = version;
	}
}
