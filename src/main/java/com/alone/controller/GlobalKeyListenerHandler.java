package com.alone.controller;

import com.alone.core.DriverManager;
import com.alone.core.ServiceLocator;
import com.alone.service.IAudioService;
import com.alone.service.IBrowserService;
import com.alone.service.IWhisperService;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static com.alone.config.AppConfig.*;

@Component
public class GlobalKeyListenerHandler implements NativeKeyListener {
    private static final Logger logger = LoggerFactory.getLogger(GlobalKeyListenerHandler.class);
    private final ServiceLocator serviceLocator;

    @Autowired
    public GlobalKeyListenerHandler(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    private void onKeyPressed(NativeKeyEvent e) {
        IAudioService audioService = serviceLocator.getAudioService();
        IWhisperService whisperService = serviceLocator.getWhisperService();
        IBrowserService browserService = serviceLocator.getBrowserService();
        if (e.getKeyCode() == GET_ANSWER_KEY) {
            logger.info("截取音频");
            byte[] recent10SecondData = audioService.getRecent10SecondData();
            String text = whisperService.processAudio(recent10SecondData);
            WebDriver driver = DriverManager.getDriver("google", LOCAL_BROWSER_PARAMS, REMOTE_BROWSER_IP_AND_PORT);
            browserService.getAiAnswerAtBrowser(driver, text);
        }
        if (e.getKeyCode() == NativeKeyEvent.VC_END) {
            logger.info("刷新");
        }
        if (e.getKeyCode() == NativeKeyEvent.VC_HOME) {
            logger.info("Stopping audio recording...");
            try {
                releaseResources();
                GlobalScreen.unregisterNativeHook();
            } catch (Exception ex) {
                logger.error("nativeKeyPressed exception is ", ex);
            }
        }
    }

    private void releaseResources() {
        // 释放任何需要释放的资源，例如关闭音频服务、浏览器等
        IWhisperService whisperService = serviceLocator.getWhisperService();
        whisperService.close();
        IAudioService audioService = serviceLocator.getAudioService();
        audioService.stopListening();
        logger.info("Resources released.");
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        onKeyPressed(e);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }
}
