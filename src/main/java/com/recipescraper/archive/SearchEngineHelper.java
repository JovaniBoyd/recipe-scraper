package com.recipescraper.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


//searches google/bing for links (Google blocks scrapes more)
public class SearchEngineHelper {

    public static List<String> findRecipeLinks(String query){
        List<String> links = new ArrayList<>();

        try {
            String searchUrl = "https://html.duckduckgo.com/html/";
            Document doc = Jsoup.connect(searchUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36")
                .header("Accept-Language", "en-US,en;q=0.9")
                .timeout(10_000)
                .data("q", query + " recipe")
                .post();

            System.out.println("Page preview:\n" + doc.outerHtml().substring(0, 2000));

            Elements anchors = doc.select("a.result__a");

            for (Element a : anchors){
                String href = a.absUrl("href");
                if (href.contains("bing.com")) continue;
                if (href.matches(".*(recipe|cook|ingredients|seriouseats|nytcooking|recipetineats|americastestkitchen|cookscountry|cooksillustrated|allrecipes|foodnetwork|epicurious)")){
                //if (href.matches("https?://(www\\.)?(allrecipes|foodnetwork|epicurious|seriouseats|recipetineats|nytimes|cooksillustrated|cookscountry|americastestkitchen)\\.[a-z]+/.*")) {
                System.out.println(" ✅ Found external recipe: " + href);
                    links.add(href);
                }

                if (links.size() >= 10) break;
            }

        } catch (IOException e){
            System.err.println("Search failed: " + e.getMessage());
        }

        return links;





    }

}
