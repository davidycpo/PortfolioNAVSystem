package market.data.provider;

import java.util.List;

import model.Stock;

public class MarketDataProvider {

	public static void main(String[] args) {
		MarketDataProviderService service = new MarketDataProviderService();
		List<Stock> stocks = service.getStockListFromDB();
		if (stocks == null) {
			System.out.println("No stocks found, returning...");
			return;
		}

		service.simulateStockMovement(stocks);
	}

}
