package auth.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockPriceApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(StockPriceApplication.class, args);

		//new CrawlerManager().startCrawling("https://www.google.com/");
	}

}
