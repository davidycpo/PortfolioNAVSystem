package database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.AssetEntity;
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
				+ "strike REAL," + "maturityDate TEXT, " + "expectedReturn REAL," + "annualizedStandardDeviation REAL,"
				+ "PRIMARY KEY (ticker, assetType));";
		try (Statement statement = connection.createStatement()) {
			statement.execute(createTableSQL);
		}
		System.out.println("Finished initializing database..." + "\n");
	}

	public void insertAsset(AssetEntity assetEntity) throws SQLException {
		if (Utils.isBlank(assetEntity.getTicker())) {
			throw new IllegalArgumentException("Asset ticker is required");
		}

		if (getAssetByTickerAndType(assetEntity.getTicker(), assetEntity.getAssetType()) != null) {
			System.out.println("Asset with Ticker: " + assetEntity.getTicker() + " and Asset Type: "
					+ assetEntity.getAssetType() + " already exists");
			return;
		}

		if (assetEntity.getAssetType() == null) {
			throw new IllegalArgumentException("Asset Type is required");
		}

		if (assetEntity.getAssetType() == AssetType.CALL || assetEntity.getAssetType() == AssetType.PUT) {
			if (assetEntity.getStrike() == null) {
				throw new IllegalArgumentException("Strike is required");
			}
			if (assetEntity.getMaturityDate() == null) {
				throw new IllegalArgumentException("Maturity Date is required");
			}
		}

		String insertSQL = "INSERT INTO assets (ticker, assetType, strike, maturityDate, expectedReturn, annualizedStandardDeviation) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
			statement.setString(1, assetEntity.getTicker());
			statement.setString(2, assetEntity.getAssetType().toString());
			if (assetEntity.getStrike() != null) {
				statement.setDouble(3, assetEntity.getStrike());
			}
			if (assetEntity.getMaturityDate() != null) {
				statement.setString(4, assetEntity.getMaturityDate().toString());
			}
			statement.setDouble(5, Math.random());
			statement.setDouble(6, Math.random());
			statement.execute();
		}

		System.out.println(
				"Inserted Asset with ticker " + assetEntity.getTicker() + " type " + assetEntity.getAssetType());
	}

	public AssetEntity getAssetByTickerAndType(final String ticker, final AssetType type) throws SQLException {
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
				Double expectedReturn = result.getDouble(5);
				Double annualizedStandardDeviation = result.getDouble(6);
				return new AssetEntity(tickerDB, assetType, strike, maturityDate, expectedReturn,
						annualizedStandardDeviation);
			}
			System.out.println("Asset not found with ticker: " + ticker + " and assetType: " + type);
			return null;
		}
	}

	public List<AssetEntity> getAssetsByType(final AssetType type) throws SQLException {
		String selectSQL = "SELECT * FROM assets WHERE assetType = ?";
		List<AssetEntity> assetEntities = new ArrayList<>();
		try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
			statement.setString(1, type.toString());
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				String tickerDB = result.getString(1);
				AssetType assetType = AssetType.valueOf(result.getString(2));
				Double strike = result.getDouble(3);
				Date maturityDate = result.getDate(4);
				Double expectedReturn = result.getDouble(5);
				Double annualizedStandardDeviation = result.getDouble(6);
				assetEntities.add(new AssetEntity(tickerDB, assetType, strike, maturityDate, expectedReturn,
						annualizedStandardDeviation));
			}
		}
		System.out.println("Found " + assetEntities.size() + " " + type + " assets \n");
		return assetEntities;
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
