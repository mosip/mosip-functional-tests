package io.mosip.dbdto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class CryptomanagerRequestDto {
	/**
	 * Application id of decrypting module
	 */

	private String applicationId;
	/**
	 * Refrence Id
	 */

	private String referenceId;
	/**
	 * Timestamp
	 */

	private LocalDateTime timeStamp;
	/**
	 * Data in BASE64 encoding to encrypt/decrypt
	 */

	private String data;

	/**
	 * salt in BASE64 encoding for encrypt/decrypt
	 */

	private String salt;

	/**
	 * aad in BASE64 encoding for encrypt/decrypt
	 */

	private String aad;

	public CryptomanagerRequestDto() {
	}

	public CryptomanagerRequestDto(String applicationId, String referenceId, LocalDateTime timeStamp, String data,
			String salt, String aad) {
		this.applicationId = applicationId;
		this.referenceId = referenceId;
		this.timeStamp = timeStamp;
		this.data = data;
		this.salt = salt;
		this.aad = aad;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getAad() {
		return aad;
	}

	public void setAad(String aad) {
		this.aad = aad;
	}

	@Override
	public String toString() {
		return "CryptomanagerRequestDto [applicationId=" + applicationId + ", referenceId=" + referenceId
				+ ", timeStamp=" + timeStamp + ", data=" + data + ", salt=" + salt + ", aad=" + aad + "]";
	}

}
