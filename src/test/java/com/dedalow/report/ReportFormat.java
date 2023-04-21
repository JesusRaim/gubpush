package com.dedalow.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ReportFormat extends Formatter {
	private final DateFormat DF = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

	public String format(LogRecord record) {
		StringBuilder builder = new StringBuilder(1000);
		String server = "";

		if (record.getLevel().equals(Level.SEVERE)) {
			server = "ERROR";
		} else if ((record.getLevel().equals(Level.ALL)) || (record.getLevel().equals(Level.FINE))
				|| (record.getLevel().equals(Level.FINER)) || (record.getLevel().equals(Level.FINEST))) {
			
			server = "DEBUG";
		} else {
			server = record.getLevel().toString();
		}

		builder.append(DF.format(new Date(record.getMillis()))).append(" - ");
		builder.append("[").append(server).append("] - ");
		builder.append(formatMessage(record));
		builder.append("\n");
		return builder.toString();
	}
	
}
