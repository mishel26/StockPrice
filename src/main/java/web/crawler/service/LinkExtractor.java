package web.crawler.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import web.crawler.utils.RobotsTxtHandler;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


@Slf4j
public  class LinkExtractor {
    public static Set<URL>extractLinks(URL url)throws IOException {


        Set<URL> links = new HashSet<>();

        Document doc = Jsoup.connect(url.toString()).get();
        Elements anchors = doc.select("a[href]");
        for(Element a : anchors){
            try{
                URL link = new URL(url,a.attr("href"));
                links.add(link);
            }catch (Exception e){
                log.error("Exception at extractLinks for {}",a);
            }
        }
        return links;

    }
}
