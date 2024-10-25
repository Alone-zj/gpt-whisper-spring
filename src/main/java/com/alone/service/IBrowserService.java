package com.alone.service;

import org.openqa.selenium.WebDriver;

public interface IBrowserService {
    Boolean checkRemoteUi();

    void gotoTargetUrl(WebDriver driver, String url);

    /**
     * 在浏览器上获取人工智能答案
     *
     * @param questionStr 问题字符串
     */
    void getAiAnswerAtBrowser(WebDriver driver, String questionStr);
}
