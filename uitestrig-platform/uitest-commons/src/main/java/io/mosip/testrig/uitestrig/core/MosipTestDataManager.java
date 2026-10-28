package io.mosip.testrig.uitestrig.core;

import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.utils.KernelAuthentication;
import io.mosip.testrig.apirig.utils.KeycloakUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MosipTestDataManager {

    private static final Logger logger = LoggerFactory.getLogger(MosipTestDataManager.class);
    private static boolean initialized = false;
    private static List<String> languageList = new ArrayList<>();

    public static synchronized void initialize() {
        if (initialized) return;
        String moduleName = UiConfigManager.getproperty("moduleName");
        if (moduleName == null || moduleName.isBlank()) {
            throw new IllegalStateException(
                "'moduleName' is not configured. Set it in the module's properties file " +
                "(e.g. moduleName=resident-ui) so Keycloak users are namespaced per module.");
        }
        BaseTestCase.currentModule = BaseTestCase.runContext + moduleName.trim();
        BaseTestCase.setRunContext(detectRunType(), detectJarPath());
        BaseTestCase.copymoduleSpecificAndConfigFile("config");
        BaseTestCase.ApplnURI = ConfigManager.getproperty("apiInternalEndPoint");
        BaseTestCase.kernelAuthLib = new KernelAuthentication();
        KeycloakUserManager.removeUser();
        try {
            KeycloakUserManager.createUsers();
        } catch (RuntimeException e) {
            KeycloakUserManager.removeUser();
            throw e;
        }
        initLanguageList();
        initialized = true;
        logger.info("MosipTestDataManager initialized — module: {}, languages: {}", moduleName, languageList);
    }

    public static synchronized void cleanup() {
        if (!initialized) return;
        KeycloakUserManager.removeUser();
        initialized = false;
        logger.info("Keycloak test users removed");
    }

    public static KernelAuthentication newKernelAuth() {
        return new KernelAuthentication();
    }

    public static List<String> getLanguageList() {
        return Collections.unmodifiableList(languageList);
    }

    private static String detectRunType() {
        // Use the thread context classloader (set by JUnit/IntelliJ to the test project root).
        // IDE/Maven: getResource("") returns a file: URL (target/test-classes/).
        // Fat JAR:   getResource("") returns a jar: URL or null.
        java.net.URL loc = Thread.currentThread().getContextClassLoader().getResource("");
        return (loc != null && "file".equals(loc.getProtocol())) ? "IDE" : "JAR";
    }

    private static String detectJarPath() {
        // Only meaningful in JAR mode. Extract the JAR path from the class resource URL.
        java.net.URL loc = MosipTestDataManager.class.getResource(
                "/" + MosipTestDataManager.class.getName().replace('.', '/') + ".class");
        if (loc == null || !"jar".equals(loc.getProtocol())) return "";
        String path = loc.getPath();
        int excl = path.indexOf('!');
        if (excl > 0) path = path.substring(0, excl);
        if (path.startsWith("file:")) path = path.substring(5);
        return path;
    }

    private static void initLanguageList() {
        String langs = UiConfigManager.getproperty("mosip.mandatory-languages");
        languageList = new ArrayList<>();
        if (langs != null && !langs.isBlank()) {
            for (String lang : langs.split(",")) {
                String t = lang.trim();
                if (!t.isEmpty()) languageList.add(t);
            }
        }
        if (languageList.isEmpty()) languageList.add("eng");
    }
}
