package com.github.splendor_mobile_game.websocket.handlers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.splendor_mobile_game.database.Database;
import com.github.splendor_mobile_game.websocket.communication.ReceivedMessage;
import com.github.splendor_mobile_game.websocket.utils.Log;
import com.github.splendor_mobile_game.websocket.utils.reflection.Reflection;

/**
 * The `ReactionManager` class manages the loading and storage of `Reaction` classes..
 */
public class ReactionManager {

    /**
     * A `Map` of reaction names to their corresponding `Reaction` classes.
     */
    public Map<String, Class<? extends Reaction>> reactions = new HashMap<>();

    /**
     * The package to search in when loading `Reaction` classes.
     */
    private String packageToSearchIn;

    /**
     * Creates a new `ReactionManager` instance with an empty `reactions` map and a `null` `packageToSearchIn`.
     */
    public ReactionManager() {

    }

    /**
     * Creates a new `ReactionManager` instance and automatically loads `Reaction` classes from the specified package.
     *
     * @param packageToSearchIn the full domain name of the package to search for classes
     * @throws IOException if an I/O error occurs while searching for classes
     */
    public ReactionManager(String packageToSearchIn) throws IOException {
        this.packageToSearchIn = packageToSearchIn;
        this.loadFromPackage(this.packageToSearchIn);
    }

    /**
    * Searches for classes in the specified package and loads reactions from them.
    * This function might not work if your project is on non system partition.
    * 
    * @deprecated
    * @param packageName the full domain name of the package to search for classes
    * @throws IOException if an I/O error occurs while searching for classes
    */
    public void loadFromPackage(String packageName) throws IOException {
        // Set the package to search in
        this.packageToSearchIn = packageName;

        // Get the class loader and package path
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');

        // Find all class files in the package
        List<Class<?>> classes = new ArrayList<>();
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File file = new File(resource.getFile());

            // Skip non-directories
            if (!file.isDirectory()) {
                continue;
            }

            // Add classes to the list
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

        // Load the reactions from the classes
        this.loadReactions(classes);
    }

    /**
    * Loads reactions from the provided list of classes. Only classes that implement the Reaction interface and have a
    * public constructor with a single int parameter will be loaded.
    *
    * @param classesToSearchIn the list of classes to search for reactions
    */
    public void loadReactions(List<Class<?>> classesToSearchIn) {
        // Check if the list of classes is null
        if (classesToSearchIn == null) {
            Log.ERROR("Cannot load reaction, because provided list of classes has not been initialized and is null!");
            return;
        }

        // Iterate over the classes and load reactions
        for (Class<?> clazz : classesToSearchIn) {
            Class<? extends Reaction> reactionClass;

            try {
                // Check if the class implements the Reaction interface
                reactionClass = clazz.asSubclass(Reaction.class);
            } catch (ClassCastException e) {
                // Log.WARNING(clazz.getName() + " doesn't implement interface `Reaction`, but is in package `"
                //         + this.packageToSearchIn + "` which is used for storing reactions!");
                continue;
            }

            // Check if the class has a public constructor with appropriate parameters
            try {
                Reflection.getConstructorWithParameters(clazz, int.class, ReceivedMessage.class, Messenger.class, Database.class);
            } catch (NoSuchMethodException e) {
                Log.ERROR(clazz.getName() + " was not registered as the Reaction, because it doesn't" 
                    + " implement constructor with `int`, `ReceivedMessage`, `Messenger` and `Database`, but it's required!"
                );
            }

            // Check if the class is public
            if (!Modifier.isPublic(clazz.getModifiers())) {
                Log.ERROR(clazz.getName() + " was not registered as the Reaction because it's not public!");
                continue;
            }

            // Get the custom name of the reaction if ReactionName annotation is used
            // else use the class name
            ReactionName reactionNameAnnotation = clazz.getAnnotation(ReactionName.class);
            String reactionNameString;

            if (reactionNameAnnotation == null) {
                // Log.WARNING(clazz.getName() + " doesn't have ReactionName annotation, so it'll be registered with the name of its class!");
                reactionNameString = clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1);
            } else {
                reactionNameString = reactionNameAnnotation.value();
            }

            // Add the reaction to the map
            this.reactions.put(reactionNameString, reactionClass);
            Log.INFO("Class `" + clazz.getName() + "` loaded as `" + reactionNameString + "`");
        }
    }

}
