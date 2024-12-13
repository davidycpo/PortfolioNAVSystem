package model;

import java.util.ArrayList;
import java.util.List;

public class Portfolio {
	private List<Holding> holdings;

	public List<Holding> getHoldings() {
		return holdings;
	}

	public void addHolding(Holding holding) {
		if (this.holdings == null) {
			this.holdings = new ArrayList<>();
		}
		holdings.add(holding);
	}

	public double getNAV() {
		double nav = 0;
		for (Holding holding : holdings) {
			nav += holding.getValue();
		}
		return nav;
	}
}
