const puppeteer = require('puppeteer-extra');
const StealthPlugin = require('puppeteer-extra-plugin-stealth');
puppeteer.use(StealthPlugin());

(async () => {
  // const browser = await puppeteer.launch({headless: false});
  const browser = await puppeteer.launch({executablePath: '/Applications/Google Chrome.app/Contents/MacOS/Google Chrome', headless: false});
  const page = await browser.newPage();
  const selectorClass = '.ranking-app-cell';
  const categories = ["finance", "art_design", "auto", "beauty", "books_reference", "business", "comics", "communication", "dating", "education", "entertainment", "events", "food_drink", "health_fitness", "house_home", "libraries_demo", "lifestyle", "transportation", "medical", "music_audio", "news_magazines", "parenting", "personalization", "photography", "productivity", "shopping", "social", "sports", "tools", "travel_local", "media_video", "weather"]
  const countryCodes = {"au" : "Australia", "at" : "Austria", "be" : "Belgium", "br" : "Brazil", "ca" : "Canada", "cz" : "Czech Republic", "dk" : "Denmark", "fi" : "Finland", "fr" : "France", "de" : "Germany", "in" : "India", "id" : "Indonesia", "ir" : "Iran", "it" : "Italy", "jp" : "Japan", "mx" : "Mexico", "nl" : "Netherlands", "no" : "Norway", "pl" : "Poland", "pt" : "Portugal", "ru" : "Russia", "sa" : "Saudi Arabia", "sk" : "Slovakia", "kr" : "South Korea", "es" : "Spain", "se" : "Sweden", "ch" : "Switzerland", "tr" : "Turkey", "gb" : "United Kingdom", "us" : "United States"}

  var result = {};

  for (let category of categories) {
    result[category] = {};
    for (let countryCode in countryCodes) {
      result[category][countryCode] = [];
      const url = 'https://www.appbrain.com/stats/google-play-rankings/top_free/' + category + '/' + countryCode;
      await page.goto(url);

      try {
        await page.waitForSelector(selectorClass, visible = true, timeout = 100000).then(() => console.log('Loaded app ranking page.'));
      } catch (error) {
        console.log('Timeout error: ' + error);
        continue;
      }
      
      await page.waitForTimeout(1000);
    
      // Promise<Array<ElementHandle<NodeFor<Selector>>>>
      var rankingElements = await page.$$(selectorClass);
    
      for (let rankingElement of rankingElements) {
        const iHTML = await page.evaluate(el => el.innerHTML, rankingElement);
        const packageName = iHTML.split("\">")[0].split("/")[3];
        result[category][countryCode].push(packageName);
      }
    
      console.log("Finished collecting " + category + " category for " + countryCodes[countryCode]);
      await page.waitForTimeout(1000);
    }
  }

  // write to file
  const fs = require('fs');
  fs.writeFile("appBrainRanking.json", JSON.stringify(result), function(err) { console.log(err)});

  await browser.close();
})();