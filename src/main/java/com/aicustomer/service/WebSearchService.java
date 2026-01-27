package com.aicustomer.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Web Search Service
 * 
 * Provides web search capabilities using Jsoup scraping.
 */
@Slf4j
@Service
public class WebSearchService {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    /**
     * Perform a web search
     * 
     * @param query The search query
     * @return A list of maps containing title, link, and snippet
     */
    public List<Map<String, String>> search(String query) {
        List<Map<String, String>> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://www.bing.com/search?q=" + encodedQuery;

            log.info("Performing web search for: {}", query);
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(5000)
                    .get();

            // Select search results (Bing specific selectors, might need adjustment over
            // time)
            Elements searchResults = doc.select("li.b_algo");

            for (Element result : searchResults) {
                if (results.size() >= 5)
                    break;

                Element titleElement = result.selectFirst("h2 a");
                Element snippetElement = result.selectFirst("div.b_caption p");

                if (titleElement != null) {
                    Map<String, String> entry = new HashMap<>();
                    entry.put("title", titleElement.text());
                    entry.put("link", titleElement.attr("href"));

                    if (snippetElement != null) {
                        entry.put("snippet", snippetElement.text());
                    } else {
                        entry.put("snippet", "No description available.");
                    }

                    results.add(entry);
                }
            }

            if (results.isEmpty()) {
                log.warn("No results found for query: {}", query);
            }

        } catch (IOException e) {
            log.error("Error performing web search: {}", e.getMessage(), e);
        }

        return results;
    }

    /**
     * Search and return formatted string
     */
    public String searchAndFormat(String query) {
        List<Map<String, String>> results = search(query);
        if (results.isEmpty()) {
            return "No online information found for: " + query;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(results.size()).append(" online results for \"").append(query).append("\":\n\n");

        for (int i = 0; i < results.size(); i++) {
            Map<String, String> result = results.get(i);
            sb.append(i + 1).append(". **").append(result.get("title")).append("**\n");
            sb.append("   Link: ").append(result.get("link")).append("\n");
            sb.append("   Summary: ").append(result.get("snippet")).append("\n\n");
        }

        return sb.toString();
    }
}
