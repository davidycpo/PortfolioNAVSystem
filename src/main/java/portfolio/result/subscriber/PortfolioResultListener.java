package portfolio.result.subscriber;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.PortfolioNAVOuterClass;

public class PortfolioResultListener {
	private static final Map<String, Integer> HEADERS_SPACING_MAP;
	private static final List<Integer> COLUMN_SPACING_LIST;
	private static final int TOTAL_SPACING;
	private static final String TOTAL_PORTFOLIO_STR = "#Total Portfolio";
	private static final DecimalFormat df = new DecimalFormat("#,##0.00");

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

	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		try (ServerSocket serverSocket = new ServerSocket(4444)) {
			while (true) {
				try (Socket socket = serverSocket.accept();
						ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
					byte[] data = (byte[]) input.readObject();
					PortfolioNAVOuterClass.PortfolioNAV portfolioNAV = PortfolioNAVOuterClass.PortfolioNAV
							.parseFrom(data);

					// Print Headers
					System.out.println("## " + portfolioNAV.getPriceChangeCount() + " Market Data Update");
					System.out.println(portfolioNAV.getPriceChangeTicker() + " change to "
							+ df.format(portfolioNAV.getPriceChangeValue()) + "\n");
					System.out.println("## Portfolio");

					for (Map.Entry<String, Integer> entry : HEADERS_SPACING_MAP.entrySet()) {
						if ("symbol".equals(entry.getKey())) {
							sb.append(String.format("%-" + entry.getValue() + "s", entry.getKey()));
							continue;
						}
						sb.append(String.format("%" + entry.getValue() + "s", entry.getKey()));
					}
					sb.append("\n");
					System.out.println(sb);
					sb.setLength(0);

					// Print Holdings
					List<PortfolioNAVOuterClass.HoldingNAV> holdings = portfolioNAV.getHoldingList();
					for (PortfolioNAVOuterClass.HoldingNAV holding : holdings) {
						sb.append(String.format("%-" + COLUMN_SPACING_LIST.get(0) + "s", holding.getSymbol()));
						sb.append(String.format("%" + COLUMN_SPACING_LIST.get(1) + "s", df.format(holding.getPrice())));
						sb.append(String.format("%" + COLUMN_SPACING_LIST.get(2) + "s",
								df.format(holding.getQuantity())));
						sb.append(String.format("%" + COLUMN_SPACING_LIST.get(3) + "s", df.format(holding.getValue())));
						sb.append("\n");
						System.out.println(sb);
						sb.setLength(0);
					}

					// Print Portfolio NAV
					sb.append("\n" + TOTAL_PORTFOLIO_STR);
					sb.append(String.format("%" + (TOTAL_SPACING - TOTAL_PORTFOLIO_STR.length()) + "s",
							df.format(portfolioNAV.getValue())));
					sb.append("\n");
					System.out.println(sb);
					sb.setLength(0);

				} catch (ClassNotFoundException e) {
					System.err.println("Failed to parse priceChange, error: " + e.getMessage());
				}
			}
		} catch (IOException e) {
			System.err.println("Failed to open socket, error: " + e.getMessage());
		}
	}
}
