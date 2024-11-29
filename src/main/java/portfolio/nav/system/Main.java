package portfolio.nav.system;

import java.sql.SQLException;

import config.Database;
import model.Asset;
import model.AssetType;

public class Main {
	public static void main(String[] args) {
		testDB();
		System.out.println("import position file");

	}

	private static void testDB() {
		String dbUrl = "jdbc:sqlite:asset.sqlite";
		Database database = null;
		try {
			database = new Database(dbUrl);
			Asset asset = new Asset("AAPL", AssetType.STOCK, 0d, null);
			database.insertAsset(asset);
			Asset apple = database.getAsset("AAPL");
			System.out.println(apple);

		} catch (SQLException e) {
			System.out.println("Failed to test db" + e.getMessage());
		} finally {
			if (database != null) {
				try {
					database.close();
				} catch (SQLException e) {
					System.out.println("Failed to close db" + e.getMessage());
				}
			}
		}
	}
}
