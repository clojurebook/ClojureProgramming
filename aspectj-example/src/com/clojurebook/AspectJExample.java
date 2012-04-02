package com.clojurebook;

public class AspectJExample {
    public void longRunningMethod () {
        System.out.println("Starting long-running method");
        try {
            Thread.sleep((long)(1000 + Math.random() * 2000));
        } catch (InterruptedException e) {
        }
    }
}
