## Recipe Web Scraper

A full-stack web application that scrapes recipes from multiple popular cooking websites using Java (with Spring Boot) and displays them through a React front-end.

### Built With

* [![React][React.js]][React-url]



## Project Structure

```
RecipeScraper/
├── src/
│   └── com/
│       └── recipescraper/
│           ├── core/
│           │   └── SmartScraper.java
│           │
│           ├── model/
│           │   └── Recipe.java
│           │
│           ├── scrapers/
│           │   ├── SiteScraper.java
│           │   ├── BBCGoodFoodScraper.java
│           │   ├── SeriousEatsScraper.java
│           │   ├── MinistryOfCurryScraper.java
│           │   └── RecipeTinEatsScraper.java
│           │
│           ├── util/
│           │   └── DataCleaner.java
│           │
│           ├── storage/
│           │   └── DataStorage.java
│           │
│           ├── api/
│           │   └── RecipeController.java
│           │
│           └── App.java
│
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── App.js
│   │   └── ...
│   └── package.json
│
├── pom.xml
└── README.md
```



