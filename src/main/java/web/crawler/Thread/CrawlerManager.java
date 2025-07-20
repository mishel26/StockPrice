package web.crawler.Thread;

import lombok.extern.slf4j.Slf4j;
import web.crawler.model.URLDepthPair;

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.*;


@Slf4j
public class CrawlerManager {
    private final int maxDepth = 2;
    private final int numThreads = 5;
    public void startCrawling(String startUrlStr) throws Exception {
        try {
            BlockingQueue<URLDepthPair> queue = new LinkedBlockingQueue<>();
            ConcurrentMap<String, Boolean> visited = new ConcurrentHashMap<>();

            URL startUrl = new URL(startUrlStr);
            visited.put(startUrl.toString(), true);
            queue.put(new URLDepthPair(startUrl, 0));

            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
            for (int i = 0; i < numThreads; i++) {
                executorService.submit(new WebCrawlerWorker(queue, visited, maxDepth));
            }

            Thread.sleep(10000);
            executorService.shutdown();

        }catch (Exception e){
            log.error("Exception at startCrawling {} : {}",startUrlStr,e.getMessage());
        }




    }
}
