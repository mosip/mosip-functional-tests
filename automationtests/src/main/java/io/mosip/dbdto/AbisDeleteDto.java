package io.mosip.dbdto;

public class AbisDeleteDto {
	 private String irisThreshold;

	    private String request_type;

	    private String maxResults;

	    private String encounter_id;

	    private String faceThreshold;

	    private Long tid;

	    private String fingerThreshold;

		@Override
		public String toString() {
			return "AbisDeleteDto [irisThreshold=" + irisThreshold + ", request_type=" + request_type + ", maxResults="
					+ maxResults + ", encounter_id=" + encounter_id + ", faceThreshold=" + faceThreshold + ", tid="
					+ tid + ", fingerThreshold=" + fingerThreshold + "]";
		}

		public String getIrisThreshold() {
			return irisThreshold;
		}

		public void setIrisThreshold(String irisThreshold) {
			this.irisThreshold = irisThreshold;
		}

		public String getRequest_type() {
			return request_type;
		}

		public void setRequest_type(String request_type) {
			this.request_type = request_type;
		}

		public String getMaxResults() {
			return maxResults;
		}

		public void setMaxResults(String maxResults) {
			this.maxResults = maxResults;
		}

		public String getEncounter_id() {
			return encounter_id;
		}

		public void setEncounter_id(String encounter_id) {
			this.encounter_id = encounter_id;
		}

		public String getFaceThreshold() {
			return faceThreshold;
		}

		public void setFaceThreshold(String faceThreshold) {
			this.faceThreshold = faceThreshold;
		}

		public Long getTid() {
			return tid;
		}

		public void setTid(Long tid) {
			this.tid = tid;
		}

		public String getFingerThreshold() {
			return fingerThreshold;
		}

		public void setFingerThreshold(String fingerThreshold) {
			this.fingerThreshold = fingerThreshold;
		}
}
