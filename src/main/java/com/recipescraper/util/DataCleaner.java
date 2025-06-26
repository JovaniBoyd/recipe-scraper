package com.recipescraper.util;
import com.recipescraper.model.Recipe;

//AI Model to clean data before storage
public class DataCleaner {

    public Recipe clean(Recipe rawData){
        // TODO: ADD AI MODEL here


        //return rawData.replaceAll("[^\\x20-\\x7E]", "").trim(); //simulator to clean text -- placeholder

        return rawData;
    }
    
}
