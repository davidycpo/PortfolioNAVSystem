package portfolio.result.subscriber;

public class PortfolioResultListener {
	public static void main(String[] args) {
		PortfolioResultListenerService service = new PortfolioResultListenerService();
		service.consumePortfolioNAVResult();
	}
}
