package com.alone.service;

public interface IWhisperService {
    void initialize();

    /**
     * 从Whisper处理音频
     *
     * @param audioData 音频数据
     * @return {@link String }
     */
    String processAudio(byte[] audioData);

    void close();
}
