package config;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.Asset;
import model.AssetType;
import utils.Utils;

public class Database {
	private final Connection connection;

	public Database(String url) throws SQLException {
		connection = DriverManager.getConnection(url);
		initializeDatabase();
	}

	private void initializeDatabase() throws SQLException {
		System.out.println("Initializing database...");
		String createTableSQL = "CREATE TABLE IF NOT EXISTS assets (" + "ticker TEXT PRIMARY KEY,"
				+ "assetType TEXT NOT NULL," + "strike REAL," + "maturityDate REAL" + ");";
		try (Statement statement = connection.createStatement()) {
			statement.execute(createTableSQL);
		}
		System.out.println("Finished initializing database...");
	}

	public void insertAsset(Asset asset) throws SQLException {
		if (Utils.isBlank(asset.getTicker())) {
			throw new IllegalArgumentException("Asset ticker is required");
		}

		if (getAsset(asset.getTicker()) != null) {
			System.out.println("Asset " + asset.getTicker() + " already exists");
			return;
		}

		if (asset.getAssetType() == null) {
			throw new IllegalArgumentException("Asset Type is required");
		}

		if (asset.getAssetType() == AssetType.CALL_OPTION || asset.getAssetType() == AssetType.PUT_OPTION) {
			if (asset.getStrike() == 0d) {
				throw new IllegalArgumentException("Strike is required");
			}
			if (asset.getMaturityDate() == null) {
				throw new IllegalArgumentException("Maturity Date is required");
			}
		}

		String insertSQL = "INSERT INTO assets (ticker, assetType, strike, maturityDate) VALUES (?, ?, ? , ?)";
		try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
			statement.setString(1, asset.getTicker());
			statement.setString(2, asset.getAssetType().toString());
			statement.setDouble(3, asset.getStrike());
			statement.setDate(4, asset.getMaturityDate());
			statement.execute();
		}

		System.out.println("Inserted Asset with ticker " + asset.getTicker());
	}

	public Asset getAsset(final String ticker) throws SQLException {
		String selectSQL = "SELECT * FROM assets WHERE ticker = ?";
		try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
			statement.setString(1, ticker);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				String tickerDB = result.getString(1);
				AssetType assetType = AssetType.valueOf(result.getString(2));
				double strike = result.getDouble(3);
				Date maturityDate = result.getDate(4);
				return new Asset(tickerDB, assetType, strike, maturityDate);
			}
			System.out.println("Asset not found with ticker: " + ticker);
			return null;
		}
	}

	// Close the connection when done
	public void close() throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}
}
