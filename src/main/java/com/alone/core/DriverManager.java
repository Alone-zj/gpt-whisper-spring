package com.alone.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class DriverManager {
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static Map<String, WebDriver> drivers = new HashMap<>();

    public static WebDriver getDriver(String browserType, String localBrowserParams, String remoteBrowserIpAndPort) {
        if (!drivers.containsKey(browserType)) {
            boolean isBrowserRunning = checkIfBrowserRunning(remoteBrowserIpAndPort);
            if (!isBrowserRunning) {
                startBrowser(remoteBrowserIpAndPort, localBrowserParams, browserType);
            }

            ChromeOptions options = new ChromeOptions();
            options.addArguments(localBrowserParams);
            options.setExperimentalOption("debuggerAddress", remoteBrowserIpAndPort);
            WebDriverManager.chromedriver().setup();
            WebDriver driver = new ChromeDriver(options);
            drivers.put(browserType, driver);
        }
        return drivers.get(browserType);
    }

    private static void startBrowser(String remoteAddress, String localBrowserParams, String browserType) {
        String[] parts = remoteAddress.split(":");
        int port = Integer.parseInt(parts[1]);
        String command;

        if ("google".equals(browserType)) {
            String params = " --remote-debugging-port=" + port + localBrowserParams;
            command = "cmd.exe /c start \"\" \"chrome.exe\" " + params;
        } else {
            // 其他浏览器的启动逻辑
            command = "cmd.exe /c start \"\" \"microsoft-edge:https://yiyan.baidu.com/\"";
        }

        try {
            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            logger.error("打开浏览器出错", e);
        }
    }

    private static boolean checkIfBrowserRunning(String remoteAddress) {
        String[] parts = remoteAddress.split(":");
        String ip = parts[0];
        int port = Integer.parseInt(parts[1]);
        try (Socket socket = new Socket(ip, port)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void quitDrivers() {
        for (WebDriver driver : drivers.values()) {
            if (driver != null) {
                driver.quit();
            }
        }
        drivers.clear();
    }
}
