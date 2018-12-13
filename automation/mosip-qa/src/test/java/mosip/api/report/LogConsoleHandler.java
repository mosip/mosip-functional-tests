package mosip.api.report;

import java.util.logging.Logger;
import java.io.IOException;
import java.util.logging.Level;

public class LogConsoleHandler {
	static String testname;

	public LogConsoleHandler(String tc) throws IOException {
		super();
		testname = tc;
		Logger log = Logger.getLogger("Logger");
		log.setLevel(Level.ALL);
		log.log(Level.INFO, "Running SoapUI test [" + testname + "]");
	}
}
