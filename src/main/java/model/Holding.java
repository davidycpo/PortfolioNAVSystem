package model;

import utils.Utils;

public class Holding {
	private final AssetEntity asset;
	private final double position;
	private double price;

	public Holding(AssetEntity asset, double position) {
		this.asset = asset;
		this.position = position;
	}

	public AssetEntity getAsset() {
		return asset;
	}

	public double getPosition() {
		return position;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	// Calculate the holding's price based on the price of the underlying asset
	public void calculatePrice() {
		this.price = Utils.calculateHoldingPrice(asset);
	}

	// Calculate the value of holding on the fly
	public double getValue() {
		if (price == 0d || position == 0d) {
			return 0d;
		}
		return price * position;
	}

	@Override
	public String toString() {
		return "Holding{" + "ticker=" + asset + ", position=" + position + ", price=" + price + '}';
	}
}
