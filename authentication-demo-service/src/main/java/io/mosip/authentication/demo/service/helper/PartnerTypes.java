package io.mosip.authentication.demo.service.helper;

public enum PartnerTypes {

    DEVICE("device"), 
    RELYING_PARTY("rp"), 
    FTM("ftm"),
    EKYC("ekyc"),
    SDK("sdk"),
    ABIS("abis");

    private String filePrepend;

    private PartnerTypes(String filePrepend) {
        this.filePrepend = filePrepend;
    }

    public String getFilePrepend() {
        return this.filePrepend;
    }
}
