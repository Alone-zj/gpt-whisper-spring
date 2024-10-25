package com.alone.service;

public interface IAudioService {

    void startListening();

    void stopListening();

    /**
     * 获取最近10秒数据
     *
     * @return {@link byte[] } 数据
     */
    byte[] getRecent10SecondData();
}
