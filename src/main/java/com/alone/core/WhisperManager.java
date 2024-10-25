package com.alone.core;

import io.github.givimad.whisperjni.WhisperContext;
import io.github.givimad.whisperjni.WhisperJNI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;

public class WhisperManager {
    private static final Logger logger = LoggerFactory.getLogger(WhisperManager.class);
    private static WhisperManager instance;
    private WhisperJNI whisper;
    private WhisperContext ctx;

    private WhisperManager() {
        try {
            WhisperJNI.loadLibrary();
            ConsoleLibraryLogger libraryLogger = new ConsoleLibraryLogger();
            WhisperJNI.setLibraryLogger(libraryLogger);
            whisper = new WhisperJNI();
            //Path resourcePath = Paths.get(ResourceLoader.class.getClassLoader().getResource("grammar.txt").getPath());
            //WhisperGrammar whisperGrammar = whisper.parseGrammar(resourcePath);
            //var params = new WhisperFullParams();
            //params.grammar = whisperGrammar;
            //params.grammarPenalty = 100f;
            // 小
            //Path path = Path.of(System.getProperty("user.home"), "ggml-model-whisper-base.bin");
            // 大
            Path path = Path.of(System.getProperty("user.home"), "ggml-model-whisper-small.bin");
            ctx = whisper.init(path);
        } catch (Exception e) {
            logger.error("WhisperManager is error", e);
        }
    }

    public static synchronized WhisperManager getInstance() {
        if (instance == null) {
            instance = new WhisperManager();
        }
        return instance;
    }

    public WhisperJNI getWhisper() {
        return whisper;
    }

    public WhisperContext getContext() {
        return ctx;
    }

    public void close() {
        if (ctx != null) {
            ctx.close();
        }
    }
}
