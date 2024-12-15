package database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.AssetEntity;
import model.AssetType;

public class DatabaseTest {

	private Database database;

	@BeforeEach
	public void setUp() throws SQLException {
		database = new Database("jdbc:sqlite::memory:");
		database.initializeDatabase();
	}

	@AfterEach
	public void tearDown() throws SQLException {
		database.purgeDatabase();
		database.close();
	}

	@Test
	public void insertAssetTest() throws SQLException {
		AssetEntity asset = new AssetEntity("AAPL", AssetType.CALL, 150.0, Date.valueOf("2023-01-01"), 0.05, 0.1);

		database.insertAsset(asset);

		AssetEntity retrievedAsset = database.getAssetByTickerAndType("AAPL", AssetType.CALL);
		assertNotNull(retrievedAsset);
		assertEquals("AAPL", retrievedAsset.getTicker());
		assertEquals(AssetType.CALL, retrievedAsset.getAssetType());
		assertEquals(150.0, retrievedAsset.getStrike());
		assertEquals(Date.valueOf("2023-01-01"), retrievedAsset.getMaturityDate());
		assertEquals(0.05, retrievedAsset.getExpectedReturn());
		assertEquals(0.1, retrievedAsset.getAnnualizedStandardDeviation());
	}

	@Test
	public void insertDuplicatedAssetTest() throws SQLException {
		AssetEntity asset = new AssetEntity("AAPL", AssetType.CALL, 150.0, Date.valueOf("2023-01-01"), 0.05, 0.1);

		database.insertAsset(asset);

		// Try to insert the same asset again
		database.insertAsset(asset);

		List<AssetEntity> assets = database.getAssetsByType(AssetType.CALL);
		assertEquals(1, assets.size());
	}

	@Test
	public void getAssetByTypeTest() throws SQLException {
		AssetEntity asset1 = new AssetEntity("AAPL", AssetType.CALL, 150.0, Date.valueOf("2023-01-01"), 0.05, 0.1);
		AssetEntity asset2 = new AssetEntity("GOOGL", AssetType.CALL, 200.0, Date.valueOf("2023-02-01"), 0.06, 0.15);

		database.insertAsset(asset1);
		database.insertAsset(asset2);

		List<AssetEntity> callAssets = database.getAssetsByType(AssetType.CALL);
		assertEquals(2, callAssets.size());
	}

	@Test
	public void getAssetByTickerAndTypeMissingTickerTest() throws SQLException {
		AssetEntity asset1 = new AssetEntity("AAPL", AssetType.CALL, 150.0, Date.valueOf("2023-01-01"), 0.05, 0.1);
		AssetEntity asset2 = new AssetEntity("GOOGL", AssetType.CALL, 200.0, Date.valueOf("2023-02-01"), 0.06, 0.15);

		// Insert assets
		database.insertAsset(asset1);
		database.insertAsset(asset2);

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			database.getAssetByTickerAndType(null, AssetType.CALL);
		});

		assertEquals("Ticker is required", exception.getMessage());
	}

	@Test
	public void getAssetByTickerAndTypeMissingTypeTest() throws SQLException {
		AssetEntity asset1 = new AssetEntity("AAPL", AssetType.CALL, 150.0, Date.valueOf("2023-01-01"), 0.05, 0.1);
		AssetEntity asset2 = new AssetEntity("GOOGL", AssetType.CALL, 200.0, Date.valueOf("2023-02-01"), 0.06, 0.15);

		// Insert assets
		database.insertAsset(asset1);
		database.insertAsset(asset2);

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			database.getAssetByTickerAndType("AAPL", null);
		});

		assertEquals("Asset type is required", exception.getMessage());
	}

	@Test
	public void insertInvalidAssetTest() {
		AssetEntity invalidAsset = new AssetEntity("", AssetType.CALL, 150.0, Date.valueOf("2023-01-01"), 0.05, 0.1);

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			database.insertAsset(invalidAsset);
		});

		assertEquals("Asset ticker is required", exception.getMessage());
	}

	@Test
	public void getAssetByTickerAndTypeMissingAssetTest() throws SQLException {
		AssetEntity asset = database.getAssetByTickerAndType("INVALID", AssetType.STOCK);
		assertNull(asset);
	}
}
