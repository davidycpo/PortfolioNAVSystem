package portfolio.nav.system;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.text.DecimalFormat;

import database.Database;
import model.AssetEntity;
import model.Holding;
import model.Portfolio;
import model.PortfolioNavResult;
import utils.Settings;

public class PortfolioNavSystemService {
	private static final ByteBuffer PRICE_CHANGE_BUFFER = ByteBuffer.allocate(Settings.PRICE_CHANGE_BUFFER_SIZE);
	private static final ByteBuffer PORTFOLIO_NAV_LENGTH_BUFFER = ByteBuffer
			.allocate(Settings.PORTFOLIO_NAV_LENGTH_BUFFER_SIZE);
	private static final ByteBuffer PORTFOLIO_NAV_RESULT_BUFFER = ByteBuffer
			.allocate(Settings.PORTFOLIO_NAV_RESULT_BUFFER_SIZE);
	private static final byte[] SYMBOL_BYTES = new byte[Settings.SYMBOL_BYTE_SIZE];
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(Settings.DECIMAL_FORMAT_PATTERN);

	/* Recalculate portfolio NAV based price change and publish NAV result */
	public void handlePriceChange(Portfolio portfolio) {
		try (ServerSocketChannel priceChangeServerSocketChannel = ServerSocketChannel.open()) {
			priceChangeServerSocketChannel.bind(new InetSocketAddress(Settings.PRICE_CHANGE_PORT));
			// Price Change Listener
			try (SocketChannel priceChangeChannel = priceChangeServerSocketChannel.accept()) {

				try (SocketChannel portfolioNavResultChannel = SocketChannel
						.open(new InetSocketAddress(Settings.HOSTNAME, Settings.PORTFOLIO_NAV_RESULT_PORT))) {

					int priceChangeCount = 1;
					while (true) {
						PRICE_CHANGE_BUFFER.clear();
						priceChangeChannel.read(PRICE_CHANGE_BUFFER);
						PRICE_CHANGE_BUFFER.flip();
						int symbolLength = PRICE_CHANGE_BUFFER.getInt();
						PRICE_CHANGE_BUFFER.get(SYMBOL_BYTES, 0, symbolLength);
						double price = PRICE_CHANGE_BUFFER.getDouble();
						String symbol = new String(SYMBOL_BYTES, 0, symbolLength);

						System.out.println("Received Price Change, Ticker: " + symbol + ", Price: "
								+ DECIMAL_FORMAT.format(price));

						// Update Price in Holding
						recalculatePortfolioNAVOnPriceChange(portfolio, symbol, price);

						// Publish Portfolio Nav Result
						publishPortfolioNavResult(portfolio, symbol, price, priceChangeCount,
								portfolioNavResultChannel);

						priceChangeCount++;
					}
				} catch (IOException e) {
					System.err.println("Failed to publish price change: " + e.getMessage());
				}
			}
		} catch (IOException e) {
			System.err.println("Failed to open socket to consume price change, error: " + e.getMessage());
		}
	}

	private void publishPortfolioNavResult(Portfolio portfolio, String symbol, double price, int priceChangeCount,
			SocketChannel portfolioNavResultChannel) throws IOException {
		PortfolioNavResult.PortfolioNAVResult portfolioNAV = getPortfolioNAV(portfolio, symbol, price,
				priceChangeCount);

		byte[] data = portfolioNAV.toByteArray();

		// Send the length of the message first
		PORTFOLIO_NAV_LENGTH_BUFFER.clear();
		PORTFOLIO_NAV_LENGTH_BUFFER.putInt(data.length);
		PORTFOLIO_NAV_LENGTH_BUFFER.flip();
		portfolioNavResultChannel.write(PORTFOLIO_NAV_LENGTH_BUFFER);

		// Send the actual message
		PORTFOLIO_NAV_RESULT_BUFFER.clear();
		PORTFOLIO_NAV_RESULT_BUFFER.put(data);
		PORTFOLIO_NAV_RESULT_BUFFER.flip();
		portfolioNavResultChannel.write(PORTFOLIO_NAV_RESULT_BUFFER);

		System.out.println("Published PortfolioNavResult for Price Change, Ticker: " + symbol + ", Price: "
				+ DECIMAL_FORMAT.format(price));
	}

	/*
	 * Calculate the Price of each holding by the underlying asset, primarily
	 * used in initialization
	 */
	public void calculatePortfolioNAV(Portfolio portfolio) {
		for (Holding holding : portfolio.getHoldings()) {
			holding.calculatePrice();
		}
	}

	/*
	 * Recalculate the Price of each holding by the underlying asset, only
	 * update the holding with matching asset in PriceChange
	 */
	public void recalculatePortfolioNAVOnPriceChange(Portfolio portfolio, String symbol, double price) {
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
			database = new Database(Settings.DB_PATH);
			database.initializeDatabase();
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
		if (portfolio == null || portfolio.getHoldings() == null || portfolio.getHoldings().isEmpty()) {
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
		// TODO Migrate away from Protobuf to reduce memory usage
		PortfolioNavResult.PortfolioNAVResult.Builder portfolioNavBuilder = PortfolioNavResult.PortfolioNAVResult
				.newBuilder()
				.setPriceChangeTicker(ticker)
				.setPriceChangeValue(priceChange)
				.setValue(portfolio.getNAV())
				.setPriceChangeCount(priceChangeCount);

		for (Holding holding : portfolio.getHoldings()) {
			AssetEntity asset = holding.getAsset();

			PortfolioNavResult.HoldingNAV holdingNAV = PortfolioNavResult.HoldingNAV.newBuilder()
					.setSymbol(asset.getDisplayName())
					.setPrice(holding.getPrice())
					.setQuantity(holding.getPosition())
					.setValue(holding.getValue())
					.build();
			portfolioNavBuilder.addHolding(holdingNAV);
		}
		return portfolioNavBuilder.build();
	}
}
