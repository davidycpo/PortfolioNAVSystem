package portfolio.nav.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.Database;
import model.AssetEntity;
import model.AssetType;
import model.Holding;
import model.Portfolio;

public class PortfolioNavSystemServiceTest {

	private PortfolioNavSystemService service;
	private Portfolio portfolio;
	private Database database;
	public static final String TEST_DB_PATH = "jdbc:sqlite:src/test/resources/test.sqlite";

	@BeforeEach
	public void setUp() throws SQLException {
		service = new PortfolioNavSystemService();
		portfolio = new Portfolio();
		database = new Database(TEST_DB_PATH);
		database.initializeDatabase();
	}

	@AfterEach
	public void tearDown() throws SQLException {
		database.purgeDatabase();
		database.close();
	}

	@Test
	public void recalculatePortfolioNAVOnPriceChangeTest() {
		Holding holding = new Holding(new AssetEntity("AAPL", AssetType.STOCK, 150.0, null, 0.05, 0.1), 10);
		holding.getAsset().setPrice(500.0);
		portfolio.addHolding(holding);

		service.recalculatePortfolioNAVOnPriceChange(portfolio, "AAPL", 160.0);

		assertEquals(1600.0, holding.getValue());
	}

	@Test
	public void calculationPortfolioNAVTest() {
		Holding holding = new Holding(new AssetEntity("GOOGL", AssetType.STOCK, 0.0, null, 0.06, 0.15), 5);
		holding.getAsset().setPrice(1000.0);
		portfolio.addHolding(holding);

		service.calculatePortfolioNAV(portfolio);

		assertEquals(5000.0, holding.getValue());
	}

	@Test
	public void parsePortfolioFromCSVTest() {
		File csvFile = new File("src/test/resources/position.csv");

		// Act
		Portfolio resultPortfolio = service.parsePortfolioFromCSV(csvFile);

		// Assert
		assertNotNull(resultPortfolio);
		assertFalse(resultPortfolio.getHoldings().isEmpty());
		assertEquals(6, resultPortfolio.getHoldings().size());
	}
}
