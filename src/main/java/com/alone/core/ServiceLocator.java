package com.alone.core;

import com.alone.service.IAudioService;
import com.alone.service.IBrowserService;
import com.alone.service.IWhisperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceLocator {
    private final IAudioService audioService;
    private final IBrowserService browserService;
    private final IWhisperService whisperService;

    @Autowired
    public ServiceLocator(IAudioService audioService, IBrowserService browserService, IWhisperService whisperService) {
        this.audioService = audioService;
        this.browserService = browserService;
        this.whisperService = whisperService;
    }

    public IAudioService getAudioService() {
        return audioService;
    }

    public IBrowserService getBrowserService() {
        return browserService;
    }

    public IWhisperService getWhisperService() {
        return whisperService;
    }
}
