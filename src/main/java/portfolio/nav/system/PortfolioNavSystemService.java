package portfolio.nav.system;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.text.DecimalFormat;

import database.Database;
import model.AssetEntity;
import model.AssetType;
import model.Holding;
import model.Portfolio;
import model.PortfolioNavResult;
import service.CsvReader;
import utils.Settings;
import utils.Utils;

public class PortfolioNavSystemService {
	private static final ByteBuffer buffer = ByteBuffer.allocate(Settings.BUFFER_SIZE);
	private static final byte[] symbolBytes = new byte[6];
	private static final DecimalFormat df = new DecimalFormat(Settings.DECIMAL_FORMAT_PATTERN);

	public void handlePriceChange(Portfolio portfolio) {
		try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
			serverSocketChannel.bind(new InetSocketAddress(Settings.PRICE_CHANGE_PORT));

			// Price Change Listener
			try (SocketChannel socketChannel = serverSocketChannel.accept()) {

				int priceChangeCount = 1;
				while (true) {
					buffer.clear();
					socketChannel.read(buffer);
					buffer.flip();
					int symbolLength = buffer.getInt();
					buffer.get(symbolBytes, 0, symbolLength);
					double price = buffer.getDouble();
					String symbol = new String(symbolBytes, 0, symbolLength);

					System.out.println("Received Price Change, Ticker: " + symbol + ", Price: " + df.format(price));

					// Update Price in Holding
					recalculatePortfolioNAVOnPriceChange(portfolio, symbol, price);

					try (Socket socket = new Socket(Settings.HOSTNAME, Settings.PORTFOLIO_NAV_PORT);
							ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {

						// Publish
						PortfolioNavResult.PortfolioNAVResult portfolioNAV = getPortfolioNAV(portfolio, symbol, price,
								priceChangeCount);
						output.writeObject(portfolioNAV.toByteArray());

					} catch (IOException e) {
						System.err.println("Failed to publish price change: " + e.getMessage());
					}

					priceChangeCount++;
				}

			}
		} catch (IOException e) {
			System.err.println("Failed to open socket, error: " + e.getMessage());
		}
	}

	public void calculatePortfolioNAV(Portfolio portfolio) {
		for (Holding holding : portfolio.getHoldings()) {
			holding.calculatePrice();
		}
	}

	private void recalculatePortfolioNAVOnPriceChange(Portfolio portfolio, String symbol, double price) {
		for (Holding holding : portfolio.getHoldings()) {
			if (symbol.equals(holding.getAsset().getTicker())) {
				holding.getAsset().setPrice(price);
				holding.calculatePrice();
			}
		}
	}

	public void persistPortfolio(Portfolio portfolio) {
		// Save asset into DB
		Database database = null;
		try {
			database = new Database(Settings.DB_URL);
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

	public Portfolio parsePortfolioFromCSV(File csv) {
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

	private PortfolioNavResult.PortfolioNAVResult getPortfolioNAV(final Portfolio portfolio, final String ticker,
			final double priceChange, final int priceChangeCount) {
		PortfolioNavResult.PortfolioNAVResult.Builder portfolioNavBuilder = PortfolioNavResult.PortfolioNAVResult
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

			PortfolioNavResult.HoldingNAV holdingNAV = PortfolioNavResult.HoldingNAV.newBuilder()
					.setSymbol(symbol)
					.setPrice(holding.getPrice())
					.setQuantity(holding.getPosition())
					.setValue(holding.getValue())
					.build();
			portfolioNavBuilder.addHolding(holdingNAV);
		}
		return portfolioNavBuilder.build();
	}
}
