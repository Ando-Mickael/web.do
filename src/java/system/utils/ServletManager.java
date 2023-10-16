package system.utils;

import system.annotations.Browseable;
import system.annotations.Url;
import system.exceptions.UrlNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

public class ServletManager {

    public String retrieveUrlFromRawurl(String uri) {
        String result = new String();
        String[] tabPart = uri.split("/");

        /*
         * [0] = ""
         * [1] = projectName
         */
        for (int i = 2; i < tabPart.length; i++) {
            result += tabPart[i];
            result += "/";
        }
        String[] tabResult = result.split(".do");

        return tabResult[0];
    }

    private static ArrayList<Class<?>> findListClasses(File directory, String packageName) throws ClassNotFoundException {
        ArrayList<Class<?>> result = new ArrayList<>();
        if (!directory.exists()) {
            return result;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                result.addAll(findListClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                result.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }

        return result;
    }

    public static ArrayList<Class<?>> getListClasses(String packageName) throws ClassNotFoundException, IOException {
        ArrayList<Class<?>> result = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<?> resources = classLoader.getResources(path);
        ArrayList<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = (URL) resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        for (File directory : dirs) {
            result.addAll(findListClasses(directory, packageName));
        }

        return result;
    }

    public static HashMap<String, ClassMethod> fillData(String packageName) throws ClassNotFoundException, IOException {
        HashMap<String, ClassMethod> result = new HashMap<>();
        ArrayList<Class<?>> listClasses = ServletManager.getListClasses(packageName);

        for (int iClass = 0; iClass < listClasses.size(); iClass++) {
            Class classTmp = listClasses.get(iClass);

            if (classTmp.isAnnotationPresent(Browseable.class)) {
                Method[] tabMethods = classTmp.getDeclaredMethods();

                for (int iMethod = 0; iMethod < tabMethods.length; iMethod++) {
                    Method method = tabMethods[iMethod];

                    if (method.isAnnotationPresent(Url.class)) {
                        String url = method.getAnnotation(Url.class).name();
                        ClassMethod classMethod = new ClassMethod(classTmp.getName(), method.getName());

                        result.put(url, classMethod);
                    }
                }
            }
        }

        return result;
    }

    public boolean checkUrl(String url, HashMap<String, ClassMethod> data) throws UrlNotSupportedException {
        boolean result = true;
        ClassMethod classMethod = data.get(url);

        if (classMethod != null) {
            result = true;
        } else {
            throw new UrlNotSupportedException(url);
        }

        return result;
    }

    private static String myCapitalize(String word) {
        String result = null;

        String firstChar = word.substring(0, 1);
        result = firstChar.toUpperCase() + word.substring(1, word.length());

        return result;
    }

    public static String createSetter(String fieldName) {
        return ("set" + myCapitalize(fieldName));
    }   

}
