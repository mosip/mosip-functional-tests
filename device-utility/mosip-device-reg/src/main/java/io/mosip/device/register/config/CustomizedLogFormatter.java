package io.mosip.device.register.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomizedLogFormatter extends Formatter {

		private static final String PATTERN = "yyyy-MM-dd'@'HH:mm:ss:";


	@Override
	public String format(LogRecord record) {
		return String.format("%1$s %2$-7s %3$s\n", new SimpleDateFormat(PATTERN).format(new Date(record.getMillis())),
				record.getLevel().getName(), formatMessage(record));
	}

}
