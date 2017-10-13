package com.sample.app;

import java.io.PrintStream;

import javax.inject.Inject;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;

public class CDIExample {
 
    @Inject
    @KSession
    KieSession kSession;
 
    public void go(PrintStream out) {
        
        kSession.insert("TEST");
        kSession.fireAllRules();
    }
 
    public static void main(String[] args) {
        Weld w = new Weld();
 
        WeldContainer wc = w.initialize();
        CDIExample bean = wc.instance().select(CDIExample.class).get();
        bean.go(System.out);
 
        w.shutdown();
    }
 
}