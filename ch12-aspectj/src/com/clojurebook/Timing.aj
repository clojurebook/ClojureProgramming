package com.clojurebook;

public aspect Timing {
    pointcut profiledMethods(): call(* AspectJExample.* (..));
    
    long time;
    
    before(): profiledMethods() {
        time = System.currentTimeMillis();
    }
    
    after(): profiledMethods() {
        System.out.println("Call to " + thisJoinPoint.getSignature() +
                " took " + (System.currentTimeMillis() - time) + "ms");
    }
}
