package com.vikrambhat.plugins;

import com.vikrambhat.core.HelloStrategy;

public class GermanHello implements HelloStrategy {

    @Override
    public String sayHello(String name) {
        return "Hallo " + name + "!";
    }

}
