package com.vikrambhat.plugins;

import com.vikrambhat.core.HelloStrategy;

public class SpanishHello implements HelloStrategy {

    @Override
    public String sayHello(String name) {
        return "¡Hola! desde plugin, " + name + "!";
    }

}
