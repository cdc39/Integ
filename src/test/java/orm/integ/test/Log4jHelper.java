package orm.integ.test;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class Log4jHelper {

	public static void initLogger() {
		
		Logger log = Logger.getRootLogger();
		
		PatternLayout layout = new PatternLayout();
		layout.setConversionPattern("%d{HH:mm:ss.SSS} %p %C{1}- %m%n");
		
		ConsoleAppender appender = new ConsoleAppender();
		appender.setLayout(layout);
		appender.activateOptions(); 
		
		log.addAppender(appender);
		log.setLevel(Level.INFO);
		
	}
	
}
