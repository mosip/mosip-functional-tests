package com.mindtree.mosip.qamill.utility;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.ObjectInputStream.GetField;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class RestClient {

	public RestClientOutput makeRestCall(RestClientInput restClientInput) throws Exception {
		System.out.println("Entering RestClient::makeRestCall");
		RestClientOutput restClientOutput = null;
		if (null == restClientInput) {
			return null;
		}
		if (!((true == restClientInput.getRequestMethod().equals("GET"))
				|| (true == restClientInput.getRequestMethod().equals("POST"))
				|| (true == restClientInput.getRequestMethod().equals("PATCH"))
				|| (true == restClientInput.getRequestMethod().equals("PUT"))
				|| (true == restClientInput.getRequestMethod().equals("DELETE")))) {
			return null;
		}

		if (true == restClientInput.getUrl().startsWith("http:")) {
			restClientOutput = makeHttpRestCall(restClientInput);
		} else if (true == restClientInput.getUrl().startsWith("https:")) {
			restClientOutput = makeHttpsRestCall(restClientInput);
		}
		System.out.println("Leaving RestClient::makeRestCall");
		return restClientOutput;
	}
	
	public RestClientOutput makeHttpRestCall(RestClientInput restClientInput) throws Exception {
		System.out.println("Entering RestClient::makeHttpRestCall");
		if (null == restClientInput) {
			return null;
		}

		String url = restClientInput.getUrl();
		int responseCode = 0;
		String responseBody = null;
		Map<String, List<String>> responseHeaders = null;

		if ((null != restClientInput.getParams()) && (restClientInput.getParams().size() > 0)) {
			url = url + "?";
			for (Map.Entry<String, String> entry : restClientInput.getParams().entrySet()) {
				url += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
			}
			url = url.substring(0, url.length() - 1); // Remove last comma (,)
		}

		//System.out.println("url2: " + url);
		URL httpUrl = null;
		try {
			httpUrl = new URL(url);
			HttpURLConnection httpcon = (HttpURLConnection) httpUrl.openConnection();
			httpcon.setRequestMethod(restClientInput.getRequestMethod());

			for (Map.Entry<String, String> entry : restClientInput.getHeaders().entrySet()) {
				httpcon.setRequestProperty(entry.getKey(), entry.getValue());
			}

			if ((true == restClientInput.getRequestMethod().equals("POST"))
					|| (true == restClientInput.getRequestMethod().equals("PATCH"))
					|| (true == restClientInput.getRequestMethod().equals("PUT"))
					|| (true == restClientInput.getRequestMethod().equals("DELETE"))) {
				if (false == restClientInput.getIsRequestBodyFileAttachment()) {
					if (null != restClientInput.getRequestBody()
							&& false == restClientInput.getRequestBody().isEmpty()) {
						httpcon.setDoOutput(true);

						byte[] outputInBytes = (restClientInput.getRequestBody()).getBytes("UTF-8");
						OutputStream os = null;
						os = httpcon.getOutputStream();
						os.write(outputInBytes);
						os.flush();
						os.close();
					}

				} else {
					httpcon.setDoOutput(true);

					String twoHyphens = "--";
					String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
					String lineEnd = "\r\n";

					int bytesRead, bytesAvailable, bufferSize;
					byte[] buffer;
					int maxBufferSize = 1 * 1024 * 1024;

					String[] q = restClientInput.getFilePath().split("/");
					int idx = q.length - 1;

					File file = new File(restClientInput.getFilePath());
					FileInputStream fileInputStream = new FileInputStream(file);

					httpcon.setUseCaches(false);
					httpcon.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

					// write filecontent
					DataOutputStream outputStream = new DataOutputStream(httpcon.getOutputStream());
					outputStream.writeBytes(twoHyphens + boundary + lineEnd);
					outputStream.writeBytes("Content-Disposition: form-data; name=\"" + restClientInput.getFieldName()
							+ "\"; filename=\"" + q[idx] + "\"" + lineEnd);
					outputStream.writeBytes("Content-Type: " + restClientInput.getMimeType() + lineEnd);
					outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

					outputStream.writeBytes(lineEnd);

					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					buffer = new byte[bufferSize];

					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
					while (bytesRead > 0) {
						outputStream.write(buffer, 0, bufferSize);
						bytesAvailable = fileInputStream.available();
						bufferSize = Math.min(bytesAvailable, maxBufferSize);
						bytesRead = fileInputStream.read(buffer, 0, bufferSize);
					}

					outputStream.writeBytes(lineEnd);
					outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
				}
			}

			responseCode = httpcon.getResponseCode();
			//System.out.println("responseCode: " + responseCode);
			BufferedReader reader = null;
			StringBuilder buf = null;

			if (responseCode == HttpURLConnection.HTTP_OK) {
				reader = new BufferedReader(new InputStreamReader(httpcon.getInputStream(), "UTF-8"));
			} else {
				reader = new BufferedReader(new InputStreamReader(httpcon.getErrorStream(), "UTF-8"));
			}
			//System.out.println("reader: " + reader);
			if (null != reader) {
				buf = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					buf.append(line);
				}
			}
			responseBody = buf.toString();
			//System.out.println("buffer: " + responseBody);

			responseHeaders = httpcon.getHeaderFields();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RestClientOutput restClientOutput = new RestClientOutput();
		restClientOutput.setResponseCode(responseCode);
		restClientOutput.setResponseBody(responseBody);
		restClientOutput.setHeaders(responseHeaders);
		System.out.println("Leaving RestClient::makeHttpRestCall");
		return restClientOutput;
	}

	public RestClientOutput makeHttpsRestCall(RestClientInput restClientInput) throws Exception {
		System.out.println("Entering RestClient::makeHttpsRestCall");
		if (null == restClientInput) {
			return null;
		}
		int responseCode = 0;
		String responseBody = null;
		Map<String, List<String>> responseHeaders = null;

		String url = restClientInput.getUrl();
		if ((null != restClientInput.getParams()) && (restClientInput.getParams().size() > 0)) {
			url = url + "?";
			for (Map.Entry<String, String> entry : restClientInput.getParams().entrySet()) {
				url += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
			}
			url = url.substring(0, url.length() - 1); // Remove last comma (,)
		}

		// IGNORE SSL CERTIFICATE FOR HTTPS
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			System.err.println();
		}

		// END OF -- IGNORE SSL CERTIFICATE FOR HTTPS

		try {
			URL httpUrl = new URL(url);
			HttpsURLConnection httpcon = (HttpsURLConnection) httpUrl.openConnection();
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			httpcon.setSSLSocketFactory(sslsocketfactory);

			httpcon.setRequestMethod(restClientInput.getRequestMethod());

			if ( (null != restClientInput.getHeaders()) && (restClientInput.getHeaders().size() > 0) ) {
				for (Map.Entry<String, String> entry : restClientInput.getHeaders().entrySet()) {
					httpcon.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}

			if ((true == restClientInput.getRequestMethod().equals("POST"))
					|| (true == restClientInput.getRequestMethod().equals("PATCH"))
					|| (true == restClientInput.getRequestMethod().equals("PUT"))
					|| (true == restClientInput.getRequestMethod().equals("DELETE"))) {
				if (false == restClientInput.getIsRequestBodyFileAttachment()) {
					if (null != restClientInput.getRequestBody()
							&& false == restClientInput.getRequestBody().isEmpty()) {
						httpcon.setDoOutput(true);

						byte[] outputInBytes = (restClientInput.getRequestBody()).getBytes("UTF-8");
						OutputStream os = null;
						os = httpcon.getOutputStream();
						os.write(outputInBytes);
						os.flush();
						os.close();
					}

				} else {
					httpcon.setDoOutput(true);

					String twoHyphens = "--";
					String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
					String lineEnd = "\r\n";

					int bytesRead, bytesAvailable, bufferSize;
					byte[] buffer;
					int maxBufferSize = 1 * 1024 * 1024;

					String[] q = restClientInput.getFilePath().split("/");
					int idx = q.length - 1;

					File file = new File(restClientInput.getFilePath());
					FileInputStream fileInputStream = new FileInputStream(file);

					httpcon.setUseCaches(false);
					httpcon.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

					// write filecontent
					DataOutputStream outputStream = new DataOutputStream(httpcon.getOutputStream());
					outputStream.writeBytes(twoHyphens + boundary + lineEnd);
					outputStream.writeBytes("Content-Disposition: form-data; name=\"" + restClientInput.getFieldName()
							+ "\"; filename=\"" + q[idx] + "\"" + lineEnd);
					outputStream.writeBytes("Content-Type: " + restClientInput.getMimeType() + lineEnd);
					outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

					outputStream.writeBytes(lineEnd);

					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					buffer = new byte[bufferSize];

					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
					while (bytesRead > 0) {
						outputStream.write(buffer, 0, bufferSize);
						bytesAvailable = fileInputStream.available();
						bufferSize = Math.min(bytesAvailable, maxBufferSize);
						bytesRead = fileInputStream.read(buffer, 0, bufferSize);
					}

					outputStream.writeBytes(lineEnd);
					outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
				}
			}

			responseCode = httpcon.getResponseCode();
			//System.out.println("responseCode: " + responseCode);
			BufferedReader reader = null;
			StringBuilder buf = null;

			if (responseCode == HttpURLConnection.HTTP_OK) {
				reader = new BufferedReader(new InputStreamReader(httpcon.getInputStream(), "UTF-8"));
			} else {
				reader = new BufferedReader(new InputStreamReader(httpcon.getErrorStream(), "UTF-8"));
			}
			//System.out.println("reader: " + reader);
			if (null != reader) {
				buf = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					buf.append(line);
				}
			}
			responseBody = buf.toString();
			System.out.println("buffer: " + responseBody);

			responseHeaders = httpcon.getHeaderFields();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RestClientOutput restClientOutput = new RestClientOutput();
		restClientOutput.setResponseCode(responseCode);
		restClientOutput.setResponseBody(responseBody);
		restClientOutput.setHeaders(responseHeaders);
		System.out.println("Leaving RestClient::makeHttpsRestCall");
		return restClientOutput;
	}
}
