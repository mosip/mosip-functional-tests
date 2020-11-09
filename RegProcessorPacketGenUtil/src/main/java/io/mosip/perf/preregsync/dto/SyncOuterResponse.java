package io.mosip.perf.preregsync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncOuterResponse {

	PreRegArchiveDTO response;

	String responsetime;
	String id;
	String version;
	Object errors;

}
