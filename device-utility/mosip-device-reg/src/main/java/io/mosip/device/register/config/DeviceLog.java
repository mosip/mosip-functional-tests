package io.mosip.device.register.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import io.mosip.device.register.util.DeviceUtil;

public class DeviceLog {
	public static Logger auditLog = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static String auditLogFileName = "mosip_auditLogs" + DeviceUtil.getCurrentDateAndTime().replace(":", "_") + ".log";
	public static String auditLogFile = System.getProperty("user.dir") +"\\logs\\"+  auditLogFileName;
	public static void setupLogger() {
		File logDir=new File(System.getProperty("user.dir") +"\\logs");
		logDir.mkdir();
		LogManager.getLogManager().reset();
		auditLog.setLevel(Level.ALL);

		ConsoleHandler ch = new ConsoleHandler();
		ch.setFormatter(new CustomizedLogFormatter());
		ch.setLevel(Level.ALL);
		auditLog.addHandler(ch);
		try {
			FileHandler fh = new FileHandler(auditLogFile, false);
			fh.setFormatter(new CustomizedLogFormatter());
			fh.setLevel(Level.ALL);
			auditLog.addHandler(fh);
		} catch (IOException e) {
			auditLog.log(Level.SEVERE, "File logger not working", e);
		}
	}
}
