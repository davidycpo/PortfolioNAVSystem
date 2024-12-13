package model;

public class Stock {

	// Identifier of the asset, usually symbol
	private String ticker;

	// the current price of the stock, can never be zero
	private Double price;

	// expected return, a unique number between 0-1
	private Double expectedReturn;

	// annualized standard deviation, a unique number between 0-1
	private Double annualizedStandardDeviation;

	public Stock(String ticker, Double price, Double expectedReturn, Double annualizedStandardDeviation) {
		this.ticker = ticker;
		this.price = price;
		this.expectedReturn = expectedReturn;
		this.annualizedStandardDeviation = annualizedStandardDeviation;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
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

	public void setExpectedReturn(Double expectedReturn) {
	}

	public Double getAnnualizedStandardDeviation() {
		return annualizedStandardDeviation;
	}

	public void setAnnualizedStandardDeviation(Double annualizedStandardDeviation) {
	}

	@Override
	public String toString() {
		return "Stock{" + "ticker='" + ticker + '\'' + ", price=" + price + ", expectedReturn=" + expectedReturn
				+ ", annualizedStandardDeviation=" + annualizedStandardDeviation + '}';
	}

}
