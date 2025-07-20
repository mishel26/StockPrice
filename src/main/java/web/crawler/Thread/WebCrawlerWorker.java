package web.crawler.Thread;

import lombok.extern.slf4j.Slf4j;
import web.crawler.model.URLDepthPair;
import web.crawler.service.LinkExtractor;
import web.crawler.utils.PolitenessManager;
import web.crawler.utils.RobotsTxtHandler;

import java.net.URL;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class WebCrawlerWorker implements Runnable {
    private final BlockingQueue<URLDepthPair>queue;
    private final ConcurrentMap<String,Boolean>visited;
    private final int maxDepth;

    public WebCrawlerWorker(BlockingQueue<URLDepthPair>queue, ConcurrentMap<String,Boolean>visited,int maxDepth){
        this.queue = queue;
        this.visited = visited;
        this.maxDepth = maxDepth;
    }

    @Override
    public void run() {
        while (true){
            try{
                URLDepthPair pair = queue.take();
                if(pair.getDepth()>=maxDepth)continue;
             if(! RobotsTxtHandler.isAllowed(pair.getUrl().toString())){
                 log.error("Blocked by robots.txt: {}",pair.getUrl().toString());
                 continue;
             }
             String domain = pair.getUrl().getHost();
                PolitenessManager.waitIfNeeded(domain);
                Set<URL> links = LinkExtractor.extractLinks(pair.getUrl());
                for(URL link : links){
                    String linkStr = link.toString();
                    if(visited.putIfAbsent(linkStr,true)==null){
                        queue.put(new URLDepthPair(link,pair.getDepth()+1));
                        log.info("[Thread: {}] Discovered: {}",Thread.currentThread().getName(), linkStr);

                    }
                }
            }catch (Exception e){
                log.error("Exception at webcrawlerworker {}",e.getMessage());
            }
        }
    }
}
