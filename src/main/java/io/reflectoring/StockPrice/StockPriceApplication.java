package io.reflectoring.StockPrice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import web.crawler.Thread.CrawlerManager;

@SpringBootApplication
public class StockPriceApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(StockPriceApplication.class, args);
		new CrawlerManager().startCrawling("https://www.google.com/");
	}

}
