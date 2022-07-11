package com.visionit.automation.pageobjects;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.Scenario;
import junit.framework.Assert;

public class SearchPageObjects {

	private static final Logger logger = LogManager.getLogger(CmnPageObjects.class);
	WebDriver driver;
	Scenario scn;
	
	public SearchPageObjects(WebDriver driver, Scenario scn) {
		this.driver = driver;
		this.scn = scn;
	}
	
	
	// Locators
	//private By searchBoxElement = By.id("twotabsearchtextbox");
	private By ListOfProductElement = By.xpath("//div[@class='a-section a-spacing-small a-spacing-top-small']//h2/a");
	
	
	// Search page methods
	public void validateSearchPageTitle()
	{
		WebDriverWait webDriverWait1 = new WebDriverWait(driver,20);
		logger.info("Waiting for page title : \"Amazon.in\"" );
		webDriverWait1.until(ExpectedConditions.titleContains("Amazon.in"));
		//Assertion for Page Title
		Assert.assertEquals("Page Title validation",true, driver.getTitle().contains("Amazon.in"));
		scn.log("Page title validation successfull: " + driver.getTitle());
		logger.info("Page title validation successfull: " + driver.getTitle());
	}
	
	
	public void clickOnFirstProd()
	{
		List<WebElement> listOfProducts = driver.findElements(ListOfProductElement);
		scn.log("Number of products searched: " + listOfProducts.size());
		logger.info("Number of products searched: " + listOfProducts.size());
		//But as this step asks click on any link, we can choose to click on Index 0 of the list
		listOfProducts.get(0).click();
		scn.log("Click on the first Link in the List. Link Text: " + listOfProducts.get(0).getText());
		logger.info("Click on the first Link in the List. Link Text: " + listOfProducts.get(0).getText());
	}

}
