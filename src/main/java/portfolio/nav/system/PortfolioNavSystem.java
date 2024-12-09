package portfolio.nav.system;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.google.protobuf.InvalidProtocolBufferException;

import database.Database;
import market.data.provider.MarketDataProvider;
import model.Holding;
import model.Portfolio;
import model.PriceChangeOuterClass;
import model.PriceChangeQueue;
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

		// Price Change Listener
		PriceChangeQueue priceChangeQueue = MarketDataProvider.getPriceChangeQueue();

		try {
			while (true) {
				byte[] data = priceChangeQueue.consume();
				PriceChangeOuterClass.PriceChange priceChange = PriceChangeOuterClass.PriceChange.parseFrom(data);
				System.out.println(
						"Processed: Ticker: " + priceChange.getTicker() + ", Price: " + priceChange.getPrice());
			}
		} catch (InvalidProtocolBufferException e) {
			System.err.println("InvalidProtocolBufferException: " + e.getMessage());
		}
	}

	private static void persistPortfolio(Portfolio portfolio) {
		// Save asset into DB
		Database database = null;
		try {
			database = new Database(DB_URL);
			for (Holding holding : portfolio.getHoldings()) {
				if (holding.getTicker() == null) {
					System.err.println("Holding's ticker is null");
					continue;
				}
				database.insertAsset(holding.getTicker());
			}
		} catch (Exception e) {
			System.err.println("Failed to save portfolio underlying asset into db, error: " + e + e.getMessage());
		} finally {
			if (database != null) {
				try {
					database.close();
				} catch (SQLException e) {
					System.err.println("Failed to close db" + e.getMessage());
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
			System.err.println("Failed to parse position csv, error: " + e.getMessage());
		}
		if (portfolio == null) {
			System.err.println("No position available");
			return null;
		}

		for (Holding holding : portfolio.getHoldings()) {
			System.out.println(holding);
		}
		return portfolio;
	}
}
