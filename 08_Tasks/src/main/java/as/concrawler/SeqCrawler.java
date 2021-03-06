package as.concrawler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Crawls the web sequentially. This class is already thread safe since it is
 * stateless.
 */
public class SeqCrawler implements Crawler {
    /** Maximal number of visited urls per request. */
    private static final int MAX_VISITS = 20;

    /**
     * Crawls the web, starting at startURL and returning a list of visited
     * URLs.
     */
    @Override
    public List<String> crawl(String startURL) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        /* Contains the already visited urls. */
        final Set<String> urlsVisited = new HashSet<String>();
        /* Contains the urls to visit. */
        final Queue<String> urlsToVisit = new LinkedList<String>();
        urlsToVisit.add(startURL);

        while ((!urlsToVisit.isEmpty()) && urlsVisited.size() < MAX_VISITS) {
            final String toVisit = urlsToVisit.poll(); // crawl next url

            try {
                final Document doc = Jsoup.parse(Jsoup.connect(toVisit)
                        .userAgent("ConCrawler/0.1 Mozilla/5.0")
                        .timeout(3000)
                        .get().html());

                urlsVisited.add(toVisit);

                final Elements links = doc.select("a[href]");
                for (final Element link : links) {
                    final String linkString = link.absUrl("href");
                    if ((!urlsVisited.contains(linkString)) && linkString.startsWith("http")) {
                        urlsToVisit.add(linkString);
                    }
                }
            } catch (final Exception e) {
                System.out.println("Problem reading '" + toVisit + "'. Message: " + e.getMessage());
            }
        }
        return new ArrayList<String>(urlsVisited);
    }
}
