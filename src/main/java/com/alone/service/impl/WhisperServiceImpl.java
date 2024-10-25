package com.alone.service.impl;

import com.alone.core.WhisperManager;
import com.alone.service.IWhisperService;
import io.github.givimad.whisperjni.WhisperContext;
import io.github.givimad.whisperjni.WhisperFullParams;
import io.github.givimad.whisperjni.WhisperJNI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import static com.alone.config.AppConfig.AUDIO_FORMAT;

@Service
public class WhisperServiceImpl implements IWhisperService {
    private static final Logger logger = LoggerFactory.getLogger(WhisperServiceImpl.class);
    private WhisperJNI whisper;
    private WhisperContext ctx;
    private boolean initialized = false;

    @Override
    public void initialize() {
        if (initialized) {
            logger.warn("WhisperService is already initialized.");
            return;
        }

        WhisperManager manager = WhisperManager.getInstance();
        this.whisper = manager.getWhisper();
        this.ctx = manager.getContext();
        initialized = true;
        logger.info("WhisperService initialized.");
    }

    @Override
    public void close() {
        if (ctx != null) {
            WhisperManager.getInstance().close();
            initialized = false;
            logger.info("WhisperService closed.");
        }
    }

    @Override
    public String processAudio(byte[] audioData) {
        float[] samples = readSamples(audioData);
        //for (int i = 0; i < samples.length; i++) {
        //    // 设置阈值 // 设置为零，去除噪声
        //    if (Math.abs(samples[i]) < 0.002) {
        //        samples[i] = 0;
        //    }
        //}
        // 处理音频并返回结果
        WhisperFullParams params = new WhisperFullParams();
        params.language = "zh";
        params.audioCtx = 1300;
        params.nThreads = 14;
        params.translate = false;
        params.singleSegment = true;
        params.initialPrompt = " Java Spring";

        int result = whisper.full(ctx, params, samples, samples.length);
        if (result != 0) {
            throw new RuntimeException("Transcription failed with code " + result);
        }
        // 获取识别结果
        int numSegments = whisper.fullNSegments(ctx);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < numSegments; i++) {
            String text = whisper.fullGetSegmentText(ctx, i);
            stringBuilder.append(text);
        }
        logger.info("Transcription: {}", stringBuilder);
        return stringBuilder.toString();
    }

    private float[] readSamples(byte[] byteArray) {
        int frameSize = AUDIO_FORMAT.getFrameSize();
        int totalFrames = byteArray.length / frameSize;
        float[] samples = new float[totalFrames];
        int sampleSizeInBytes = AUDIO_FORMAT.getSampleSizeInBits() / 8;
        boolean isBigEndian = AUDIO_FORMAT.isBigEndian();

        for (int i = 0; i < totalFrames; i++) {
            int sampleIndex = i * frameSize;
            int sample = 0;
            for (int byteIndex = 0; byteIndex < sampleSizeInBytes; byteIndex++) {
                int byteValue = byteArray[sampleIndex + (isBigEndian ? byteIndex : (sampleSizeInBytes - 1 - byteIndex))] & 0xFF;
                sample = (sample << 8) | byteValue;
            }
            if (sampleSizeInBytes == 2) {
                sample = (short) sample;
            }
            samples[i] = sample / 32768.0f;
        }
        return samples;
    }
}
