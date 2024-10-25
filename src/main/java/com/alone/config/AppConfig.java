package com.alone.config;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import javax.sound.sampled.AudioFormat;

public interface AppConfig {
    /**
     * ai url
     */
    String AI_URL = "https://yiyan.baidu.com/chat/MjU4MDIzMjI5OTo0NjY0NjQxMjcy";
    // 测试
    String REMOTE_BROWSER_IP_AND_PORT = "127.0.0.1:9333";
    String LOCAL_BROWSER_PARAMS = "";
    // 开发
    //String REMOTE_BROWSER_IP_AND_PORT = "127.0.0.1:9222";
    //String LOCAL_BROWSER_PARAMS = "user-data-dir=C:\\ChromeDev";
    String TIP = "下面的文本有模糊音, 请理解并给出回答:";
    /**
     * 音频格式
     */
    AudioFormat AUDIO_FORMAT = new AudioFormat(16000, 16, 1, true, true);
    /**
     * 获取答案键 NativeKeyEvent.VC_PAGE_DOWN
     */
    int GET_ANSWER_KEY = NativeKeyEvent.VC_PRINTSCREEN;
}
