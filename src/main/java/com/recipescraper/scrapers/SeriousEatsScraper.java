package com.recipescraper.scrapers;

import com.recipescraper.model.Recipe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.parser.Parser;

import java.util.ArrayList;
import java.util.List;
//Note: seriouseats.com is heavily JavaScript-rendered, HTML isn't the same as viewed on browser
// must use root sitemap index (.xml) instead
public class SeriousEatsScraper implements SiteScraper {

@Override
public List<Recipe> search(String query, int maxResults) {
    List<Recipe> recipes = new ArrayList<>();
    try {
        // Step 1: Fetch main sitemap index
        String sitemapIndexUrl = "https://www.seriouseats.com/sitemap.xml";
        Document sitemapIndexDoc = Jsoup.connect(sitemapIndexUrl)
                .userAgent("Mozilla/5.0")
                .timeout(10_000)
                .parser(Parser.xmlParser())
                .get();

        Elements sitemapLocs = sitemapIndexDoc.select("sitemap > loc");
        List<String> allRecipeUrls = new ArrayList<>();

        // Step 2: For each sub-sitemap, fetch and parse recipe URLs
        for (Element sitemapLoc : sitemapLocs) {
            String subSitemapUrl = sitemapLoc.text();
            Document subSitemapDoc = Jsoup.connect(subSitemapUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(10_000)
                    .parser(Parser.xmlParser())
                    .get();

            Elements urlLocs = subSitemapDoc.select("url > loc");
            for (Element urlLoc : urlLocs) {
                allRecipeUrls.add(urlLoc.text());
                if (allRecipeUrls.size() >= 1000) break; // optional limit
            }
            if (allRecipeUrls.size() >= 1000) break;
        }

        System.out.println("SeriousEats sitemap: " + allRecipeUrls.size() + " links");

        String[] keywords = query.toLowerCase().split("\\s+");
        for (String url : allRecipeUrls) {
            if (recipes.size() >= maxResults) break;

            String titleGuess = url.substring(url.lastIndexOf('/') + 1).replace("-", " ");
            boolean matches = false;
            for (String keyword : keywords) {
                if (titleGuess.contains(keyword)) {
                    matches = true;
                    break;
                }
            }
            if (!matches) continue;

            System.out.println("→ Trying: " + url);
            Recipe recipe = scrapeDetails(url);
            if (recipe != null) {
                recipes.add(recipe);
            }
        }

    } catch (Exception e) {
        System.err.println("failed to search Serious Eats: " + e.getMessage());
    }
    return recipes;
}



    private Recipe scrapeDetails(String url) {
    try {
        Document doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0")
            .timeout(10_000)
            .get();

        // Title
        Element titleEl = doc.selectFirst("h1.heading__title");
        if (titleEl == null) {
            System.out.println("  ✘ No title found");
            return null;
        }
        String title = titleEl.text();

        // Ingredients (try several common Serious Eats patterns)
        Elements ingredientEls = doc.select("ul.structured-ingredients__list li, section.recipe-ingredients li, div.recipe-ingredients li");
        List<String> ingredients = ingredientEls.eachText();
        System.out.println("  ✔ Ingredients found: " + ingredients.size());

        // Instructions (also varies)
        Elements instructionEls = doc.select("ol li, div.recipe-procedures p, div.recipe-directions__steps li");
        List<String> instructions = instructionEls.eachText();
        System.out.println("  ✔ Instructions found: " + instructions.size());

        if (ingredients.size() < 2 || instructions.size() < 2) {
            System.out.println("  ✘ Skipping due to too few ingredients or instructions");
            return null;
        }

        return new Recipe(title, ingredients, instructions, 0, 0, url, 0.0);

    } catch (Exception e) {
        System.err.println("Failed to scrape Serious Eats recipe: " + e.getMessage());
        return null;
    }
}
    

    
}


// IN scrapeDetails

 //  double rating = 0.0;

          /*   if (ratingEl != null){
                try {
                    rating = Double.parseDouble(ratingEl.attr("data-rating"));
                } catch (NumberFormatException ignored) {}
            } */