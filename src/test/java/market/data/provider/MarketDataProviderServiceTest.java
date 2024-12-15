package market.data.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import database.Database;
import model.AssetEntity;
import model.AssetType;
import model.Stock;
import utils.Settings;

public class MarketDataProviderServiceTest {

	private static final ByteBuffer PRICE_CHANGE_BUFFER = ByteBuffer.allocate(Settings.PRICE_CHANGE_BUFFER_SIZE);
	private static final byte[] SYMBOL_BYTES = new byte[6];
	public static final String TEST_DB_PATH = "jdbc:sqlite:src/test/resources/test.sqlite";

	private MarketDataProviderService service;
	private Database database;
	private TestSocketChannel testSocketChannel;

	@BeforeEach
	public void setUp() throws SQLException {
		// Set up in-memory SQLite database
		testSocketChannel = new TestSocketChannel();
		database = new Database(TEST_DB_PATH);
		database.initializeDatabase();
		database.insertAsset(new AssetEntity("AAPL", AssetType.STOCK, 150.0, null, 0.05, 0.1));
		database.insertAsset(new AssetEntity("GOOGL", AssetType.STOCK, 200.0, null, 0.06, 0.15));
		service = new MarketDataProviderService();
	}

	@AfterEach
	public void tearDown() throws SQLException {
		database.purgeDatabase();
		database.close();
	}

	@Test
	public void getStockListFromDBTest() {
		System.out.println("getStockListFromDB");
		List<Stock> stocks = service.getStockListFromDB(TEST_DB_PATH);

		assertNotNull(stocks);
		assertEquals(2, stocks.size());
		assertEquals("AAPL", stocks.get(0).getTicker());
		assertEquals("GOOGL", stocks.get(1).getTicker());
	}

	@Test
	public void simulateStockMovementTest() throws InterruptedException {
		AtomicReference<AssertionFailedError> error = new AtomicReference<>();
		Thread serverThread = new Thread(() -> {
			try (ServerSocketChannel priceChangeServerSocketChannel = ServerSocketChannel.open()) {
				priceChangeServerSocketChannel.bind(new InetSocketAddress(Settings.PRICE_CHANGE_PORT));
				// Price Change Listener
				try (SocketChannel priceChangeChannel = priceChangeServerSocketChannel.accept()) {
					PRICE_CHANGE_BUFFER.clear();
					priceChangeChannel.read(PRICE_CHANGE_BUFFER);
					PRICE_CHANGE_BUFFER.flip();
					int symbolLength = PRICE_CHANGE_BUFFER.getInt();
					PRICE_CHANGE_BUFFER.get(SYMBOL_BYTES, 0, symbolLength);
					double price = PRICE_CHANGE_BUFFER.getDouble();
					String symbol = new String(SYMBOL_BYTES, 0, symbolLength);
					assertEquals("AAPL", symbol);
					assertNotEquals(0.0, price);
				}
			} catch (IOException e) {
				System.err.println("Failed to open socket to consume price change, error: " + e.getMessage());

			} catch (AssertionFailedError e) {
				error.set(e);
			}
		});

		serverThread.start();
		// Allow some time for server thread to start
		Thread.sleep(100);

		// Create a mock stock
		Stock stock = new Stock("AAPL", 150.0, 0.05, 0.1);
		List<Stock> stocks = Arrays.asList(stock);

		Thread simulationThread = new Thread(() -> {
			// Simulate stock movement
			service.simulateStockMovement(stocks);
		});

		simulationThread.start();
		// Allow some time for simulation thread to publish
		Thread.sleep(2000);

		// Clean up threads
		simulationThread.interrupt(); // Stop the simulation thread
		simulationThread.join(); // Wait for the thread to finish
		serverThread.interrupt(); // Stop the server thread
		serverThread.join();

		assertNull(error.get());
	}

	@Test
	public void publishPriceChangeTest() throws IOException {
		Stock stock = new Stock("AAPL", 150.0, 0.05, 0.1);
		service.publishPriceChange(stock, testSocketChannel);

		ByteBuffer buffer = ByteBuffer.allocate(Settings.PRICE_CHANGE_BUFFER_SIZE);
		buffer.putInt(stock.getTicker().getBytes().length);
		buffer.put(stock.getTicker().getBytes());
		buffer.putDouble(stock.getPrice());
		buffer.flip();

		// Published one PriceChange
		assertEquals(1, testSocketChannel.publishedData.size());
		assertEquals(4 + stock.getTicker().getBytes().length + 8, testSocketChannel.publishedData.get(0).limit());
	}

	private static class TestSocketChannel extends SocketChannel {
		public List<ByteBuffer> publishedData = new ArrayList<>();

		protected TestSocketChannel() {
			super(null);
		}

		@Override
		public SocketChannel bind(SocketAddress local) {
			return null;
		}

		@Override
		public <T> SocketChannel setOption(SocketOption<T> name, T value) {
			return null;
		}

		@Override
		public <T> T getOption(SocketOption<T> name) {
			return null;
		}

		@Override
		public Set<SocketOption<?>> supportedOptions() {
			return Collections.emptySet();
		}

		@Override
		public SocketChannel shutdownInput() {
			return null;
		}

		@Override
		public SocketChannel shutdownOutput() {
			return null;
		}

		@Override
		public Socket socket() {
			return null;
		}

		@Override
		public boolean isConnected() {
			return false;
		}

		@Override
		public boolean isConnectionPending() {
			return false;
		}

		@Override
		public boolean connect(SocketAddress remote) {
			return false;
		}

		@Override
		public boolean finishConnect() {
			return false;
		}

		@Override
		public SocketAddress getRemoteAddress() {
			return null;
		}

		@Override
		public int read(ByteBuffer dst) {
			return 0;
		}

		@Override
		public long read(ByteBuffer[] dsts, int offset, int length) {
			return 0;
		}

		@Override
		public int write(ByteBuffer src) {
			publishedData.add(src);
			return src.remaining();
		}

		@Override
		public long write(ByteBuffer[] srcs, int offset, int length) {
			return 0;
		}

		@Override
		public SocketAddress getLocalAddress() {
			return null;
		}

		@Override
		protected void implCloseSelectableChannel() {

		}

		@Override
		protected void implConfigureBlocking(boolean block) {

		}
	}
}
