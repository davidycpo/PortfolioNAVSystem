package portfolio.nav.system;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import database.Database;
import model.Holding;
import model.Portfolio;
import service.CsvReader;

public class PortfolioNavSystem {

	private static final String DB_URL = "jdbc:sqlite:src/main/resources/asset.sqlite";
	public static final String POSITION_CSV_PATH = "src/main/resources/position.csv";

	public static void main(String[] args) {
		File positionFile = new File(POSITION_CSV_PATH);
		Portfolio portfolio = parsePortfolioFromCSV(positionFile);
		if (portfolio == null)
			return;

		persistPortfolio(portfolio);
	}

	private static void persistPortfolio(Portfolio portfolio) {
		// Save asset into DB
		Database database = null;
		try {
			database = new Database(DB_URL);
			for (Holding holding : portfolio.getHoldings()) {
				if (holding.getTicker() == null) {
					System.out.println("Holding's ticker is null");
					continue;
				}
				database.insertAsset(holding.getTicker());
			}
			// database.purgeDatabase();
		} catch (Exception e) {
			System.out.println("Failed to save portfolio underlying asset into db, error: " + e.getMessage());
		} finally {
			if (database != null) {
				try {
					database.close();
				} catch (SQLException e) {
					System.out.println("Failed to close db" + e.getMessage());
				}
			}
		}
	}

	private static Portfolio parsePortfolioFromCSV(File csv) {
		// parse position file
		CsvReader csvReader = new CsvReader();
		Portfolio portfolio = null;
		try {
			portfolio = csvReader.parseCSV(csv);
		} catch (IOException e) {
			System.out.println("Failed to parse position csv, error: " + e.getMessage());
		}
		if (portfolio == null) {
			System.out.println("No position available");
			return null;
		}

		for (Holding holding : portfolio.getHoldings()) {
			System.out.println(holding);
		}
		return portfolio;
	}
}
