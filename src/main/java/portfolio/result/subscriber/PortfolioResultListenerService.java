package portfolio.result.subscriber;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.PortfolioNavResult;
import utils.Settings;

public class PortfolioResultListenerService {
	private static final Map<String, Integer> HEADERS_SPACING_MAP;
	private static final List<Integer> COLUMN_SPACING_LIST;
	private static final int TOTAL_SPACING;
	private static final String TOTAL_PORTFOLIO_STR = "#Total Portfolio";
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(Settings.DECIMAL_FORMAT_PATTERN);
	private static final StringBuilder STRING_BUILDER = new StringBuilder();

	static {
		Map<String, Integer> tmp = new HashMap<>();
		tmp.put("symbol", 20);
		tmp.put("price", 30);
		tmp.put("qty", 30);
		tmp.put("value", 30);
		HEADERS_SPACING_MAP = Collections.unmodifiableMap(tmp);
		COLUMN_SPACING_LIST = Collections.unmodifiableList(new ArrayList<>(tmp.values()));
		TOTAL_SPACING = COLUMN_SPACING_LIST.stream().mapToInt(Integer::intValue).sum();
	}

	public void consumePortfolioNAVResult() {
		// Portfolio NAV Result Listener

		try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
			serverSocketChannel.bind(new InetSocketAddress(Settings.PORTFOLIO_NAV_RESULT_PORT));
			try (SocketChannel socketChannel = serverSocketChannel.accept()) {

				while (true) {
					ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
					socketChannel.read(lengthBuffer);
					lengthBuffer.flip();
					int messageLength = lengthBuffer.getInt();

					// Read the actual message
					ByteBuffer messageBuffer = ByteBuffer.allocate(messageLength);
					socketChannel.read(messageBuffer);
					messageBuffer.flip();

					PortfolioNavResult.PortfolioNAVResult portfolioNAV = PortfolioNavResult.PortfolioNAVResult
							.parseFrom(messageBuffer.array());

					// Print Headers
					System.out.println("## " + portfolioNAV.getPriceChangeCount() + " Market Data Update");
					System.out.println(portfolioNAV.getPriceChangeTicker() + " change to "
							+ DECIMAL_FORMAT.format(portfolioNAV.getPriceChangeValue()) + "\n");
					System.out.println("## Portfolio");

					for (Map.Entry<String, Integer> entry : HEADERS_SPACING_MAP.entrySet()) {
						if ("symbol".equals(entry.getKey())) {
							STRING_BUILDER.append(String.format("%-" + entry.getValue() + "s", entry.getKey()));
							continue;
						}
						STRING_BUILDER.append(String.format("%" + entry.getValue() + "s", entry.getKey()));
					}
					STRING_BUILDER.append("\n");
					System.out.println(STRING_BUILDER);
					STRING_BUILDER.setLength(0);

					// Print Holdings
					List<PortfolioNavResult.HoldingNAV> holdings = portfolioNAV.getHoldingList();
					for (PortfolioNavResult.HoldingNAV holding : holdings) {
						STRING_BUILDER
								.append(String.format("%-" + COLUMN_SPACING_LIST.get(0) + "s", holding.getSymbol()));
						STRING_BUILDER.append(String.format("%" + COLUMN_SPACING_LIST.get(1) + "s",
								DECIMAL_FORMAT.format(holding.getPrice())));
						STRING_BUILDER.append(String.format("%" + COLUMN_SPACING_LIST.get(2) + "s",
								DECIMAL_FORMAT.format(holding.getQuantity())));
						STRING_BUILDER.append(String.format("%" + COLUMN_SPACING_LIST.get(3) + "s",
								DECIMAL_FORMAT.format(holding.getValue())));
						STRING_BUILDER.append("\n");
						System.out.println(STRING_BUILDER);
						STRING_BUILDER.setLength(0);
					}

					// Print Portfolio NAV
					STRING_BUILDER.append("\n" + TOTAL_PORTFOLIO_STR);
					STRING_BUILDER.append(String.format("%" + (TOTAL_SPACING - TOTAL_PORTFOLIO_STR.length()) + "s",
							DECIMAL_FORMAT.format(portfolioNAV.getValue())));
					STRING_BUILDER.append("\n");
					System.out.println(STRING_BUILDER);
					STRING_BUILDER.setLength(0);
				}
			}
		} catch (IOException e) {
			System.err.println("Failed to open socket, error: " + e.getMessage());
		}
	}
}
