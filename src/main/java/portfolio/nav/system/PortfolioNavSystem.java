package portfolio.nav.system;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import database.Database;
import model.AssetEntity;
import model.AssetType;
import model.Holding;
import model.Portfolio;
import model.PortfolioNAVOuterClass;
import service.CsvReader;
import utils.Utils;

public class PortfolioNavSystem {

	private static final String DB_URL = "jdbc:sqlite:src/main/resources/asset.sqlite";
	private static final String POSITION_CSV_PATH = "src/main/resources/position.csv";
	private static final int BUFFER_SIZE = 512;
	private static final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

	public static void main(String[] args) {
		File positionFile = new File(POSITION_CSV_PATH);
		Portfolio portfolio = parsePortfolioFromCSV(positionFile);
		if (portfolio == null)
			return;

		persistPortfolio(portfolio);

		// Initial Price calculation - based on random start of the day price
		// This is required for
		for (Holding holding : portfolio.getHoldings()) {
			holding.calculatePrice();
		}

		try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
			serverSocketChannel.bind(new InetSocketAddress(3333));

			// Price Change Listener
			try (SocketChannel socketChannel = serverSocketChannel.accept()) {

				int priceChangeCount = 1;
				while (true) {
					buffer.clear();
					int bytesRead = socketChannel.read(buffer);
					byte[] data = new byte[bytesRead];
					buffer.get(data);
					String message = new String(buffer.array(), 0, bytesRead, StandardCharsets.UTF_8);
					String[] splits = message.split(" ");

					String symbol = splits[0];
					double price = Double.parseDouble(splits[1]);

					System.out.println("Received Price Change, Ticker: " + symbol + ", Price: " + Math.ceil(price));

					// Update Price in Holding
					for (Holding holding : portfolio.getHoldings()) {
						if (symbol.equals(holding.getAsset().getTicker())) {
							holding.getAsset().setPrice(price);
							holding.calculatePrice();
						}
					}

					// Publish
					publishPortfolioNAV(portfolio, symbol, price, priceChangeCount);
					priceChangeCount++;

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

	private static void publishPortfolioNAV(final Portfolio portfolio, final String ticker, final double priceChange,
			final int priceChangeCount) {
		PortfolioNAVOuterClass.PortfolioNAV.Builder portfolioNavBuilder = PortfolioNAVOuterClass.PortfolioNAV
				.newBuilder()
				.setPriceChangeTicker(ticker)
				.setPriceChangeValue(priceChange)
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
						+ (int) asset.getStrike() + "-" + optionSymbol;
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
