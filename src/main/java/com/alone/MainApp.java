package com.alone;

import com.alone.controller.GlobalKeyListenerHandler;
import com.alone.core.DriverManager;
import com.alone.core.ServiceLocator;
import com.alone.service.IAudioService;
import com.alone.service.IBrowserService;
import com.alone.service.IWhisperService;
import com.github.kwhat.jnativehook.GlobalScreen;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import static com.alone.config.AppConfig.*;

/**
 * 主应用程序
 *
 * @author Alone
 */
@Component
@ComponentScan("com.alone")
public class MainApp {
    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);
    private final ServiceLocator serviceLocator;
    private final GlobalKeyListenerHandler globalKeyListenerHandler;


    @Autowired
    public MainApp(ServiceLocator serviceLocator, GlobalKeyListenerHandler globalKeyListenerHandler) {
        this.serviceLocator = serviceLocator;
        this.globalKeyListenerHandler = globalKeyListenerHandler;
    }

    public void run() {
        try {
            initializeBrowser();
            initializeWhisperService();
            startAudioListening();
            registerGlobalKeyListener();
        } catch (Exception e) {
            logger.error("Error during application run: ", e);
        }
    }

    @Bean
    public DriverManager createDriverManager() {
        return new DriverManager();
    }

    private void initializeBrowser() {
        IBrowserService browserService = serviceLocator.getBrowserService();
        if (!browserService.checkRemoteUi()) {
            logger.error("请打开浏览器远程端口后启动.");
            System.exit(1);
        }
        WebDriver driver = DriverManager.getDriver("google", LOCAL_BROWSER_PARAMS, REMOTE_BROWSER_IP_AND_PORT);
        browserService.gotoTargetUrl(driver, AI_URL);
    }

    private void initializeWhisperService() {
        IWhisperService whisperService = serviceLocator.getWhisperService();
        whisperService.initialize();
    }

    private void startAudioListening() {
        IAudioService audioService = serviceLocator.getAudioService();
        audioService.startListening();
    }

    private void registerGlobalKeyListener() {
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(globalKeyListenerHandler);
        } catch (Exception e) {
            logger.error("registerGlobalKeyListener is error ", e);
        }
    }
}
