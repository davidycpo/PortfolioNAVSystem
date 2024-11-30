package utils;

import java.sql.Date;
import java.text.ParseException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public class Utils {

	private static final String MMM_YYYY_PATTERN = "MMM yyyy";

	public static boolean isBlank(String str) {
		return str == null || str.trim().isEmpty();
	}

	public static Date parseDate(String dateStr) throws ParseException {
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
				.appendPattern(MMM_YYYY_PATTERN)
				.toFormatter(Locale.ENGLISH);
		YearMonth yearMonth = YearMonth.parse(dateStr, formatter);
		return Date.valueOf(yearMonth.atDay(1));
	}
}
