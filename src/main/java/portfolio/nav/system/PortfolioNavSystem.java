package portfolio.nav.system;

import java.io.File;

import model.Portfolio;
import utils.Settings;

public class PortfolioNavSystem {

	public static void main(String[] args) {
		PortfolioNavSystemService service = new PortfolioNavSystemService();

		File positionFile = new File(Settings.POSITION_CSV_PATH);
		Portfolio portfolio = service.parsePortfolioFromCSV(positionFile);
		if (portfolio == null)
			return;

		service.persistPortfolio(portfolio);

		/*
		 * Initial Price calculation - based on random start of the day price
		 * This is required for initial PortfolioNAV result when new price
		 * change is not yet available for all stocks
		 */
		service.calculatePortfolioNAV(portfolio);

		service.handlePriceChange(portfolio);
	}

}
