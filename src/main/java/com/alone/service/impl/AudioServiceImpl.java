package com.alone.service.impl;

import com.alone.service.IAudioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import static com.alone.config.AppConfig.AUDIO_FORMAT;

@Service
public class AudioServiceImpl implements IAudioService {
    private static final Logger logger = LoggerFactory.getLogger(AudioServiceImpl.class);
    /**
     * 采样率Sample Rate: 每秒钟采集的样本数,单位赫兹, 44100 Hz（CD 质量）和 16000 Hz（电话质量）
     * <p>
     * 帧大小Frame Size: 每个音频样本的字节数, 取决于通道数和每个样本的位深度<br>
     * 单声道 16 位音频的帧大小为 2 字节<br>
     * 立体声 16 位音频的帧大小为 4 字节
     */
    private static final int SAMPLE_RATE = (int) AUDIO_FORMAT.getSampleRate();
    /**
     * 缓冲区大小<br>
     * 每秒字节 BYTES_PER_SECOND=SAMPLE_RATE×FRAME_SIZE
     */
    private static final int BYTES_PER_SECOND = SAMPLE_RATE * AUDIO_FORMAT.getFrameSize();
    /**
     * 60秒的音频数据
     */
    private static byte[] audioBuffer = new byte[BYTES_PER_SECOND * 60];

    private static int writeIndex = 0;

    private TargetDataLine line;

    @Override
    public void startListening() {
        try {
            line = AudioSystem.getTargetDataLine(AUDIO_FORMAT);
            line.open(AUDIO_FORMAT);
            line.start();
            logger.info("Listening for audio input...");
            Thread recordingThread = getRecordingThread(line);
            recordingThread.start();
        } catch (Exception e) {
            logger.error("Exception occurred: ", e);
        }
    }

    @Override
    public void stopListening() {
        if (line != null) {
            line.stop();
            line.close();
            logger.info("Stopped listening.");
        }
    }

    private Thread getRecordingThread(TargetDataLine line) {
        return new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                while (true) {
                    int bytesRead = line.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        synchronized (this) {
                            System.arraycopy(buffer, 0, audioBuffer, writeIndex, bytesRead);
                            writeIndex = (writeIndex + bytesRead) % audioBuffer.length;
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error during recording: {}", e.getMessage());
            }
        });
    }

    @Override
    public synchronized byte[] getRecent10SecondData() {
        byte[] recentData = new byte[BYTES_PER_SECOND * 10];
        int currentIndex = writeIndex;
        int startIndex = (currentIndex - recentData.length + audioBuffer.length) % audioBuffer.length;

        if (startIndex < currentIndex) {
            // 单段读取
            System.arraycopy(audioBuffer, startIndex, recentData, 0, currentIndex - startIndex);
        } else {
            // 分段读取
            int firstPartLength = audioBuffer.length - startIndex;
            System.arraycopy(audioBuffer, startIndex, recentData, 0, firstPartLength);
            System.arraycopy(audioBuffer, 0, recentData, firstPartLength, currentIndex);
        }
        return recentData;
    }
}
