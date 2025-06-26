package com.recipescraper.scrapers;

import com.recipescraper.model.Recipe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class EatingWellScraper implements SiteScraper {
    

@Override
public List<Recipe> search(String query, int maxResults) {
    List<Recipe> recipes = new ArrayList<>();

    // 1. Setup ChromeDriver
    System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\chromedriver\\chrome-win64\\chrome.exe"); // 👈 or use PATH
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless"); // headless mode: no GUI
    options.addArguments("--no-sandbox");
    WebDriver driver = new ChromeDriver(options);

    try {
        // 2. Load search page
        String searchUrl = "https://www.eatingwell.com/search/?q=" + query.replace(" ", "+");
        driver.get(searchUrl);
        Thread.sleep(3000); // wait for JavaScript to load results

        // 3. Parse rendered page with Jsoup
        String pageSource = driver.getPageSource();
        Document doc = Jsoup.parse(pageSource);

        Elements cards = doc.select("a[href^=/recipe/]");

        System.out.println("EatingWell: found " + cards.size() + " candidate recipes");

        for (Element card : cards) {
            if (recipes.size() >= maxResults) break;

            String recipeUrl = "https://www.eatingwell.com" + card.attr("href");
            String title = card.text().trim();
            if (title.isEmpty()) continue;

            System.out.println("  ? Trying: " + recipeUrl);
            Recipe recipe = scrapeDetails(recipeUrl);
            if (recipe != null) {
                recipe.setTitle(title);
                recipes.add(recipe);
            }
        }

    } catch (Exception e) {
        System.err.println("Failed to search EatingWell: " + e.getMessage());
    } finally {
        driver.quit();
    }

    return recipes;
}

    private Recipe scrapeDetails(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .referrer("https://www.google.com")
                    .timeout(10000)
                    .get();

            // Ingredients - inside <li> under <ul> with data-testid="ingredients-list"
            Elements ingredientItems = doc.select("ul[data-testid=ingredients-list] li");
            List<String> ingredients = new ArrayList<>();
            for (Element item : ingredientItems) {
                String text = item.text().trim();
                if (!text.isEmpty()) ingredients.add(text);
            }

            // Instructions - <li> under <ol> with data-testid="instructions-list"
            Elements instructionEls = doc.select("ol[data-testid=instructions-list] li");
            List<String> instructions = new ArrayList<>();
            for (Element step : instructionEls) {
                String text = step.text().trim();
                if (!text.isEmpty()) instructions.add(text);
            }

            System.out.println("  ✔ Ingredients found: " + ingredients.size());
            System.out.println("  ✔ Instructions found: " + instructions.size());

            if (ingredients.size() < 2 || instructions.size() < 2) return null;

            // Title fallback
            String title = doc.selectFirst("h1").text();

            return new Recipe(title, ingredients, instructions, 0, 0, url, 0.0);

        } catch (Exception e) {
            System.err.println("Failed to scrape EatingWell recipe: " + e.getMessage());
            return null;
        }
    }
}
