package utils;

public class Settings {
	// Connection
	public static final String HOSTNAME = "localhost";
	public static final int PRICE_CHANGE_PORT = 3333;
	public static final int PORTFOLIO_NAV_RESULT_PORT = 4444;

	// Quant related
	public static final double riskFreeInterestRate = 0.02;

	// Market Data Simulation
	public static final double MIN_TIME_DELTA = 0.5;
	public static final double MAX_TIME_DELTA = 2;
	public static final double MIN_RANDOM_VALUE = 200d;
	public static final double MAX_RANDOM_VALUE = 500d;

	// Buffer
	public static final int PRICE_CHANGE_BUFFER_SIZE = 18;
	public static final int PORTFOLIO_NAV_LENGTH_BUFFER_SIZE = 4;
	public static final int PORTFOLIO_NAV_RESULT_BUFFER_SIZE = 512;
	public static final int SYMBOL_BYTE_SIZE = 6;

	// DB
	public static final String DB_PATH = "jdbc:sqlite:src/main/resources/asset.sqlite";
	public static final String POSITION_CSV_PATH = "src/main/resources/position.csv";

	// Formatting
	public static final String DECIMAL_FORMAT_PATTERN = "#,##0.00";
}
