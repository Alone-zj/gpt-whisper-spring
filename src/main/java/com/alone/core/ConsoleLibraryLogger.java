package com.alone.core;

import io.github.givimad.whisperjni.WhisperJNI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleLibraryLogger implements WhisperJNI.LibraryLogger {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleLibraryLogger.class);

    @Override
    public void log(String text) {
        logger.debug(text);
    }
}
