package com.alone.service.impl;

import com.alone.service.IBrowserService;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.net.Socket;
import java.time.Duration;
import java.util.Objects;
import static com.alone.config.AppConfig.*;

@Service
public class BrowserServiceImpl implements IBrowserService {
    private static final Logger logger = LoggerFactory.getLogger(BrowserServiceImpl.class);

    @Override
    public Boolean checkRemoteUi() {
        String[] parts = REMOTE_BROWSER_IP_AND_PORT.split(":");
        String ip = parts[0];
        int port = Integer.parseInt(parts[1]);

        try (Socket socket = new Socket(ip, port)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void gotoTargetUrl(WebDriver driver, String url) {
        String currentUrl = driver.getCurrentUrl();
        if (StringUtils.isBlank(currentUrl)) {
            driver.get(url);
        } else {
            if (!url.equals(currentUrl)) {
                try {
                    driver.get(url);
                } catch (Exception e) {
                    logger.error("访问URL出错", e);
                }
            }
        }
    }


    @Override
    public void getAiAnswerAtBrowser(WebDriver driver, String questionStr) {
        String currentUrl = driver.getCurrentUrl();
        // 只在当前 URL 与目标 URL 不同的情况下导航
        if (!AI_URL.equals(currentUrl)) {
            driver.get(AI_URL);
        }
        JavascriptExecutor js = (JavascriptExecutor) driver;
        boolean isPageLoaded = Objects.equals(js.executeScript("return document.readyState"), "complete");
        if (isPageLoaded) {
            try {
                WebElement stopButton = driver.findElement(By.cssSelector("div.i7f2fQQE span.mEKFkIX7"));
                stopButton.click();
            } catch (NoSuchElementException e) {
                //logger.info("元素不存在，不处理异常");
            }
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(200));
            WebElement editableDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.yc-editor-wrapper > div.yc-editor")));
            Actions actions = new Actions(driver);
            actions.click(editableDiv).perform();
            editableDiv.sendKeys(TIP + questionStr);
            editableDiv.sendKeys(Keys.RETURN);
        }
    }
}
