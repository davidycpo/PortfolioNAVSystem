package market.data.provider;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import database.Database;
import model.AssetEntity;
import model.AssetType;
import model.PriceChangeOuterClass;
import model.PriceChangeQueue;
import model.Stock;

public class MarketDataProvider {

	public static final int CONSTANT_FACTOR = 7257600;

	private static final Random random = new Random();

	private static final PriceChangeQueue priceChangeQueue = new PriceChangeQueue();

	private static final String DB_URL = "jdbc:sqlite:src/main/resources/asset.sqlite";
	public static final double MIN_RANDOM_VALUE = 200d;
	public static final double MAX_RANDOM_VALUE = 500d;
	public static final double MIN_TIME_DELTA = 0.5;
	public static final double MAX_TIME_DELTA = 2;

	public static void main(String[] args) {
		// Get Stock from DB
		List<AssetEntity> assetEntities = getAssetEntities();
		if (assetEntities == null)
			return;

		List<Stock> stocks = new ArrayList<>();
		for (AssetEntity asset : assetEntities) {
			stocks.add(toStock(asset));
		}

		// Simulate stock movement and publish price change
		publishStockMovement(stocks);
	}

	public static PriceChangeQueue getPriceChangeQueue() {
		return priceChangeQueue;
	}

	private static List<AssetEntity> getAssetEntities() {
		List<AssetEntity> assetEntities = new ArrayList<>();
		Database database = null;
		try {
			database = new Database(DB_URL);
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

	private static void publishStockMovement(List<Stock> stocks) {
		int counter = 1;
		System.out.println("## " + counter + " Market Data Update");
		for (Stock stock : stocks) {
			System.out.println(stock.getTicker() + " change to " + Math.ceil(stock.getPrice()));
		}
		System.out.println();

		while (true) {
			for (Stock stock : stocks) {
				counter++;
				double deltaTime = getRandomDouble(MIN_TIME_DELTA, MAX_TIME_DELTA) * 1000;
				// System.out.println("Time interval: " + deltaTime + "\n");

				// Sleep for delta time
				try {
					Thread.sleep((long) deltaTime);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					System.err.println("Thread was interrupted");
					break;
				}

				// Calculate Stock Movement
				System.out.println("## " + counter + " Market Data Update");
				double factor = brownianMotionFactor(deltaTime, stock.getExpectedReturn(),
						stock.getAnnualizedStandardDeviation());
				// System.out.println(stock.getTicker() + " factor: " + factor);
				double newStockPrice = stock.getPrice() + stock.getPrice() * factor;
				stock.setPrice(newStockPrice);

				System.out.println(stock.getTicker() + " change to " + Math.ceil(stock.getPrice()) + "\n");

				// Publish Price Change
				PriceChangeOuterClass.PriceChange priceChange = PriceChangeOuterClass.PriceChange.newBuilder()
						.setTicker(stock.getTicker())
						.setPrice(stock.getPrice())
						.build();

				priceChangeQueue.publish(priceChange.toByteArray());
				System.out
						.println("Published: Ticker: " + stock.getTicker() + " Price: " + Math.ceil(stock.getPrice()));
			}
		}
	}

	private static Stock toStock(AssetEntity asset) {
		// Random initial price
		return new Stock(asset.getTicker(), getRandomDouble(MIN_RANDOM_VALUE, MAX_RANDOM_VALUE),
				asset.getExpectedReturn(), asset.getAnnualizedStandardDeviation());
	}

	private static double brownianMotionFactor(double deltaTime, double expectedReturn,
			double annualizedStandardDeviation) {
		return expectedReturn * (deltaTime / CONSTANT_FACTOR) + annualizedStandardDeviation
				* getRandomVariableFromNormalDistribution() * Math.sqrt(deltaTime / CONSTANT_FACTOR);
	}

	private static double getRandomVariableFromNormalDistribution() {
		double r1 = random.nextDouble();
		double r2 = random.nextDouble();
		return Math.sqrt(-2.0 * Math.log(r1)) * Math.cos(2.0 * Math.PI * r2);
	}

	// Generate random int in [min, max)
	private static double getRandomDouble(double min, double max) {
		return min + (max - min) * random.nextDouble();
	}
}
