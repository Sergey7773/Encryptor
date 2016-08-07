package encryptor.encryptor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class TimeKeepingLogger extends Logger{
	
	private Calendar calendar;
	private DateFormat dateFormat;
	
	protected TimeKeepingLogger(String name) {
		super(name);
		calendar = Calendar.getInstance();
		dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	}
	
	@Override
	public void info(Object Message) {
		//super.info(dateFormat.format(calendar.getTime())+" : "+Message.toString());
	}
	
}
