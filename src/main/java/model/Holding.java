package model;

public class Holding {
	private Asset ticker;
	private Double position;
	private Double price;
	private Double value;

	public Holding(Asset ticker, Double position) {
		this.ticker = ticker;
		this.position = position;
	}

	public Asset getTicker() {
		return ticker;
	}

	public void setTicker(Asset ticker) {
		this.ticker = ticker;
	}

	public Double getPosition() {
		return position;
	}

	public void setPosition(Double position) {
		this.position = position;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getValue() {
		// TODO the calculation
		if (price == null) {
			return null;
		}
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Holding{" + "ticker=" + ticker + ", position=" + position + ", price=" + price + ", value=" + value
				+ '}';
	}
}
