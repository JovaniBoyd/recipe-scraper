package com.recipescraper.core;

import java.util.*;

import com.recipescraper.model.Recipe;
import com.recipescraper.scrapers.*;

import java.util.concurrent.*;

// Main controller
public class SmartScraper {
    private final List<SiteScraper> siteScrapers = List.of(
        new SeriousEatsScraper(),
        new RecipeTinEatsScraper(),
        new BBCGoodFoodScraper(), //revise
        new MinistryOfCurryScraper()
        //TODO: Some can't be parsed with Jsoup due to dynamic javascript
        //new EatingWellScraper()
        //new NYTCookingScraper()
        //new FoodNetworkScraper()
        
        //AmericasTestKitchen/CooksCountry/CooksIllustrated
    );

    private static final int MAX_RECIPES = 6; 

    public List<Recipe> searchRecipes(List<String> ingredients){  
        System.out.println("Searching for recipes with ingredients: " + ingredients);  
        String query = buildQuery(ingredients);
        //added for multithreading
        ExecutorService executor = Executors.newFixedThreadPool(siteScrapers.size());
        
        Map<String, List<Recipe>> scraperResults = new HashMap<>();//store from each scraper, keyed with class name
        List<Future<Void>> futures = new ArrayList<>(); // store future tasks

        for (SiteScraper scraper : siteScrapers) {
            futures.add(executor.submit(() -> {        
                List<Recipe> scraped = scraper.search(query, MAX_RECIPES);
                // Store results thread
                synchronized (scraperResults){
                    scraperResults.put(scraper.getClass().getSimpleName(), scraped);
                }
                System.out.println("->" + scraper.getClass().getSimpleName() + " gave " + scraped.size() + " recipes");
                return null;
             }));
            }
            // wait for all threads to finish
        for (Future<Void> future: futures){
            try{              
                future.get();
                } catch(InterruptedException | ExecutionException e){
                e.printStackTrace();
            } 
                
            }                
        executor.shutdown();

            // randomize results of all scrapers
            List<Recipe> finalResults = new ArrayList<>();


            List<List<Recipe>> pools = new ArrayList<>(scraperResults.values());
            Random rand = new Random();


            // Combine results from all scrapers and draw randomly
        while (finalResults.size() < MAX_RECIPES && !pools.isEmpty()){
            int pooli = rand.nextInt(pools.size()); // pick random pool
            List<Recipe> selectedPool = pools.get(pooli);

            if (!selectedPool.isEmpty()){
                finalResults.add(selectedPool.remove(0));
            }
            if (selectedPool.isEmpty()){
                pools.remove(pooli);
            }

        }
        System.out.println("Total Recipes colelcted " + finalResults.size());
        return finalResults;
       

    }



    private String buildQuery(List<String> ingredients){
        return String.join(" ", ingredients);

    }


}
