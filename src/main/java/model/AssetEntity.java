package model;

import java.sql.Date;

public class AssetEntity {
	// Identifier of the asset, usually symbol
	private String ticker;

	// Type of the asset
	private AssetType assetType;

	// the strike price, only available for option
	private double strike;

	// maturity date of the option, only available for option
	private Date maturityDate;

	// expected return, a unique number between 0-1
	private final double expectedReturn;

	// annualized standard
	private final double annualizedStandardDeviation;

	// current price
	private double price;

	public AssetEntity(String ticker, AssetType assetType, double strike, Date maturityDate, double expectedReturn,
			double annualizedStandardDeviation) {
		this.ticker = ticker;
		this.assetType = assetType;
		this.strike = strike;
		this.maturityDate = maturityDate;
		this.expectedReturn = expectedReturn;
		this.annualizedStandardDeviation = annualizedStandardDeviation;
	}

	public AssetEntity(String ticker, AssetType assetType, double strike, Date maturityDate, double expectedReturn,
			double annualizedStandardDeviation, double price) {
		this.ticker = ticker;
		this.assetType = assetType;
		this.strike = strike;
		this.maturityDate = maturityDate;
		this.expectedReturn = expectedReturn;
		this.annualizedStandardDeviation = annualizedStandardDeviation;
		this.price = price;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public AssetType getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}

	public double getStrike() {
		return strike;
	}

	public void setStrike(double strike) {
		this.strike = strike;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public double getExpectedReturn() {
		return expectedReturn;
	}

	public void setExpectedReturn(double expectedReturn) {
	}

	public double getAnnualizedStandardDeviation() {
		return annualizedStandardDeviation;
	}

	public void setAnnualizedStandardDeviation(double annualizedStandardDeviation) {
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "AssetEntity{" + "ticker='" + ticker + '\'' + ", assetType=" + assetType + ", strike=" + strike
				+ ", maturityDate=" + maturityDate + ", expectedReturn=" + expectedReturn
				+ ", annualizedStandardDeviation=" + annualizedStandardDeviation + ", price=" + price + '}';
	}
}
