package com.aicustomer.service;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class WebSearchServiceTest {

    @Test
    public void testSearch() {
        WebSearchService service = new WebSearchService();
        List<Map<String, String>> results = service.search("OpenAI");

        System.out.println("Search Results: " + results);

        // Note: This test might fail if network is blocked or Bing changes structure.
        // We just want to ensure no exceptions are thrown and code executes.
        assertNotNull(results);
    }
}
