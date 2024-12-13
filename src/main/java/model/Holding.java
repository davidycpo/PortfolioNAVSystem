package model;

import utils.Utils;

public class Holding {
	private final AssetEntity asset;
	private final Double position;
	private Double price;

	public Holding(AssetEntity asset, Double position) {
		this.asset = asset;
		this.position = position;
	}

	public AssetEntity getAsset() {
		return asset;
	}

	public Double getPosition() {
		return position;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public void calculatePrice() {
		this.price = Utils.calculateHoldingPrice(asset);
	}

	public Double getValue() {
		if (price == null || position == null) {
			return 0d;
		}
		return price * position;
	}

	@Override
	public String toString() {
		return "Holding{" + "ticker=" + asset + ", position=" + position + ", price=" + price + '}';
	}
}
