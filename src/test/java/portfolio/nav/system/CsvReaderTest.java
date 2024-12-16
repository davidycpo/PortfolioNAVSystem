package portfolio.nav.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.AssetType;
import model.Holding;
import model.Portfolio;

public class CsvReaderTest {

	private CsvReader csvReader;

	@BeforeEach
	public void setUp() {
		csvReader = new CsvReader();
	}

	@Test
	public void parseValidCsvFileTest() throws IOException {
		// Prepare a valid CSV file
		File csvFile = createTempCsvFile(
				"symbol,positionSize\n" + "AAPL-JAN-2025-150-C,10\n" + "GOOGL,5\n" + "TELSA-MAR-2025-150-P,5\n");

		Portfolio portfolio = csvReader.parseCSV(csvFile);

		assertNotNull(portfolio);
		assertEquals(3, portfolio.getHoldings().size());

		Holding holding1 = portfolio.getHoldings().get(0);
		assertEquals("AAPL", holding1.getAsset().getTicker());
		assertEquals(AssetType.CALL, holding1.getAsset().getAssetType());
		assertEquals(150.0, holding1.getAsset().getStrike());
		assertEquals(10, holding1.getPosition());
		assertEquals("2025-01-01", holding1.getAsset().getMaturityDate().toString());

		Holding holding2 = portfolio.getHoldings().get(1);
		assertEquals("GOOGL", holding2.getAsset().getTicker());
		assertEquals(AssetType.STOCK, holding2.getAsset().getAssetType());
		assertEquals(5, holding2.getPosition());
		assertNull(holding2.getAsset().getMaturityDate());

		Holding holding3 = portfolio.getHoldings().get(2);
		assertEquals("TELSA", holding3.getAsset().getTicker());
		assertEquals(AssetType.PUT, holding3.getAsset().getAssetType());
		assertEquals(150.0, holding3.getAsset().getStrike());
		assertEquals(5, holding3.getPosition());
		assertEquals("2025-03-01", holding3.getAsset().getMaturityDate().toString());
	}

	@Test
	public void parseCsvFileInvalidOptionTypeTest() throws IOException {
		File csvFile = createTempCsvFile("symbol,positionSize\n" + "AAPL,10\n" + "AAPL-JAN-2025-150-X,5");

		Portfolio portfolio = csvReader.parseCSV(csvFile);

		assertNotNull(portfolio);
		assertEquals(1, portfolio.getHoldings().size());
		assertEquals("AAPL", portfolio.getHoldings().get(0).getAsset().getTicker());
		assertEquals(10, portfolio.getHoldings().get(0).getPosition());
	}

	@Test
	public void parseCsvFileInvalidPositionTest() throws IOException {
		File csvFile = createTempCsvFile("symbol,positionSize\n" + "AAPL-DEC-2025-150-C,INVALID\n" + "GOOGL,5");

		Portfolio portfolio = csvReader.parseCSV(csvFile);

		assertNotNull(portfolio);
		assertEquals(1, portfolio.getHoldings().size());
		assertEquals("GOOGL", portfolio.getHoldings().get(0).getAsset().getTicker());
		assertEquals(5, portfolio.getHoldings().get(0).getPosition());
	}

	@Test
	public void parseCsvFileInvalidStrikeTest() throws IOException {
		File csvFile = createTempCsvFile("symbol,positionSize\n" + "AAPL,10\n" + "AAPL-JAN-2025-INVALID-C,5");

		Portfolio portfolio = csvReader.parseCSV(csvFile);

		assertNotNull(portfolio);
		assertEquals(1, portfolio.getHoldings().size());
		assertEquals("AAPL", portfolio.getHoldings().get(0).getAsset().getTicker());
		assertEquals(10, portfolio.getHoldings().get(0).getPosition());
	}

	@Test
	public void parseCsvFileInvalidMaturityDateTest() throws IOException {
		File csvFile = createTempCsvFile("symbol,positionSize\n" + "AAPL,10\n" + "AAPL-01-2025-300-C,5");

		Portfolio portfolio = csvReader.parseCSV(csvFile);

		assertNotNull(portfolio);
		assertEquals(1, portfolio.getHoldings().size());
		assertEquals("AAPL", portfolio.getHoldings().get(0).getAsset().getTicker());
		assertEquals(10, portfolio.getHoldings().get(0).getPosition());
	}

	@Test
	public void parseCsvFileEmptyCsvTest() throws IOException {
		File csvFile = createTempCsvFile("");

		Portfolio portfolio = csvReader.parseCSV(csvFile);

		assertNotNull(portfolio);
		assertNull(portfolio.getHoldings());
	}

	// Helper method to create a temporary CSV file
	private File createTempCsvFile(String content) throws IOException {
		File tempFile = File.createTempFile("test", ".csv");
		tempFile.deleteOnExit();
		Files.write(tempFile.toPath(), content.getBytes());
		return tempFile;
	}
}
