package model;

import java.sql.Date;

public class Asset {
	// Identifier of the asset, usually symbol
	private String ticker;

	// Type of the asset
	private AssetType assetType;

	// the strike price, only available for option
	private Double strike;

	// maturity date of the option, only available for option
	private Date maturityDate;

	public Asset(String ticker, AssetType assetType, Double strike, Date maturityDate) {
		this.ticker = ticker;
		this.assetType = assetType;
		this.strike = strike;
		this.maturityDate = maturityDate;
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

	public Double getStrike() {
		return strike;
	}

	public void setStrike(Double strike) {
		this.strike = strike;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	@Override
	public String toString() {
		return "Asset{" + "ticker='" + ticker + '\'' + ", assetType=" + assetType + ", strike=" + strike
				+ ", maturityDate=" + maturityDate + '}';
	}

}
