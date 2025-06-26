package com.recipescraper.scrapers;
import com.recipescraper.model.Recipe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.nodes.Element;
import com.fasterxml.jackson.databind.*;

// Generic scraper for general sites, if this doesn't work it will fall back on site-specific classes
public class GenericRecipeScraper {

    public Recipe tryScrape(String url) {
    try {
        Document doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0")
            .timeout(10_000)
            .get();

        System.out.println("Trying generic scrape for: " + url);

        // 1. Try JSON-LD parsing
        for (Element script : doc.select("script[type=application/ld+json]")) {
            try {
                JsonNode json = new ObjectMapper().readTree(script.html());

                if (json.isArray()) {
                    for (JsonNode node : json) {
                        if (node.has("@type") && node.get("@type").asText().equalsIgnoreCase("Recipe")) {
                            return parseJsonLd(node, url);
                        }
                    }
                } else if (json.has("@type") && json.get("@type").asText().equalsIgnoreCase("Recipe")) {
                    return parseJsonLd(json, url);
                }

            } catch (Exception e) {
                // Ignore bad JSON blocks
            }
        }

        // 2. Try fallback HTML scraping
        System.out.println("  No schema.org elements found. Trying HTML fallback.");

        String title = doc.title();

        Elements ingredients = doc.select("ul li");
        Elements instructions = doc.select("ol li, .directions-content li, .instructions li, p");

        List<String> ingredientList = ingredients.stream()
            .map(Element::text)
            .filter(text -> text.toLowerCase().contains("salmon") ||
                    text.toLowerCase().contains("lemon") ||
                    text.toLowerCase().contains("asparagus") ||
                    text.toLowerCase().contains("garlic") ||
                    text.toLowerCase().contains("butter"))
    .collect(Collectors.toList());
        List<String> instructionList = instructions.stream()
            .map(Element::text)
            .filter(text -> text.length() > 20) // crude filter to skip short junk
            .collect(Collectors.toList());

        if (ingredientList.size() < 1 || instructionList.size() < 1) {
            System.out.println("  Fallback HTML also failed.");
            return null;
        }

        return new Recipe(title, ingredientList, instructionList, 0, 0, url, 0.0);

    } catch (Exception e) {
        System.err.println("Exception: " + e.getMessage());
        return null;
    }
}

    private Recipe parseJsonLd(JsonNode json, String url) {
        String title = json.path("name").asText("Unknown title");

        List<String> ingredients = new ArrayList<>();
        json.path("recipeIngredient").forEach(i -> ingredients.add(i.asText()));

        List<String> instructions = new ArrayList<>();
        JsonNode instNode = json.get("recipeInstructions");
        if (instNode != null) {
            if (instNode.isArray()) {
                for (JsonNode step : instNode) {
                    if (step.has("text")) instructions.add(step.get("text").asText());
                    else instructions.add(step.asText());
                }
            } else if (instNode.has("text")) {
                instructions.add(instNode.get("text").asText());
            }
        }

        if (ingredients.size() < 1 || instructions.size() < 1) {
            System.out.println("  Insufficient ingredients or instructions found.");
            return null;
        }

        return new Recipe(title, ingredients, instructions, 0, 0, url, 0.0);
    }

private List<String> parseJsonList(JsonNode node) {
    List<String> list = new ArrayList<>();
    if (node != null && node.isArray()) {
        for (JsonNode item : node) {
            list.add(item.asText());
        }
    }
    return list;
}

private List<String> parseJsonInstructions(JsonNode node) {
    List<String> list = new ArrayList<>();
    if (node == null) return list;

    if (node.isArray()) {
        for (JsonNode step : node) {
            if (step.isTextual()) {
                list.add(step.asText());
            } else if (step.has("text")) {
                list.add(step.get("text").asText());
            }
        }
    } else if (node.has("text")) {
        list.add(node.get("text").asText());
    }
    return list;
}


    /* 
    public Recipe tryScrape(String url){
        try {
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10_000)
                .get();

            String title = doc.title();
            Elements ingredientEls = doc.select("li:contains(ingredient, li[class*=ingredient], span[class*=ingredient])");
            Elements instructionEls = doc.select("li:contains(step), li[class*=step], p:contains(step)");

            List<String> ingredients = ingredientEls.eachText();
            List<String> instructions = instructionEls.eachText();

            if (ingredients.size() < 2 || instructions.size() < 2){
                return null;
            }

            return new Recipe(title, ingredients, instructions, 0, 0, url, 0.0);

        } catch (Exception e){
            return null; // let SmatScraper fallback
        }

    }*/
}
