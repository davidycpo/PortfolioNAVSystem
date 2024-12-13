package portfolio.nav.system;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import database.Database;
import model.AssetEntity;
import model.AssetType;
import model.Holding;
import model.Portfolio;
import model.PortfolioNAVOuterClass;
import model.PriceChangeOuterClass;
import service.CsvReader;
import utils.Utils;

public class PortfolioNavSystem {

	private static final String DB_URL = "jdbc:sqlite:src/main/resources/asset.sqlite";
	private static final String POSITION_CSV_PATH = "src/main/resources/position.csv";

	public static void main(String[] args) {
		File positionFile = new File(POSITION_CSV_PATH);
		Portfolio portfolio = parsePortfolioFromCSV(positionFile);
		if (portfolio == null)
			return;

		for (Holding holding : portfolio.getHoldings()) {
			holding.calculatePrice();
		}

		persistPortfolio(portfolio);

		// Price Change Listener
		try (ServerSocket serverSocket = new ServerSocket(3333)) {
			int priceChangeCount = 0;
			while (true) {
				try (Socket socket = serverSocket.accept();
						ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
					byte[] data = (byte[]) input.readObject();
					PriceChangeOuterClass.PriceChange priceChange = PriceChangeOuterClass.PriceChange.parseFrom(data);
					System.out.println("Received Price Change, Ticker: " + priceChange.getTicker() + ", Price: "
							+ Math.ceil(priceChange.getPrice()));

					// Update Price in Holding
					for (Holding holding : portfolio.getHoldings()) {
						if (priceChange.getTicker().equals(holding.getAsset().getTicker())) {
							holding.getAsset().setPrice(priceChange.getPrice());
							holding.calculatePrice();
							break;
						}
					}

					// Publish
					publishPortfolioNAV(portfolio, priceChange, priceChangeCount);

					priceChangeCount++;

				} catch (ClassNotFoundException e) {
					System.err.println("Failed to parse priceChange, error: " + e.getMessage());
				}
			}
		} catch (IOException e) {
			System.err.println("Failed to open socket, error: " + e.getMessage());
		}
	}

	private static void persistPortfolio(Portfolio portfolio) {
		// Save asset into DB
		Database database = null;
		try {
			database = new Database(DB_URL);
			for (Holding holding : portfolio.getHoldings()) {
				if (holding.getAsset() == null) {
					System.err.println("Holding's ticker is null");
					continue;
				}
				database.insertAsset(holding.getAsset());
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

	private static void publishPortfolioNAV(final Portfolio portfolio,
			final PriceChangeOuterClass.PriceChange priceChange, final int priceChangeCount) {
		PortfolioNAVOuterClass.PortfolioNAV.Builder portfolioNavBuilder = PortfolioNAVOuterClass.PortfolioNAV
				.newBuilder()
				.setPriceChangeTicker(priceChange.getTicker())
				.setPriceChangeValue(priceChange.getPrice())
				.setValue(portfolio.getNAV())
				.setPriceChangeCount(priceChangeCount);

		for (Holding holding : portfolio.getHoldings()) {
			AssetEntity asset = holding.getAsset();
			String symbol;
			if (AssetType.STOCK.equals(asset.getAssetType())) {
				symbol = asset.getTicker();
			} else {
				String optionSymbol = AssetType.PUT.equals(asset.getAssetType()) ? "P" : "C";
				symbol = asset.getTicker() + "-" + Utils.parseString(asset.getMaturityDate()) + "-"
						+ asset.getStrike().intValue() + "-" + optionSymbol;
			}

			PortfolioNAVOuterClass.HoldingNAV holdingNAV = PortfolioNAVOuterClass.HoldingNAV.newBuilder()
					.setSymbol(symbol)
					.setPrice(holding.getPrice())
					.setQuantity(holding.getPosition())
					.setValue(holding.getValue())
					.build();
			portfolioNavBuilder.addHolding(holdingNAV);
		}
		PortfolioNAVOuterClass.PortfolioNAV portfolioNav = portfolioNavBuilder.build();

		try (Socket socket = new Socket("localhost", 4444);
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {
			output.writeObject(portfolioNav.toByteArray());
		} catch (IOException e) {
			System.err.println("Failed to publish price change: " + e.getMessage());
		}
	}
}
