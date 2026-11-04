package io.mosip.testrig.apirig.otp;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Root{
	 public List<Object> attachments;
	 public Headers headers;
	 public List<HeaderLine> headerLines;
	 public String html;
	 public String subject;
	 public Date date;
	 public To to;
	 public From from;
	 public Cc cc;
	 public String messageId;
	 public String type;
	 public String text;
	}