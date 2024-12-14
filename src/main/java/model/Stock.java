package model;

public class Stock {

	// Identifier of the asset, usually symbol
	private final String ticker;

	// the current price of the stock, can never be zero
	private double price;

	// expected return, a unique number between 0-1
	private final double expectedReturn;

	// annualized standard deviation, a unique number between 0-1
	private final double annualizedStandardDeviation;

	public Stock(String ticker, double price, double expectedReturn, double annualizedStandardDeviation) {
		this.ticker = ticker;
		this.price = price;
		this.expectedReturn = expectedReturn;
		this.annualizedStandardDeviation = annualizedStandardDeviation;
	}

	public String getTicker() {
		return ticker;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getExpectedReturn() {
		return expectedReturn;
	}

	public double getAnnualizedStandardDeviation() {
		return annualizedStandardDeviation;
	}

	@Override
	public String toString() {
		return "Stock{" + "ticker='" + ticker + '\'' + ", price=" + price + ", expectedReturn=" + expectedReturn
				+ ", annualizedStandardDeviation=" + annualizedStandardDeviation + '}';
	}

}
