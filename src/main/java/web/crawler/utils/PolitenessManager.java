package web.crawler.utils;

import java.util.HashMap;
import java.util.Map;

public class PolitenessManager {
    private static  final Map<String,Long>lastAccessMap = new HashMap<>();
    private static final long DELAY_MS = 2000;

    public static synchronized  void waitIfNeeded(String domain){
        long currentTime = System.currentTimeMillis();
        long lastAccessTime = lastAccessMap.getOrDefault(domain,0L);
        long waitTime = DELAY_MS - (currentTime-lastAccessTime);
        if(waitTime>0){
            try{
                Thread.sleep(waitTime);
            }catch (InterruptedException ignored){}
        }
        lastAccessMap.put(domain,System.currentTimeMillis());
    }
}
