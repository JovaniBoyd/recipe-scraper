## Recipe Web Scraper

A full-stack web application that scrapes recipes from multiple popular cooking websites using Java (with Spring Boot) and displays them through a React front-end.

### Built With

* [![Java][Java]][Java-url]
* [![React][React.js]][React-url]
* [![Maven][Maven]][Maven-url]
* [![Spring Boot][Spring Boot]][Spring Boot-url]
* [![Node][Node.js]][Node.js-url]
* [![SQLite][SQLite]][SQLite-url]

### Features
* Search recipes by ingredients
* Scrapes from sites:
    * [BBC Good Food][BBC-url]
    * [Serious Eats][SeriousEats-url]
    * [Ministry of Curry][MinistryofCurry-url]
    * [RecipeTin Eats][recipetineats-url]
* Multi-threaded backend for faster and mixed results
* Randomized results from different sites
* React UI for easy input and display


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
## Getting Started
### Prerequisites
* Java 17+
* Maven
* Node.js + npm

### Backend Setup - Spring Boot
```sh
cd RecipeScraper
mvn spring-boot:run
```
API at: http://localhost:8080/api/recipes

### Frontend Setup - React
```sh
cd frontend
npm install
npm start
```
React UI at: http://localhost:3000
  











[Java]: https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white
[Java-url]: https://www.java.com/

[React.js]: https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB
[React-url]: https://reactjs.org/

[Spring Boot]: https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[Spring Boot-url]: https://spring.io/projects/spring-boot

[Maven]: https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=Apache-Maven&logoColor=white
[Maven-url]: https://maven.apache.org/

[React.js]: https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB
[React-url]: https://reactjs.org/

[Node.js]: https://img.shields.io/badge/Node.js-339933?style=for-the-badge&logo=node.js&logoColor=white
[Node.js-url]: https://nodejs.org/

[HTML5]: https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white
[HTML5-url]: https://developer.mozilla.org/en-US/docs/Web/HTML

[CSS3]: https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white
[CSS3-url]: https://developer.mozilla.org/en-US/docs/Web/CSS

[SQLite]:https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white
[SQLite-url]:https://www.sqlite.org/index.html

[BBC-url]: https://www.bbcgoodfood.com/
[SeriousEats-url]: https://www.seriouseats.com/
[recipetineats-url]: https://www.seriouseats.com/
[MinistryofCurry-url]: https://ministryofcurry.com/

