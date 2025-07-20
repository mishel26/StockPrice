package web.crawler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

@Data
public class URLDepthPair {
    private final URL url;
    private final int depth;

    public URLDepthPair(URL url,int depth){
        this.url = url;
        this.depth = depth;
    }
}
