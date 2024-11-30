package model;

import java.util.ArrayList;
import java.util.List;

public class Portfolio {
	private List<Holding> holdings;

	public List<Holding> getHoldings() {
		return holdings;
	}

	public void setHoldings(List<Holding> holdings) {
		this.holdings = holdings;
	}

	public void addHolding(Holding holding) {
		if (this.holdings == null) {
			this.holdings = new ArrayList<>();
		}
		holdings.add(holding);
	}
}
