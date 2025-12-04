package io.mosip.testrig.apirig.utils;

import org.apache.log4j.Logger;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class RestAssuredPrettyLogger {
	
	private static final Logger apiLogger = Logger.getLogger("API_LOGGER");
	
	public static Filter getMaskingFilter() {
        return new Filter() {
            @Override
            public Response filter(FilterableRequestSpecification req,
                                   FilterableResponseSpecification res,
                                   FilterContext ctx) {

            	apiLogger.info("Request method:\t" + req.getMethod());
            	apiLogger.info("Request URI:\t" + req.getURI());

                // Proxy
            	apiLogger.info("Proxy:\t\t" +
                        (req.getProxySpecification() != null ?
                                req.getProxySpecification().toString() : "<none>"));

                // Request params
            	apiLogger.info("Request params:\t" +
                        (req.getRequestParams().isEmpty() ? "<none>" : req.getRequestParams()));

                // Query params
            	apiLogger.info("Query params:\t" +
                        (req.getQueryParams().isEmpty() ? "<none>" : req.getQueryParams()));

                // Form params
            	apiLogger.info("Form params:\t" +
                        (req.getFormParams().isEmpty() ? "<none>" : req.getFormParams()));

                // Path params
            	apiLogger.info("Path params:\t" +
                        (req.getPathParams().isEmpty() ? "<none>" : req.getPathParams()));

                // Headers
            	apiLogger.info("Headers:\t");
                req.getHeaders().asList()
                        .forEach(h -> apiLogger.info("\t" + h.getName() + "=" + h.getValue()));

                // Cookies (FIXED)
                apiLogger.info("Cookies:\t" +
                        (req.getCookies() == null || req.getCookies().asList().isEmpty()
                                ? "<none>" : req.getCookies()));

                // Multiparts (safe check)
                apiLogger.info("Multiparts:\t" +
                        (req.getMultiPartParams() == null || req.getMultiPartParams().isEmpty()
                                ? "<none>" : req.getMultiPartParams()));

                // Body
                String body = req.getBody() == null ? "" : req.getBody().toString();
                String maskedBody = LogMaskingUtil.maskSensitiveData(body);

                apiLogger.info("Body:");
                apiLogger.info(maskedBody);

                // Execute the actual request
                Response response = ctx.next(req, res);

                apiLogger.info(response.getStatusLine());

                // Response headers
                response.getHeaders().forEach(
                        h -> apiLogger.info(h.getName() + ": " + h.getValue())
                );

                // Response body
                String maskedResponse = LogMaskingUtil.maskSensitiveData(response.asString());
                apiLogger.info("\n" + maskedResponse);

                return response;
            }
        };
    }

}