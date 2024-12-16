package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.text.ParseException;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

import model.AssetEntity;
import model.AssetType;
import model.Stock;

public class UtilsTest {

	@Test
	public void isBlankTest() {
		assertTrue(Utils.isBlank(null));
		assertTrue(Utils.isBlank(""));
		assertTrue(Utils.isBlank("   "));
		assertFalse(Utils.isBlank("text"));
	}

	@Test
	public void parseDateValidDateTest() throws ParseException {
		String dateStr = "Jan 2025";
		Date date = Utils.parseDate(dateStr);
		assertNotNull(date);
		assertEquals(Date.valueOf("2025-01-01"), date);
	}

	@Test
	public void parseDateInvalidDateTest() {
		String invalidDateStr = "Invalid Date";
		assertThrows(DateTimeParseException.class, () -> Utils.parseDate(invalidDateStr));
	}

	@Test
	public void parseStringTest() {
		Date date = Date.valueOf("2025-01-01");
		String formattedDate = Utils.parseString(date);
		assertEquals("Jan-2025", formattedDate);
	}

	@Test
	public void getRandomDoubleTest() {
		double min = 1.0;
		double max = 5.0;
		double randomValue = Utils.getRandomDouble(min, max);
		assertTrue(randomValue >= min && randomValue < max);
	}

	@Test
	public void cumulativeProbabilityTest() {
		double x = 0.0; // Standard normal
		double probability = Utils.getCumulativeProbability(x);
		assertEquals(0.5, probability, 0.01);
	}

	@Test
	public void calculateYearsUntilTest() {
		Date futureDate = Date.valueOf("2025-01-01");
		double yearsUntil = Utils.calculateYearsUntil(futureDate);
		assertTrue(yearsUntil > 0);
	}

	@Test
	public void calculateHoldingPriceStockTest() {
		AssetEntity asset = new AssetEntity("AAPL", AssetType.STOCK, 150.0, null, 0.05, 0.1);
		asset.setPrice(150.0);

		double price = Utils.calculateHoldingPrice(asset);
		assertEquals(150.0, price);
	}

	@Test
	public void calculateHoldingPriceCallOptionStrikeLessThanPriceTest() {
		AssetEntity asset = new AssetEntity("AAPL", AssetType.CALL, 150.0, Date.valueOf("2025-01-01"), 0.05, 0.1);
		asset.setPrice(200.0);
		double price = Utils.calculateHoldingPrice(asset);
		assertEquals(50.12, price, 0.01);
	}

	@Test
	public void calculateHoldingPriceCallOptionStrikeLargerThanPriceTest() {
		AssetEntity asset = new AssetEntity("AAPL", AssetType.CALL, 150.0, Date.valueOf("2025-01-01"), 0.05, 0.1);
		asset.setPrice(100.0);
		double price = Utils.calculateHoldingPrice(asset);
		assertEquals(0.0, price, 0.01);
	}

	@Test
	public void calculateHoldingPricePutOptionStrikeLessThanPriceTest() {
		AssetEntity asset = new AssetEntity("AAPL", AssetType.PUT, 150.0, Date.valueOf("2025-01-01"), 0.05, 0.1);
		asset.setPrice(200d);
		double price = Utils.calculateHoldingPrice(asset);
		assertEquals(0.0, price, 0.01);
	}

	@Test
	public void calculateHoldingPricePutOptionStrikeLargerThanPriceTest() {
		AssetEntity asset = new AssetEntity("AAPL", AssetType.PUT, 234.0, Date.valueOf("2025-01-01"), 0.3, 0.25);
		asset.setPrice(100.0);
		double price = Utils.calculateHoldingPrice(asset);
		assertEquals(133.80, price, 0.01);
	}

	@Test
	public void toStockTest() {
		AssetEntity asset = new AssetEntity("AAPL", AssetType.STOCK, 150.0, null, 0.05, 0.1);
		Stock stock = Utils.toStock(asset, 100.0, 200.0);
		assertNotNull(stock);
		assertEquals("AAPL", stock.getTicker());
		assertTrue(stock.getPrice() >= 100.0 && stock.getPrice() <= 200.0);
	}
}
