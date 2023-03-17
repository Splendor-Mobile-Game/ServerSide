package com.github.splendor_mobile_game.handlers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.splendor_mobile_game.utils.Log;
import com.github.splendor_mobile_game.utils.Reflection;

public class ReactionManager {
    public Map<String, Class<? extends Reaction>> reactions = new HashMap<>();
    private String packageToSearchIn;

    public ReactionManager() {
    }

    public ReactionManager(String packageToSearchIn) throws IOException {
        this.packageToSearchIn = packageToSearchIn;
        this.loadFromPackage(this.packageToSearchIn);
    }

    public void loadFromPackage(String packageName) throws IOException {
        this.packageToSearchIn = packageName;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        List<Class<?>> classes = new ArrayList<>();

        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File file = new File(resource.getFile());

            if (!file.isDirectory())
                continue;

            for (File classFile : file.listFiles()) {
                String className = packageName + "." + classFile.getName().replace(".class", "");
                Class<?> clazz;

                try {
                    clazz = Class.forName(className);
                    classes.add(clazz);
                } catch (ClassNotFoundException e) {
                    Log.ERROR("There is class file `" + classFile.getName()
                            + "` where inside there is no class with the same name! Skipping this class.");
                    e.printStackTrace();
                }
            }
        }

        this.loadReactions(classes);
    }

    private void loadReactions(List<Class<?>> classesToSearchIn) {
        if (classesToSearchIn == null) {
            Log.ERROR("Cannot load reaction, because provided list of classes has not been initialized and is null!");
            return;
        }

        for (Class<?> clazz : classesToSearchIn) {
            Class<? extends Reaction> reactionClass;

            try {
                reactionClass = clazz.asSubclass(Reaction.class);
            } catch (ClassCastException e) {
                // Log.WARNING(clazz.getName() + " doesn't implement interface `Reaction`, but is in package `"
                //         + this.packageToSearchIn + "` which is used for storing reactions!");
                continue;
            }

            if (!Reflection.hasOneParameterConstructor(clazz, int.class)) {
                Log.ERROR(
                        clazz.getName()
                                + " was not registered as the Reaction, beacause it doesn't implement constructor with `int` as the only parameter which is required!");
                continue;
            }

            if (!Modifier.isPublic(clazz.getModifiers())) {
                Log.ERROR(clazz.getName() + " was not registered as the Reaction because it's not public!");
                continue;
            }

            String simpleName = clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1);
            this.reactions.put(simpleName, reactionClass);

            Log.INFO("Class `" + clazz.getName() + "` loaded as `" + simpleName + "`");
        }
    }

}
