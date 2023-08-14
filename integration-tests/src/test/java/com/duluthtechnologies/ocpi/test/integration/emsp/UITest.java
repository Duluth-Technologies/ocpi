package com.duluthtechnologies.ocpi.test.integration.emsp;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

class UITest extends AbstractEMSPTest {

	private static final Logger LOG = LoggerFactory.getLogger(UITest.class);

	@Autowired
	private WebDriver driver;

	@ResourceLock("web-driver") // No parallel actions on browser driver
	@Test
	void testSwaggerUI() throws InterruptedException {
		String url = emspTestInstance.getExternalUrl() + "/" + "swagger-ui.html";
		LOG.info("Opening page [{}]...", url);
		driver.get(url);

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		wait.until(webDriver -> "complete"
				.equals(((JavascriptExecutor) webDriver).executeScript("return document.readyState")));

		// TODO Fix the wait for the page to be loaded
		TimeUnit.SECONDS.sleep(10);

		// Check for the title of the page
		Assertions.assertThat(driver.findElement(By.xpath("//*[contains(text(), 'OCPI API')]"))).isNotNull();
	}

}
