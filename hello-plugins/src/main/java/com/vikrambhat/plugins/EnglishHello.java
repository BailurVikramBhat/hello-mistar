package com.vikrambhat.plugins;

import com.vikrambhat.core.HelloStrategy;

public class EnglishHello implements HelloStrategy {

    @Override
    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }

}
