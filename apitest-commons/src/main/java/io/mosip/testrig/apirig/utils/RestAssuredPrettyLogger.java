package io.mosip.testrig.apirig.utils;

import org.apache.log4j.Logger;
import org.testng.Reporter;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class RestAssuredPrettyLogger {
	
	private static final Logger apiLogger = Logger.getLogger("API_LOGGER");
	private static final int REPORT_BODY_MAX_CHARS = 8192;
	private static final ThreadLocal<Boolean> DSL_REPORT_CAPTURE = ThreadLocal.withInitial(() -> false);

	/**
	 * Enables compact request/response logging to the current TestNG result for
	 * this thread. Console logging remains unchanged.
	 */
	public static void startDslReportCapture() {
		DSL_REPORT_CAPTURE.set(true);
	}

	public static void stopDslReportCapture() {
		DSL_REPORT_CAPTURE.remove();
	}
	
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
                        (req.getRequestParams().isEmpty() ? "<none>" : LogMaskingUtil.maskParams(req.getRequestParams())));

                // Query params
            	apiLogger.info("Query params:\t" +
                        (req.getQueryParams().isEmpty() ? "<none>" : LogMaskingUtil.maskParams(req.getQueryParams())));

                // Form params
            	apiLogger.info("Form params:\t" +
                        (req.getFormParams().isEmpty() ? "<none>" : LogMaskingUtil.maskParams(req.getFormParams())));

                // Path params
            	apiLogger.info("Path params:\t" +
                        (req.getPathParams().isEmpty() ? "<none>" : LogMaskingUtil.maskParams(req.getPathParams())));

                // Headers
            	apiLogger.info("Headers:\t");
                req.getHeaders().asList()
                        .forEach(h -> apiLogger.info("\t" + h.getName() + "=" + LogMaskingUtil.maskSensitiveData(h.getName(), h.getValue())));
                
                // Cookies
                String cookiesLog = (req.getCookies() == null || req.getCookies().asList().isEmpty()) 
                        ? "<none>" 
                        : req.getCookies().asList().stream()
                            .map(c -> c.getName() + "=" + LogMaskingUtil.maskSensitiveData(c.getName(), c.getValue()))
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("<none>");

                apiLogger.info("Cookies:\t" + cookiesLog);
                
                // Multiparts
                if (req.getMultiPartParams() == null || req.getMultiPartParams().isEmpty()) {
                    apiLogger.info("Multiparts:\t" + "<none>");
                } else {
                    StringBuilder mpLog = new StringBuilder();
                    req.getMultiPartParams().forEach(mp -> {
                        String key = mp.getControlName();   // field name
                        Object value = mp.getContent();     // value

                        // Pass key and value to the masking method
                        String maskedValue = LogMaskingUtil.maskSensitiveData(key, String.valueOf(value));

                        if (mpLog.length() > 0) {
                            mpLog.append(", ");
                        }
                        mpLog.append(key).append("=").append(maskedValue);
                    });

                    apiLogger.info("Multiparts:\t" + mpLog);
                }

                // Body
                String body = req.getBody() == null ? "" : req.getBody().toString();
                String maskedBody = LogMaskingUtil.maskSensitiveData(body);

                apiLogger.info("Body:");
                apiLogger.info(maskedBody);

                // Execute the actual request
                long startTime = System.currentTimeMillis();
                Response response;
                try {
                    response = ctx.next(req, res);
                } catch (RuntimeException e) {
                    reportToDsl(req.getMethod(), req.getURI(), null,
                            System.currentTimeMillis() - startTime, e);
                    throw e;
                }

                apiLogger.info(response.getStatusLine());

                // Response headers
                response.getHeaders().forEach(
                        h -> apiLogger.info(h.getName() + ": " + LogMaskingUtil.maskSensitiveData(h.getName(), h.getValue()))
                );

                // Response body
                String maskedResponse = LogMaskingUtil.maskSensitiveData(response.asString());
                apiLogger.info("\n" + maskedResponse);
                reportToDsl(req.getMethod(), req.getURI(), response,
                        System.currentTimeMillis() - startTime, null);

                return response;
            }
        };
    }

	private static void reportToDsl(String method, String uri, Response response, long durationMs,
			RuntimeException failure) {
		if (!DSL_REPORT_CAPTURE.get()) {
			return;
		}

		StringBuilder report = new StringBuilder();
		report.append("<div class='dsl-internal-api' style='border:1px solid #aaa;padding:6px;margin:4px 0;'>")
				.append("<b>Internal API: ").append(escapeHtml(method)).append(" ")
				.append(escapeHtml(uri)).append("</b><br>");

		if (response != null) {
			int statusCode = response.getStatusCode();
			report.append("Status: <span style='color:")
					.append(statusCode >= 200 && statusCode < 300 ? "green" : "red")
					.append("'>").append(statusCode).append("</span><br>")
					.append("Duration: ").append(durationMs).append(" ms")
					.append("<pre style='white-space:pre-wrap;'>")
					.append(escapeHtml(limit(LogMaskingUtil.maskSensitiveData(response.asString()))))
					.append("</pre>");
		} else {
			report.append("<span style='color:red;font-weight:bold;'>Request failed after ")
					.append(durationMs).append(" ms: ")
					.append(escapeHtml(rootCauseMessage(failure))).append("</span>");
		}
		report.append("</div>");
		Reporter.log(report.toString(), true);
	}

	private static String limit(String value) {
		if (value == null) {
			return "";
		}
		return value.length() <= REPORT_BODY_MAX_CHARS ? value
				: value.substring(0, REPORT_BODY_MAX_CHARS) + "\n... response truncated ...";
	}

	private static String rootCauseMessage(Throwable throwable) {
		Throwable root = throwable;
		while (root != null && root.getCause() != null) {
			root = root.getCause();
		}
		if (root == null) {
			return "Unknown error";
		}
		return root.getMessage() != null ? root.getMessage() : root.getClass().getSimpleName();
	}

	private static String escapeHtml(String value) {
		if (value == null) {
			return "";
		}
		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&#39;");
	}

}