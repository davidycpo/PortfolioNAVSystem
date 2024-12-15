package market.data.provider;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import model.AssetEntity;
import model.AssetType;
import model.Stock;
import utils.Settings;
import utils.Utils;

public class MarketDataProviderService {
	private static final ByteBuffer BUFFER = ByteBuffer.allocate(Settings.PRICE_CHANGE_BUFFER_SIZE);
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(Settings.DECIMAL_FORMAT_PATTERN);

	// Get Stock from DB
	public List<Stock> getStockListFromDB() {
		List<AssetEntity> assetEntities = getAssetEntities();
		if (assetEntities == null)
			return null;

		List<Stock> stocks = new ArrayList<>();
		for (AssetEntity asset : assetEntities) {
			stocks.add(Utils.toStock(asset, Settings.MIN_RANDOM_VALUE, Settings.MAX_RANDOM_VALUE));
		}
		return stocks;
	}

	private List<AssetEntity> getAssetEntities() {
		List<AssetEntity> assetEntities = new ArrayList<>();
		Database database = null;
		try {
			database = new Database(Settings.DB_URL);
			assetEntities = database.getAssetsByType(AssetType.STOCK);
		} catch (Exception e) {
			System.err.println("Failed to find assets from db, error: " + e.getMessage());
		} finally {
			if (database != null) {
				try {
					database.close();
				} catch (SQLException e) {
					System.err.println("Failed to close db" + e.getMessage());
				}
			}
		}

		if (assetEntities == null || assetEntities.isEmpty()) {
			System.err.println("No stock asset is found");
			return null;
		}
		return assetEntities;
	}

	// Simulate stock movement and publish price change
	public void simulateStockMovement(final List<Stock> stocks) {
		int counter = 1;
		try (SocketChannel socketChannel = SocketChannel.open()) {
			socketChannel.connect(new InetSocketAddress(Settings.HOSTNAME, Settings.PRICE_CHANGE_PORT));
			while (true) {
				double deltaTime = Utils.getRandomDouble(Settings.MIN_TIME_DELTA, Settings.MAX_TIME_DELTA) * 1000;
				// System.out.println("Time interval: " + deltaTime + "\n");

				// Sleep for delta time
				try {
					Thread.sleep((long) deltaTime);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					System.err.println("Thread was interrupted");
					break;
				}

				// Pick A Random Stock
				int randomIndex = (int) (Math.random() * stocks.size());
				Stock stock = stocks.get(randomIndex);

				// Calculate Stock Movement
				System.out.println("## " + counter + " Market Data Update");
				double factor = Utils.getBrownianMotionFactor(deltaTime, stock.getExpectedReturn(),
						stock.getAnnualizedStandardDeviation());
				// System.out.println(stock.getTicker() + " factor: " +
				// factor);
				double newStockPrice = stock.getPrice() + stock.getPrice() * factor;
				stock.setPrice(newStockPrice);

				System.out.println(stock.getTicker() + " change to " + DECIMAL_FORMAT.format(stock.getPrice()) + "\n");

				// Publish Price Change
				publishPriceChange(stock, socketChannel);
				counter++;
			}
		} catch (IOException e) {
			System.err.println("Failed to publish price change: " + e.getMessage());
		}

	}

	private void publishPriceChange(final Stock stock, final SocketChannel channel) throws IOException {
		BUFFER.clear();
		byte[] tickerBytes = stock.getTicker().getBytes();
		BUFFER.putInt(tickerBytes.length);
		BUFFER.put(tickerBytes);
		BUFFER.putDouble(stock.getPrice());
		BUFFER.flip();
		channel.write(BUFFER);
		System.out.println("Published Price Change: Ticker: " + stock.getTicker() + " Price: "
				+ DECIMAL_FORMAT.format(stock.getPrice()) + "\n");
	}

}
