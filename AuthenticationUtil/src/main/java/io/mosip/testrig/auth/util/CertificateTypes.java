package io.mosip.testrig.auth.util;

public enum CertificateTypes {

    INTERNAL("ida-internal.cer"), 
    PARTNER("ida-partner.cer"), 
    IDA_FIR("ida-fir.cer");

    private String fileName;

    private CertificateTypes(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }
}
