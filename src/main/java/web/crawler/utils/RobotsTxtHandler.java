package web.crawler.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Slf4j
public class RobotsTxtHandler {
    private static final Map<String, Set<String>> disallowMap = new HashMap<>();

    public static boolean isAllowed(String utlStr){
        try{
            URL url = new URL(utlStr);
            String host = url.getHost();
            if(!disallowMap.containsKey(host)){
                fetchRobotsTxt(host);
            }
            String path = url.getPath();
            for(String disallowed : disallowMap.getOrDefault(host,new HashSet<>())){
                if(path.startsWith(disallowed)){
                    return false;
                }
            }
            return true;
        }
        catch (Exception e){
            log.error("Exception at RobotsTxtHandler {}",e.getMessage());
            return  true;
        }
    }
    private static void fetchRobotsTxt(String host){
        Set<String>disallowedPaths = new HashSet<>();
        try{
            URL robotsURL = new URL("https://"+host+"/robots.txt");
            HttpURLConnection connection = (HttpURLConnection) robotsURL.openConnection();
            connection.setRequestProperty("User-Agent","MyWebCrawler");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            boolean applicable = false;
            while((line=reader.readLine())!=null){
                line = line.trim();
                if(line.startsWith("User-agent:")){
                    applicable = line.split(":")[1].trim().equals("*");
                }else if(applicable && line.startsWith("Disallow:")){
                    String disallowedPath = line.split(":",2)[1].trim();
                    if(!disallowedPath.isEmpty()){
                        disallowedPaths.add(disallowedPath);
                    }
                }
            }
            reader.close();
        }
        catch (Exception e){
            log.error("Ex");
            e.printStackTrace();
        }
        disallowMap.put(host,disallowedPaths);
    }

}
