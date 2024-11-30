package portfolio.nav.system;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import database.Database;
import model.Asset;
import model.AssetType;
import model.Holding;
import model.Portfolio;
import service.CsvReader;

public class PortfolioNavSystem {

	private static final String DB_URL = "jdbc:sqlite:src/main/resources/asset.sqlite";

	public static void main(String[] args) {
		// parse position file
		CsvReader csvReader = new CsvReader();
		File positionFile = new File("src/main/resources/position.csv");
		Portfolio portfolio = null;
		try {
			portfolio = csvReader.parseCSV(positionFile);
		} catch (IOException e) {
			System.out.println("Failed to parse position csv, error: " + e.getMessage());
		}
		if (portfolio == null) {
			System.out.println("No position available");
			return;
		}

		for(Holding holding : portfolio.getHoldings()) {
			System.out.println(holding);
		}

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
//			database.purgeDatabase();
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

	private static void testDB() {
		Database database = null;
		try {
			database = new Database(DB_URL);
			Asset asset = new Asset("AAPL", AssetType.STOCK, 0d, null);
			database.insertAsset(asset);
			Asset apple = database.getAssetByTickerAndType("AAPL", AssetType.STOCK);
			System.out.println(apple);
			database.purgeDatabase();

		} catch (SQLException e) {
			System.out.println("Failed to test db" + e.getMessage());
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
}
