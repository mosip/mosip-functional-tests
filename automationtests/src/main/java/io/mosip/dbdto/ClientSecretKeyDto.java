package io.mosip.dbdto;

public class ClientSecretKeyDto {

	private String id;
	private Object metadata;
	private ClientSecretKeyRequestDto request;
	private String requesttime;
	private String version;

	public ClientSecretKeyDto() {
	}

	public ClientSecretKeyDto(String id, Object metadata, ClientSecretKeyRequestDto request, String requesttime,
			String version) {
		super();
		this.id = id;
		this.metadata = metadata;
		this.request = request;
		this.requesttime = requesttime;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getMetadata() {
		return metadata;
	}

	public void setMetadata(Object metadata) {
		this.metadata = metadata;
	}

	public ClientSecretKeyRequestDto getRequest() {
		return request;
	}

	public void setRequest(ClientSecretKeyRequestDto request) {
		this.request = request;
	}

	public String getRequesttime() {
		return requesttime;
	}

	public void setRequesttime(String requesttime) {
		this.requesttime = requesttime;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
