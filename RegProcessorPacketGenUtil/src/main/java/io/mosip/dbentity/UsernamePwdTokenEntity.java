/**
 * 
 */
package io.mosip.dbentity;

import java.time.LocalDateTime;

/**
 * @author madmin
 *
 */
public class UsernamePwdTokenEntity {

	private UsernamePasswordDto request;

	private String metadata;

	private LocalDateTime requesttime;

	private String id;

	private String version;

	public UsernamePwdTokenEntity() {

	}

	public UsernamePasswordDto getRequest() {
		return request;
	}

	public void setRequest(UsernamePasswordDto request) {
		this.request = request;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public LocalDateTime getRequesttime() {
		return requesttime;
	}

	public void setRequesttime(LocalDateTime requesttime) {
		this.requesttime = requesttime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
