package io.mosip.testrig.apirig.dataprovider.mds;
import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRCapture extends HttpEntityEnclosingRequestBase{
private static final Logger logger = LoggerFactory.getLogger(HttpRCapture.class);

	    String METHOD_NAME ;

	    public void setMethod(String method) {
	    	METHOD_NAME = method;
	    }
	    public HttpRCapture() {
	        super();
	        METHOD_NAME = "RCAPTURE";
	    }

	    @Override
	    public String getMethod() {
	        return METHOD_NAME;  
	    }

	    public HttpRCapture(final String uri) {
	        super();
	        setURI(URI.create(uri));
	        METHOD_NAME = "RCAPTURE";
	    }

	    public String getName() {
	        return METHOD_NAME;
	    }
}

