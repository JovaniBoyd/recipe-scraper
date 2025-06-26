package com.recipescraper.scrapers;

import com.recipescraper.model.Recipe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class FoodNetworkScraper implements SiteScraper {

    private static final List<String> USER_AGENTS = Arrays.asList(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; rv:102.0) Gecko/20100101 Firefox/102.0"
    );

    private String getRandomUserAgent() {
        return USER_AGENTS.get(new Random().nextInt(USER_AGENTS.size()));
    }

    @Override
    public List<Recipe> search(String query, int maxResults) {
        List<Recipe> recipes = new ArrayList<>();
        try {
            String searchUrl = "https://www.foodnetwork.com/search/" + query.replace(" ", "-") + "-";
            Document doc = Jsoup.connect(searchUrl)
                    .userAgent(getRandomUserAgent())
                    .referrer("https://www.google.com/")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .timeout(10000)
                    .get();

            Elements cards = doc.select("section.o-ResultCard__m-MediaBlock a.m-MediaBlock__a-Headline");
            System.out.println("FoodNetwork: found " + cards.size() + " cards");

            for (Element card : cards) {
                if (recipes.size() >= maxResults) break;

                String url = card.absUrl("href");
                System.out.println(" → Trying: " + url);
                Recipe recipe = scrapeDetails(url);
                if (recipe != null) recipes.add(recipe);
            }

        } catch (Exception e) {
            System.err.println("Failed to search Food Network: " + e.getMessage());
        }

        return recipes;
    }

    private Recipe scrapeDetails(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(getRandomUserAgent())
                    .referrer("https://www.google.com/")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .timeout(10000)
                    .get();

            String title = doc.selectFirst("h1.o-AssetTitle__a-Headline").text();
            List<String> ingredients = doc.select("p.o-Ingredients__a-Ingredient").eachText();
            List<String> instructions = doc.select("li.o-Method__m-Step").eachText();

            System.out.println("  ✔ Ingredients found: " + ingredients.size());
            System.out.println("  ✔ Instructions found: " + instructions.size());

            if (ingredients.size() < 2 || instructions.size() < 2) return null;

            return new Recipe(title, ingredients, instructions, 0, 0, url, 0.0);

        } catch (Exception e) {
            System.err.println("Failed to scrape Food Network recipe: " + e.getMessage());
            return null;
        }
    }
}