package com.visionit.automation.stepdefs;

import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.visionit.automation.core.WebDriverFactory;
import com.visionit.automation.pageobjects.CmnPageObjects;
import com.visionit.automation.pageobjects.SearchPageObjects;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import junit.framework.Assert;

public class StepDefs {

	
	private static final Logger logger = LogManager.getLogger(StepDefs.class);

	WebDriver driver;
	String base_url = "https://amazon.in";
	int implicit_wait_timeout_in_sec = 20;
	Scenario scn; // this is set in the @Before method

	CmnPageObjects cmnPageObjects;
	SearchPageObjects searchPageObjects;

	// make sure to use this before import io.cucumber.java.Before;
	// Use @Before to execute steps to be executed before each scnerio
	// one example can be to invoke the browser
	@Before
	public void setUp(Scenario scn) throws Exception{
		this.scn = scn;
		//driver = new ChromeDriver();
		String browserName = WebDriverFactory.getBrowserName();
		driver = WebDriverFactory.getWebDriverForBrowser(browserName);
		logger.info("Browser invoked.");

		cmnPageObjects = new CmnPageObjects(driver, scn);
		searchPageObjects = new SearchPageObjects(driver, scn);

	}


	//	@Given("User open the browser")                  to comment junit lines used:- control+&
	//	public void user_open_the_browser() {            to perform multiline comment in feacture file used :- control+/
	//
	//		driver=new FirefoxDriver();
	//		driver.manage().deleteAllCookies();
	//		driver.manage().window().maximize();
	//		driver.manage().timeouts().implicitlyWait(Implicit_Wait_timeUnit_In_Sec, TimeUnit.SECONDS);
	//
	//	}

	@Given("User navigate to the home page of the application url")
	public void user_navigate_to_the_home_page_of_the_application_url() {


		WebDriverFactory.navigateToTheUrl(base_url);
		scn.log("Browser navigated to URL: " + base_url);
		cmnPageObjects.validateLandingPageTitle();
	}

	@When("User search for a product {string}")
	public void user_search_for_a_product(String productName) {

		//Wait and Search for product
		cmnPageObjects.searchProduct(productName);
		cmnPageObjects.clickOnSearchBtn();
	}

	@Then("Search product is displayed")
	public void search_product_is_displayed() {

		//Wait for titile
		searchPageObjects.validateSearchPageTitle();
	}

	@When("User click on any product")
	public void user_click_on_any_product() {

		//listOfProducts will have all the links displayed in the search box
		searchPageObjects.clickOnFirstProd();
	}


	@Then("Product Description is displayed in new tab")
	public void product_description_is_displayed_in_new_tab() {

		//As product description click will open new tab, we need to switch the driver to the new tab
		//If you do not switch, you can not access the new tab html elements
		//This is how you do it
		Set<String> Handles=driver.getWindowHandles();

		scn.log("List of windows found: "+Handles.size());
		logger.info("List of windows found: "+Handles.size());

		scn.log("Windows handles: " + Handles.toString());
		logger.info("Windows handles: " + Handles.toString());

		Iterator<String> it=Handles.iterator();// get the iterator to iterate the elements in set

		String original=it.next();//gives the parent window id
		String ProdDecsp=it.next();//gives the child window id

		driver.switchTo().window(ProdDecsp);
		scn.log("Switched to the new window/tab");
		logger.info("Switched to the new window/tab");

		//Now driver can access new driver window, but can not access the orignal tab
		//Check product title is displayed
		WebElement productTitle=driver.findElement(By.xpath("//span[@id='productTitle']"));		
		Assert.assertTrue("Search product Title not Match", productTitle.isDisplayed());
		scn.log("Product Title header is matched and displayed as: " + productTitle.getText() );
		logger.info("Product Title header is matched and displayed as: " + productTitle.getText());

		WebElement AddToCartBtn=driver.findElement(By.xpath("//span[@id='submit.add-to-cart']"));
		Assert.assertEquals("Add To Cart Button not Display", true, AddToCartBtn.isDisplayed());
		scn.log("Add to cart Button is displayed");
		logger.info("Add to cart Button is displayed");

		//Switch back to the Original Window, however no other operation to be done
		driver.switchTo().window(original);
		System.out.println("After Shift original Window Title : "+driver.getTitle());
		scn.log("Switched back to Original tab");
		logger.info("Switched back to Original tab");
	}


	// make sure to use this after import io.cucumber.java.After;
	// Use @After to execute steps to be executed after each scnerio
	// one example can be to close the browser
	@After(order=1)
	public void cleanUp(){

		WebDriverFactory.quitDriver();	
	}



	//1. Screen shot capturing is a important part of test cases failure investigation.
	//2. When test cases fails, we need to give the evidence to the person who is investigating the report of the test execution.
	//3. There can be many screen shot capturing strategies. like:
	//     -Take one screen shot when test case end (pass or fail)
	//     -Take screen shot after each line of the scenario.
	//     -Take screen shot as soon as a step fails.
	//4. Usually taking screen shot when scenario/step fails is more resonable and commonly used strategy.
	//5. To implement it we will need to know at run time if the test is pass or fail.
	//6. If the test is pass we will choose not to take a screen shot.
	//7. If the test fails we will take the screenshot and attach it with the native report.
	//8. Then can be achieved in cucumber using 'Scenario' Object injected in @After method.
	//9. So we have added another After method (you can have many after methods)
	//10.However we need to make sure, that this after method gets executed before above After method, otherwise browser will be closed by above after method and screen shot will not be captured.
	//11.To run this @After method first, we need to add the argument 'order' to this method's annotation.
	//12. Giving this method order as 2, means it will always execute first, and then giving order as 1 to the above after method.
	//    Order number is the order in which this hook should run. Higher numbers are run first. The default order is 10000.
	//13. Now since we need to capture the screen shot only after a test is failed, we will put a if condition as check the failure using method 'isFailed'.
	//14. If test is failed it will take the screen shot and attach the screen shot with the report. For this s.attach method is used. (in old version, method name was embed)
	@After(order=2)
	public void takeScreenShot(Scenario s) {
		if (s.isFailed()) {
			TakesScreenshot scrnShot = (TakesScreenshot)driver;
			byte[] data = scrnShot.getScreenshotAs(OutputType.BYTES);
			scn.attach(data, "image/png","Failed Step Name: " + s.getName());
		}else{
			scn.log("Test case is passed, no screen shot captured");
		}
	}

}
