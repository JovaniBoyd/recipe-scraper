package com.recipescraper.storage;

//import java.io.FileWriter;
//import java.io.IOException;

import com.recipescraper.model.Recipe;

//import java.io.FileWriter;
import java.sql.*;
import java.util.*;
//import java.util.stream.Collectors;
//import java.util.List;


public class DataStorage {

    private static final String DB_URL = "jdbc:sqlite:data/recipes.db"; //subject to change "jdbc:sqlite:cookbook.db"

    public DataStorage(){
        createTableIfNotExists();
    }

    private void createTableIfNotExists(){
        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement()){

        String createRecipeTable = "CREATE TABLE IF NOT EXISTS recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCRIMENT," +
                "title TEXT," +
                "ingredients TEXT," +
                "instructions TEXT," +
                "prep_time INTEGER," +
                "cook_time INTEGER," +
                "website TEXT," +
                "rating DOUBLE)";
        
            stmt.execute(createRecipeTable);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public void saveRecipe(Recipe recipe){
        String sql = "INSERT INTO Recipe (title, ingredients, instructions, prepTime, cookTime, website, rating) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, recipe.getTitle());
        pstmt.setString(2, String.join(",", recipe.getIngredients()));
        pstmt.setString(3, String.join("\n", recipe.getInstructions()));
        pstmt.setInt(4, recipe.getPrepTimeMinutes());
        pstmt.setInt(5, recipe.getCookTimeMinutes());
        pstmt.setString(6, recipe.getWebsite());
        pstmt.setDouble(7, recipe.getRating());

        pstmt.executeUpdate();
        System.out.println("Recipe saved: " + recipe.getTitle());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Recipe> getAllRecipes(){
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT * FROM Recipe";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()){
                String title = rs.getString("title");
                List<String> ingredients = Arrays.asList(rs.getString("ingredients").split(","));
                List<String> instructions = Arrays.asList(rs.getString("instructions").split("\n"));
                int prepTime = rs.getInt("prepTime");
                int cookTime = rs.getInt("cookTime");
                String website = rs.getString("website");
                double rating = rs.getDouble("rating");

                Recipe recipe = new Recipe(title, ingredients, instructions, prepTime, cookTime, website, rating);
                recipes.add(recipe);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return recipes;

    }


    /*public void save(String cleanedData){
        try (FileWriter writer = new FileWriter("data/cleaned_output.txt")){
            writer.write(cleanedData);
            System.out.println(cleanedData);

        } catch (IOExeption e){
            e.printStackTrace();
        }
    }
        */



}
