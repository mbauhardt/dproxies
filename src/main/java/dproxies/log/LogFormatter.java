package dproxies.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    private static final DateFormat _dateFormat = new SimpleDateFormat(
	    "dd/MM/yyyy hh:mm:ss.SSS");

    @Override
    public String format(LogRecord record) {
	StringBuilder builder = new StringBuilder(1000);
	builder.append(_dateFormat.format(new Date(record.getMillis())))
		.append(" - ");
	builder.append("[").append(record.getLevel()).append("] - ");
	builder.append("[").append(record.getSourceClassName()).append(".");
	builder.append(record.getSourceMethodName()).append("] - ");
	builder.append(formatMessage(record));
	builder.append("\n");
	return builder.toString();
    }

}
