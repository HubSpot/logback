/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2022, QOS.ch. All rights reserved.
 * <p>
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 * <p>
 * or (per the licensee's choosing)
 * <p>
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * @author Ceki G&uuml;lc&uuml;
 */
public class EnvUtil {

    private EnvUtil() {
    }

    /**
     * <p>Returns the current version of logback, or null if data is not
     * available.
     * </p>
     *
     * @since 1.3.0
     * @return current version or null if missing version data
     */
    static public String logbackVersion() {
        String moduleVersion = logbackVersionByModule();
        if(moduleVersion != null) {
            return moduleVersion;
        }

        Package pkg = EnvUtil.class.getPackage();
        if (pkg == null) {
            return null;
        }
        return pkg.getImplementationVersion();
    }

    /**
     * <p>Returns the current version of logback via class.getModule() or null if data is not
     * available.
     * </p>
     *
     * @since 1.3.0
     * @return current version or null if missing version data
     */
    static private String logbackVersionByModule() {
        if(!isJDK9OrHigher()) {
            return null;
        }

        try {
            //Module module = EnvUtil.class.getModule();
            Object moduleObject = ReflectionUtil.invokeMethodOnObject(EnvUtil.class, "getModule");
            if (moduleObject == null)
                return null;

            // ModuleDescriptor md = module.getDescriptor();
            Object moduleDescriptorObject = ReflectionUtil.invokeMethodOnObject(moduleObject, "getDescriptor");
            if (moduleDescriptorObject == null)
                return null;

            // Optional<String> opt = md.rawVersion();
            Object optionalStringObject = ReflectionUtil.invokeMethodOnObject(moduleDescriptorObject, "rawVersion");
            if(optionalStringObject == null)
                return null;

            Optional<String> opt = (Optional<String>) optionalStringObject;
            return opt.orElse(null);
        } catch(NoSuchMethodException|IllegalAccessException| InvocationTargetException e) {
            return null;
        }
    }

    static public int getJDKVersion(String javaVersionStr) {
        int version = 0;

        for (char ch : javaVersionStr.toCharArray()) {
            if (Character.isDigit(ch)) {
                version = (version * 10) + (ch - 48);
            } else if (version == 1) {
                version = 0;
            } else {
                break;
            }
        }
        return version;
    }

    static private boolean isJDK_N_OrHigher(int n) {
        String javaVersionStr = System.getProperty("java.version", "");
        if (javaVersionStr.isEmpty())
            return false;

        int version = getJDKVersion(javaVersionStr);
        return version > 0 && n <= version;
    }

    static public boolean isJDK5() {
        return isJDK_N_OrHigher(5);
    }

    static public boolean isJDK6OrHigher() {
        return isJDK_N_OrHigher(6);
    }

    static public boolean isJDK7OrHigher() {
        return isJDK_N_OrHigher(7);
    }

    static public boolean isJDK9OrHigher() {
        return isJDK_N_OrHigher(9);
    }

    static public boolean isJDK16OrHigher() {
        return isJDK_N_OrHigher(16);
    }

    static public boolean isJDK18OrHigher() {
        return isJDK_N_OrHigher(18);
    }

    /**
     * @since logback 1.3.12/1.4.12
     * @return true if runtime JDK is version 21 or higher
     */
    static public boolean isJDK21OrHigher() {
        return isJDK_N_OrHigher(21);
    }

    static public boolean isJaninoAvailable() {
        ClassLoader classLoader = EnvUtil.class.getClassLoader();
        try {
            Class<?> bindingClass = classLoader.loadClass("org.codehaus.janino.ScriptEvaluator");
            return (bindingClass != null);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isWindows() {
        String os = System.getProperty("os.name");
        return os.startsWith("Windows");
    }

    static public boolean isClassAvailable(Class callerClass, String className) {
        ClassLoader classLoader = Loader.getClassLoaderOfClass(callerClass);
        try {
            Class<?> bindingClass = classLoader.loadClass(className);
            return (bindingClass != null);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
