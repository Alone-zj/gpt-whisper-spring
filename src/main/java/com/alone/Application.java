package com.alone;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 启动 Spring 应用上下文
 *
 * @author Alone
 */
public class Application {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainApp.class);
        MainApp app = context.getBean(MainApp.class);
        app.run();
        context.close();
    }
}
