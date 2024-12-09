package model;

public class Stock {

	// Identifier of the asset, usually symbol
	private final String ticker;

	// the current price of the stock, can never be zero
	private Double price;

	// expected return, a unique number between 0-1
	private final Double expectedReturn;

	// annualized standard deviation, a unique number between 0-1
	private final Double annualizedStandardDeviation;

	public Stock(String ticker, Double price, Double expectedReturn, Double annualizedStandardDeviation) {
		this.ticker = ticker;
		this.price = price;
		this.expectedReturn = expectedReturn;
		this.annualizedStandardDeviation = annualizedStandardDeviation;
	}

	public String getTicker() {
		return ticker;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getExpectedReturn() {
		return expectedReturn;
	}

	public Double getAnnualizedStandardDeviation() {
		return annualizedStandardDeviation;
	}

	@Override
	public String toString() {
		return "Stock{" + "ticker='" + ticker + '\'' + ", price=" + price + ", expectedReturn=" + expectedReturn
				+ ", annualizedStandardDeviation=" + annualizedStandardDeviation + '}';
	}

}
