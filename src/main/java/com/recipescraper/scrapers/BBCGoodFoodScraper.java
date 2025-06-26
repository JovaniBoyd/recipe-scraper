package com.recipescraper.scrapers;

import com.recipescraper.model.Recipe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

//import org.jsoup.nodes.DataNode;
import com.google.gson.*;

public class BBCGoodFoodScraper implements SiteScraper {

    @Override
    public List<Recipe> search(String query, int maxResults) {
        List<Recipe> recipes = new ArrayList<>();
        try {
            String searchUrl = "https://www.bbcgoodfood.com/search?q=" + query.replace(" ", "+");
            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(10000)
                    .get();

            Elements cards = doc.select("article.card");
            System.out.println("BBCGF: found " + cards.size() + " candidate recipes");

            for (Element card : cards) {
                if (recipes.size() >= maxResults) break;

                Element linkEl = card.selectFirst("a[href*=\"/recipes/\"]");
                if (linkEl == null) continue;

                String recipeUrl = linkEl.absUrl("href");
                String title = linkEl.attr("aria-label").replace("View ", "").trim();

                System.out.println("  ? Trying: " + recipeUrl);

                Recipe recipe = scrapeDetails(recipeUrl);
                if (recipe != null) {
                    recipe.setTitle(title);
                    recipes.add(recipe);
                }
            }

        } catch (Exception e) {
            System.err.println("Failed to search BBC Good Food: " + e.getMessage());
        }

        return recipes;
    }

    private Recipe scrapeDetails(String url) {
    try {
        Document doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
            .referrer("https://www.google.com")
            .header("Accept-Language", "en-US,en;q=0.9")
            .timeout(10000)
            .get();

        // Find <script type="application/ld+json"> containing the recipe JSON
        Element jsonLd = null;
        for (Element script : doc.select("script[type=application/ld+json]")) {
            String json = script.data();
            if (json.contains("\"@type\":\"Recipe\"")) {
                jsonLd = script;
                break;
            }
        }
        if (jsonLd == null) {
            System.err.println("No Recipe JSON-LD found on page.");
            return null;
        }

        String json = jsonLd.data();
        JsonElement root = JsonParser.parseString(json);

        // Sometimes JSON-LD is an array, sometimes an object
        JsonObject recipeObj;
        if (root.isJsonArray()) {
            for (JsonElement el : root.getAsJsonArray()) {
                JsonObject obj = el.getAsJsonObject();
                if ("Recipe".equals(obj.get("@type").getAsString())) {
                    recipeObj = obj;
                    break;
                }
            }
            // fallback if not found
            recipeObj = root.getAsJsonArray().get(0).getAsJsonObject();
        } else {
            recipeObj = root.getAsJsonObject();
        }

        // Extract title
        String title = recipeObj.has("name") ? recipeObj.get("name").getAsString() : "";

        // Extract ingredients array
        List<String> ingredients = new ArrayList<>();
        if (recipeObj.has("recipeIngredient")) {
            JsonArray ingArray = recipeObj.getAsJsonArray("recipeIngredient");
            for (JsonElement ingEl : ingArray) {
                ingredients.add(ingEl.getAsString());
            }
        }

        // Extract instructions - could be a string or an array of objects
        List<String> instructions = new ArrayList<>();
        if (recipeObj.has("recipeInstructions")) {
            JsonElement instr = recipeObj.get("recipeInstructions");
            if (instr.isJsonArray()) {
                for (JsonElement stepEl : instr.getAsJsonArray()) {
                    if (stepEl.isJsonObject() && stepEl.getAsJsonObject().has("text")) {
                        instructions.add(stepEl.getAsJsonObject().get("text").getAsString());
                    } else if (stepEl.isJsonPrimitive()) {
                        instructions.add(stepEl.getAsString());
                    }
                }
            } else if (instr.isJsonPrimitive()) {
                instructions.add(instr.getAsString());
            }
        }

        System.out.println("  ✔ Ingredients found: " + ingredients.size());
        System.out.println("  ✔ Instructions found: " + instructions.size());

        if (ingredients.size() < 2 || instructions.size() < 2) return null;

        return new Recipe(title, ingredients, instructions, 0, 0, url, 0.0);

    } catch (Exception e) {
        System.err.println("Failed to scrape BBC Good Food recipe: " + e.getMessage());
        return null;
    }
}
}
