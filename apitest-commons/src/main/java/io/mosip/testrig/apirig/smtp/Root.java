package io.mosip.testrig.apirig.smtp;

import java.util.Date;
import java.util.List;

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