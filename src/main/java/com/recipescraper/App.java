package com.recipescraper;

import java.util.List;

import com.recipescraper.core.SmartScraper;
import com.recipescraper.model.Recipe;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args)
    {
        
        SmartScraper scraper = new SmartScraper();
        System.out.println("Beginning...");

        List<Recipe> recipes = scraper.searchRecipes(
         //   List.of("shrimip", "rice", "tomato")
            //List.of("chicken", "rice")
        List.of("salmon", "lemon", "asparagus")
            //List.of("tuna", "lemon", "tomato")
        );

        for (Recipe r : recipes){
            System.out.println("Title: " + r.getTitle());
            System.out.println("Link: " + r.getWebsite());
            System.out.println("Ingredients: " + r.getIngredients());
            System.out.println("Instructions: " + r.getInstructions().size() + " steps");
            System.out.println("=".repeat(40));


        }

    }
    @Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("*");
            }
        };
    }
}
}
