package com.buyondip.service;

import com.buyondip.dto.NewsItemDto;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsService {

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);

    private final OkHttpClient okHttpClient;

    public NewsService(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    private static final String ET_MARKET_RSS = "https://economictimes.indiatimes.com/markets/rssfeeds/1977021501.cms";
    private static final String MONEYCONTROL_RSS = "https://www.moneycontrol.com/rss/marketreports.xml";

    @Cacheable(value = "news", key = "#symbol")
    public List<NewsItemDto> getStockNews(String symbol) {
        List<NewsItemDto> items = new ArrayList<>();
        String query = symbol.toLowerCase().replaceAll("\\.ns$|\\.bse$", "");

        List<NewsItemDto> etNews = fetchRss(ET_MARKET_RSS, "Economic Times");
        etNews.stream()
                .filter(n -> containsQuery(n, query))
                .limit(5)
                .forEach(items::add);

        if (items.size() < 5) {
            List<NewsItemDto> mcNews = fetchRss(MONEYCONTROL_RSS, "Moneycontrol");
            mcNews.stream()
                    .filter(n -> containsQuery(n, query))
                    .limit(5 - items.size())
                    .forEach(items::add);
        }

        if (items.isEmpty()) {
            return etNews.stream().limit(5).toList();
        }
        return items;
    }

    @Cacheable(value = "marketNews")
    public List<NewsItemDto> getMarketNews() {
        List<NewsItemDto> items = new ArrayList<>();
        items.addAll(fetchRss(ET_MARKET_RSS, "Economic Times").stream().limit(8).toList());
        items.addAll(fetchRss(MONEYCONTROL_RSS, "Moneycontrol").stream().limit(4).toList());
        return items;
    }

    private boolean containsQuery(NewsItemDto n, String query) {
        String title = n.getTitle() != null ? n.getTitle().toLowerCase() : "";
        String desc = n.getDescription() != null ? n.getDescription().toLowerCase() : "";
        return title.contains(query) || desc.contains(query);
    }

    private List<NewsItemDto> fetchRss(String feedUrl, String sourceName) {
        List<NewsItemDto> items = new ArrayList<>();
        try {
            Request request = new Request.Builder()
                    .url(feedUrl)
                    .header("User-Agent", "Mozilla/5.0")
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.warn("RSS fetch failed for {}: HTTP {}", feedUrl, response.code());
                    return items;
                }

                byte[] body = response.body().bytes();
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(new ByteArrayInputStream(body)));

                for (SyndEntry entry : feed.getEntries()) {
                    NewsItemDto dto = new NewsItemDto();
                    dto.setTitle(entry.getTitle());
                    dto.setUrl(entry.getLink());
                    dto.setSource(sourceName);

                    if (entry.getDescription() != null) {
                        dto.setDescription(entry.getDescription().getValue()
                                .replaceAll("<[^>]+>", "").trim());
                    }

                    if (entry.getPublishedDate() != null) {
                        dto.setPublishedAt(entry.getPublishedDate().toInstant()
                                .atZone(ZoneId.of("Asia/Kolkata"))
                                .toLocalDateTime());
                    }
                    items.add(dto);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse RSS from {}: {}", feedUrl, e.getMessage());
        }
        return items;
    }
}
