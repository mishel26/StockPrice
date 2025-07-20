package service;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
public class StockFetcher {

    //@Value("${alphaVantage.API.KEY}")
    private static String alphaApiKey = "YPVOPCO6V4KA9WJ3";

    //@Value("${alphaVantage.url}")
    private static String alphaVantageURL = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s";

    public static void fetchStockPrice(String stockName){
        HttpClient httpClient = HttpClient.newHttpClient();
        String url = String.format(alphaVantageURL,stockName,alphaApiKey);

        HttpRequest request =HttpRequest.newBuilder().uri(URI.create(url))
                .GET().build();

        try{
            HttpResponse<String>response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
            String responseData = response.body();
            JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
            JsonObject globalQuote = jsonObject.getAsJsonObject("Global Quote");
            if(globalQuote == null || globalQuote.size()==0){
                log.error("Invalid stockName or data is unavailable {}",stockName);
            }
            String price = globalQuote.get("05. price").getAsString();
            long timeStamp = System.currentTimeMillis();
            String data = String.format("[%s] %s → ₹%s", timeStamp, stockName, price);

            log.info("[ {} ] {} -> {}, {}",timeStamp,stockName,price,globalQuote);

            try(BufferedWriter writer = new BufferedWriter(new FileWriter("stock_prices.txt", true))){
                writer.write(data);
                writer.newLine();
            }
        }
        catch (Exception e){
                e.printStackTrace();
        }
    }

    public static void downloadSymbols(){
        try {
            String url = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=INFY&apikey=%s";
             url = String.format(url,alphaApiKey);
            String outputFile = "EQUITY_L.csv";
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).followRedirects(HttpClient.Redirect.ALWAYS).build();
            URI uri = URI.create(url);
            HttpRequest req = HttpRequest.newBuilder()
                              .uri(uri)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .header("Referer", "https://www.nseindia.com/")
                    .GET()
                    .build();
            HttpResponse<byte[]> response = client.send(req,HttpResponse.BodyHandlers.ofByteArray());
            if(response.statusCode()==200){
                Files.write(Path.of("./Symbols1.txt"),response.body());
                log.info("CSV downloaded successfully to:  Symbols.csv");

            }
            else {
                log.info("Failed to download CSV. HTTP Status: {}",response.statusCode());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        downloadSymbols();
        fetchStockPrice("RELIANCE.BSE");
        fetchStockPrice("IBM");
    }
}
