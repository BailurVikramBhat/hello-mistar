package com.vikrambhat.core;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class LanguageLoader {
    private static final AppLogger logger = new AppLogger();

    public static HelloStrategy loadLanguage(String className) {
        try {
            File pluginsDirectory = new File(System.getProperty("user.dir"), "plugins");
            logger.log("LanguageLoader:try - Successfully loaded plugins directory");
            URL[] urls = { pluginsDirectory.toURI().toURL() };
            logger.log("LanguageLoader:try - Successfully loaded URLs");
            logger.log("URLs: " + Arrays.stream(urls).map(URL::toString).toList());
            try (URLClassLoader loader = new URLClassLoader(urls)) {
                Class<?> singleClass = loader.loadClass(className);
                if (HelloStrategy.class.isAssignableFrom(singleClass)) {
                    logger.log("LanguageLoader:try:try -" + className + " is assignable from HelloStrategy!");
                    return (HelloStrategy) singleClass.getDeclaredConstructor().newInstance();
                } else {
                    System.out.println("Class doesn't implement HelloStrategy");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
