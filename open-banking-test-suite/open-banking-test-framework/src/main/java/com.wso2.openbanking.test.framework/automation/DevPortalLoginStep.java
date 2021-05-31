package com.wso2.openbanking.test.framework.automation;

import com.wso2.openbanking.test.framework.util.ConfigParser;
import com.wso2.openbanking.test.framework.util.TestConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

/**
 * Dev Portal Login Steps.
 */
public class DevPortalLoginStep implements BrowserAutomationStep {

	public String devPortalLoginStep;

	/**
	 * Initialize DevPortal Login Step.
	 *
	 * @param devPortalLoginStep devPortal URL.
	 */
	public DevPortalLoginStep(String devPortalLoginStep) {
		this.devPortalLoginStep = devPortalLoginStep;
	}

	/**
	 * Execute automation using driver.
	 *
	 * @param webDriver driver object.
	 * @param context   automation context.
	 */
	@Override
	public void execute(RemoteWebDriver webDriver, BrowserAutomation.AutomationContext context) {

		webDriver.navigate().to(devPortalLoginStep);
		webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		webDriver.findElement(By.xpath(TestConstants.BTN_DEVPORTAL_SIGNIN)).click();
		WebElement username;

		//Enter User Name
		username = webDriver.findElement(By.id(TestConstants.USERNAME_FIELD_ID));
		username.clear();
		username.sendKeys(ConfigParser.getInstance().getTppUserName());

		WebElement password = webDriver.findElement(By.id(TestConstants.PASSWORD_FIELD_ID));
		password.clear();
		password.sendKeys(ConfigParser.getInstance().getTppPassword());

		//Click on Continue Button
		webDriver.findElement(By.xpath(TestConstants.BTN_CONTINUE)).submit();

		WebDriverWait wait = new WebDriverWait(webDriver, 30);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(
						By.id(TestConstants.USERNAME_FIELD_ID)));
	}
}
