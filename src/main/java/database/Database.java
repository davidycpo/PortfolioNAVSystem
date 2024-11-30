package database;

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
		String createTableSQL = "CREATE TABLE IF NOT EXISTS assets " + "(ticker TEXT, " + "assetType TEXT NOT NULL, "
				+ "strike REAL," + "maturityDate REAL, " + "PRIMARY KEY (ticker, assetType));";
		try (Statement statement = connection.createStatement()) {
			statement.execute(createTableSQL);
		}
		System.out.println("Finished initializing database...");
	}

	public void insertAsset(Asset asset) throws SQLException {
		if (Utils.isBlank(asset.getTicker())) {
			throw new IllegalArgumentException("Asset ticker is required");
		}

		if (getAssetByTickerAndType(asset.getTicker(), asset.getAssetType()) != null) {
			System.out.println("Asset with Ticker: " + asset.getTicker() + " and Asset Type: " + asset.getAssetType()
					+ " already exists");
			return;
		}

		if (asset.getAssetType() == null) {
			throw new IllegalArgumentException("Asset Type is required");
		}

		if (asset.getAssetType() == AssetType.CALL || asset.getAssetType() == AssetType.PUT) {
			if (asset.getStrike() == null) {
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
			if (asset.getStrike() != null) {
				statement.setDouble(3, asset.getStrike());
			}
			if (asset.getMaturityDate() != null) {
				statement.setDate(4, asset.getMaturityDate());
			}
			statement.execute();
		}

		System.out.println("Inserted Asset with ticker " + asset.getTicker() + " type " + asset.getAssetType());
	}

	public Asset getAssetByTickerAndType(final String ticker, final AssetType type) throws SQLException {
		if (ticker == null) {
			throw new IllegalArgumentException("Ticker is required");
		}
		if (type == null) {
			throw new IllegalArgumentException("Asset type is required");
		}
		String selectSQL = "SELECT * FROM assets WHERE ticker = ? and assetType = ?";
		try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
			statement.setString(1, ticker);
			statement.setString(2, type.toString());
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				String tickerDB = result.getString(1);
				AssetType assetType = AssetType.valueOf(result.getString(2));
				Double strike = result.getDouble(3);
				Date maturityDate = result.getDate(4);
				return new Asset(tickerDB, assetType, strike, maturityDate);
			}
			System.out.println("Asset not found with ticker: " + ticker + " and assetType: " + type);
			return null;
		}
	}

	public void close() throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

	public void purgeDatabase() throws SQLException {
		System.out.println("Purging database...");
		String deleteSQL = "DELETE FROM assets";
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(deleteSQL);
		}
	}
}
