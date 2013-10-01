@Grapes([
	@Grab("org.gebish:geb-core:0.9.1"),
	@Grab("org.seleniumhq.selenium:selenium-firefox-driver:2.35.0"),
	@Grab("org.seleniumhq.selenium:selenium-support:2.35.0")
])
import geb.*

def engines = [
  Google: [home:GoogleHomePage, search:GoogleResultsPage],
  Bing:  [home:BingHomePage, search:BingResultsPage]
]

def browser

def items = engines.collectEntries { engineName, pages ->
  def ret
  println "Extracting data from $engineName"
  browser=Browser.drive {
    to pages.home
    search "Chuck Norris"
    at pages.search
    ret = results*.text()
  }
  [(engineName): ret]
}

browser.quit()

items.each { engineName, list ->
  println "For $engineName - "
  list.eachWithIndex { entry, idx ->
       println " $idx : $entry" 
  }
}

class GoogleHomePage extends Page {
    static url = "http://google.com/?complete=0"
    static at = { title == "Google" }
    static content = {
        searchField { $("input[name=q]") }
        searchButton(to: GoogleResultsPage) { $("input[value='Google Search']") }
    }
 
    void search(String searchTerm) {
        searchField.value searchTerm
        searchButton.click()
    }
}
 
class GoogleResultsPage extends Page {
    static at = { waitFor { title.endsWith("Google Search") } }
    static content = {
        results(wait: true) { $("li.g .r a") }
    }
}


class BingHomePage extends Page {
    static url = "http://www.bing.com"
    static at = { title == "Bing" }
    static content = {
        searchField { $("input[name=q]") }
        searchButton(to: BingResultsPage) { $("input[name='go']") }
    }
 
    void search(String searchTerm) {
        searchField.value searchTerm
        searchButton.click()
    }
}

class BingResultsPage extends Page {
    static at = { waitFor { title.endsWith("- Bing") } }
    static content = {
        results(wait: true) { $("#results h3 a") }
    }
}
