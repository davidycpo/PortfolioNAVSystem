package service;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;

import model.Asset;
import model.AssetType;
import model.Holding;
import model.Portfolio;
import utils.Utils;

public class CsvReader {

	private static final String SYMBOL_SEPARATOR = "-";
	private static final String CELL_SEPARATOR = ",";
	private static final String CALL_OPTION_STR = "C";
	private static final String PUT_OPTION_TYPE = "P";

	public Portfolio parseCSV(final File csvFile) throws IOException {
		System.out.println("importing position file");
		List<String> lines = Files.readLines(csvFile, Charsets.UTF_8);
		int count = 0;
		Portfolio portfolio = new Portfolio();
		for (String line : lines) {
			// skip first line
			if (count == 0) {
				count++;
				continue;
			}
			List<String> cell = Splitter.on(CELL_SEPARATOR).trimResults().splitToList(line);
			if (cell.size() != 2) {
				System.out.println("Invalid line: " + line + " line #" + count);
				count++;
				continue;
			}
			String symbol = cell.get(0);
			String ticker;
			AssetType assetType;
			Date maturityDate = null;
			Double strike = null;
			double position;
			if (symbol.contains(SYMBOL_SEPARATOR)) {
				// An Option
				List<String> symbolList = Splitter.on("-").trimResults().splitToList(symbol);
				if (symbolList.size() != 5) {
					System.out.println("Invalid symbol: " + symbol + " line #" + count);
					count++;
					continue;
				}
				ticker = symbolList.get(0);
				String maturityMonth = symbolList.get(1);
				String maturityYear = symbolList.get(2);
				String strikeStr = symbolList.get(3);
				String typeStr = symbolList.get(4);

				String maturityDateStr = String.join(" ", maturityMonth, maturityYear);
				try {
					maturityDate = Utils.parseDate(maturityDateStr);
				} catch (ParseException e) {
					System.out.println("Failed to parse maturityDate: " + maturityDateStr + " line #" + count
							+ " error: " + e.getMessage());
					count++;
					continue;
				}

				try {
					strike = Double.valueOf(strikeStr);
				} catch (NumberFormatException e) {
					System.out.println("Invalid strike: " + strikeStr + " line #" + count);
					count++;
					continue;
				}

				if (CALL_OPTION_STR.equalsIgnoreCase(typeStr)) {
					assetType = AssetType.CALL;
				} else if (PUT_OPTION_TYPE.equalsIgnoreCase(typeStr)) {
					assetType = AssetType.PUT;
				} else {
					System.out.println("Invalid option type: " + typeStr + " line #" + count);
					count++;
					continue;
				}

			} else {
				// A Common Stock
				ticker = symbol;
				assetType = AssetType.STOCK;
			}
			// Position
			String positionStr = cell.get(1);
			try {
				position = Double.parseDouble(positionStr);
			} catch (NumberFormatException e) {
				System.out.println("Invalid position: " + positionStr + " line #" + count);
				count++;
				continue;
			}
			Asset asset = new Asset(ticker, assetType, strike, maturityDate);
			Holding holding = new Holding(asset, position);
			holding.setTicker(asset);
			portfolio.addHolding(holding);
			count++;
		}
		return portfolio;
	}
}
