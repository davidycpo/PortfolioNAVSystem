package utils;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.Random;

import model.AssetEntity;
import model.AssetType;

public class Utils {

	private static final String MMM_YYYY_PATTERN = "MMM yyyy";
	private static final String MMM_YYYY_WITH_SEPARATOR_PATTERN = "MMM-yyyy";
	private static final double riskFreeInterestRate = 0.02;
	private static final Random random = new Random();

	public static boolean isBlank(String str) {
		return str == null || str.trim().isEmpty();
	}

	public static Date parseDate(String dateStr) throws ParseException {
		if (dateStr == null || dateStr.trim().isEmpty())
			return null;
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
				.appendPattern(MMM_YYYY_PATTERN)
				.toFormatter(Locale.ENGLISH);
		YearMonth yearMonth = YearMonth.parse(dateStr, formatter);
		return Date.valueOf(yearMonth.atDay(1));
	}

	public static String parseString(Date date) {
		if (date == null)
			return null;
		SimpleDateFormat formatter = new SimpleDateFormat(MMM_YYYY_WITH_SEPARATOR_PATTERN, Locale.ENGLISH);
		return formatter.format(date);
	}

	public static double getRandomVariableFromNormalDistribution() {
		double r1 = random.nextDouble();
		double r2 = random.nextDouble();
		return Math.sqrt(-2.0 * Math.log(r1)) * Math.cos(2.0 * Math.PI * r2);
	}

	// Generate random int in [min, max)
	public static double getRandomDouble(double min, double max) {
		return min + (random.nextDouble() * (max - min));
	}

	/*
	 * return the cumulative probability of a random variable that is
	 * distributed according to a standardized normal distribution having a
	 * value less than x
	 */
	public static double getCumulativeProbability(double x) {
		return 0.5 * (1 + errorFunction(x / Math.sqrt(2)));
	}

	private static double errorFunction(double x) {
		double sign = (x >= 0) ? 1 : -1;
		x = Math.abs(x);

		double a1 = 0.254829592;
		double a2 = -0.284496736;
		double a3 = 1.421413741;
		double a4 = -1.453152027;
		double a5 = 1.061405429;
		double p = 0.3275911;

		double t = 1.0 / (1.0 + p * x);
		double y = 1.0 - ((((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t) * Math.exp(-x * x);

		return sign * y;
	}

	// Return the duration from now till the input date in unit of year
	public static double calculateYearsUntil(Date date) {
		if (date == null)
			return 0;
		long differenceInMs = date.getTime() - System.currentTimeMillis();
		return (double) differenceInMs / (1000 * 60 * 60 * 24 * 365.25);
	}

	/*
	 * Calculate the price of the holding based on the underlying asset's
	 * current price, assume a constant riskFreeInterestRate
	 */
	public static double calculateHoldingPrice(AssetEntity asset) {
		if (asset == null || asset.getPrice() == null) {
			return 0d;
		}
		if (AssetType.STOCK.equals(asset.getAssetType())) {
			return asset.getPrice();
		}

		double timeToMaturity = Utils.calculateYearsUntil(asset.getMaturityDate());
		double d1 = calculateD1Factor(asset.getPrice(), asset.getStrike(), asset.getAnnualizedStandardDeviation(),
				timeToMaturity);
		double d2 = calculateD2Factor(d1, asset.getAnnualizedStandardDeviation(), timeToMaturity);
		// In years
		if (AssetType.CALL.equals(asset.getAssetType())) {
			return (asset.getPrice() * Utils.getCumulativeProbability(d1)) - (asset.getStrike()
					* Math.exp(-1 * riskFreeInterestRate * timeToMaturity) * Utils.getCumulativeProbability(d2));

		}
		if (AssetType.PUT.equals(asset.getAssetType())) {
			return (asset.getStrike() * Math.exp(-1 * riskFreeInterestRate * timeToMaturity)
					* Utils.getCumulativeProbability(-1 * d2))
					- (asset.getPrice() * Utils.getCumulativeProbability(-1 * d1));
		}
		return 0d;
	}

	private static double calculateD1Factor(double stockPrice, double strikePrice, double annualizedStandardDeviation,
			double timeToMaturity) {
		double top = Math.log(stockPrice / strikePrice)
				+ ((riskFreeInterestRate + (annualizedStandardDeviation * annualizedStandardDeviation) / 2)
						* timeToMaturity);
		double bottom = annualizedStandardDeviation * Math.sqrt(timeToMaturity);
		return top / bottom;
	}

	private static double calculateD2Factor(double d1Factor, double annualizedStandardDeviation,
			double timeToMaturity) {
		return d1Factor - annualizedStandardDeviation * Math.sqrt(timeToMaturity);
	}
}
