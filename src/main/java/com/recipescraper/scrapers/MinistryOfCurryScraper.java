package com.recipescraper.scrapers;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.recipescraper.model.Recipe;

public class MinistryOfCurryScraper implements SiteScraper {

    private static final String SITE_BASE = "https://ministryofcurry.com";

    // MinistryOfCurry has no real search page, so we crawl sitemap URLs
    // Example sitemap index URL
    private static final String SITEMAP_INDEX = "https://ministryofcurry.com/sitemap_index.xml";

    @Override
    public List<Recipe> search(String query, int maxResults) {
        System.out.println("MinistryOfCurry: searching for '" + query + "'");

        List<Recipe> results = new ArrayList<>();
        List<String> queryIngredients = List.of(query.toLowerCase().split("\\s+"));

        try {
            // Fetch sitemap index XML
            Document sitemapIndexDoc = Jsoup.connect(SITEMAP_INDEX).ignoreContentType(true).get();

            // Get all sitemap URLs from sitemap index
            Elements sitemapElements = sitemapIndexDoc.select("sitemap > loc");

            // For each sitemap, extract recipe URLs and process them
            for (Element sitemapEl : sitemapElements) {
                if (results.size() >= maxResults) break;

                String sitemapUrl = sitemapEl.text();

                // Fetch sitemap XML
                Document sitemapDoc = Jsoup.connect(sitemapUrl).ignoreContentType(true).get();

                // Get recipe URLs from sitemap
                Elements urlElements = sitemapDoc.select("url > loc");

                for (Element urlEl : urlElements) {
                    if (results.size() >= maxResults) break;

                    String recipeUrl = urlEl.text();

                    Recipe recipe = extractRecipeFromPage(recipeUrl, queryIngredients);
                    if (recipe != null) {
                        results.add(recipe);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("MinistryOfCurryScraper error: " + e.getMessage());
            e.printStackTrace();
        }

        return results;
    }

    private Recipe extractRecipeFromPage(String url, List<String> queryIngredients) throws Exception {
        Document doc = Jsoup.connect(url).get();

        String title = doc.selectFirst("h1.entry-title").text();

        Elements ingredientElements = doc.select("ul.wprm-recipe-ingredients li");
        List<String> ingredients = new ArrayList<>();
        for (Element ing : ingredientElements) {
            String ingText = ing.text().trim();
            if (!ingText.isEmpty()) ingredients.add(ingText);
        }

        System.out.println("Checking recipe: " + title);
        System.out.println("Ingredients:");
        for (String ing : ingredients) {
            System.out.println(" - " + ing);
        }
        System.out.println("Query words: " + queryIngredients);

        // Relaxed match: check if any query word appears in ingredients
        boolean matchesAny = false;
        for (String q : queryIngredients) {
            for (String ing : ingredients) {
                if (ing.toLowerCase().contains(q)) {
                    matchesAny = true;
                    break;
                }
            }
            if (matchesAny) break;
        }
        if (!matchesAny) {
            System.out.println("Recipe '" + title + "' rejected: no query word found.");
            return null;
        }

        Elements instructionElements = doc.select("div.wprm-recipe-instruction-text");
        if (instructionElements.isEmpty()) {
    // fallback if above doesn't work
    instructionElements = doc.select("div.wprm-recipe-instructions li");
}
        List<String> instructions = new ArrayList<>();
        for (Element ins : instructionElements) {
            String insText = ins.text().trim();
            if (!insText.isEmpty()) instructions.add(insText);
        }

        int prepTime = 0;
        int cookTime = 0;
        try {
            Element prepEl = doc.selectFirst(".wprm-recipe-prep_time .wprm-recipe-prep_time-minutes");
            if (prepEl != null) prepTime = Integer.parseInt(prepEl.text().replaceAll("[^0-9]", ""));
            Element cookEl = doc.selectFirst(".wprm-recipe-cook_time .wprm-recipe-cook_time-minutes");
            if (cookEl != null) cookTime = Integer.parseInt(cookEl.text().replaceAll("[^0-9]", ""));
        } catch (Exception ignored) {}

        double rating = 0.0;
        try {
            Element ratingEl = doc.selectFirst("span.wprm-recipe-rating-average");
            if (ratingEl != null) rating = Double.parseDouble(ratingEl.text());
        } catch (Exception ignored) {}

        String imageUrl = null;
        Element imgEl = doc.selectFirst("div.wprm-recipe-image img");
        if (imgEl != null) imageUrl = imgEl.absUrl("src");

        Recipe recipe = new Recipe(title, ingredients, instructions, prepTime, cookTime, SITE_BASE, rating);
        recipe.setImageUrl(imageUrl);
        return recipe;
    }
}
