package org.alentar.parallelportmon.scripts;


import org.alentar.parallelportmon.utils.FileUtils;

import java.util.HashMap;
import java.util.logging.Logger;

public class TemplateManager {
    private static TemplateManager instance;

    private HashMap<String, String> templateScripts;

    private TemplateManager() {
        templateScripts = new HashMap<>();
    }

    public static TemplateManager getInstance() {
        if (instance == null) instance = new TemplateManager();
        return instance;
    }

    public void addTemplate(String name, String data) {
        this.templateScripts.put(name, data);
    }

    public void loadScriptFromResources(String name) throws Exception {
        String path = "/script_templates/" + name + ".jstmpl";

        addTemplate(name, FileUtils.readResourceAsString(path));
        Logger.getGlobal().info("Loaded Template: " + name);
    }

    public TemplateScript getTemplateScript(String name) {
        return new TemplateScript(templateScripts.get(name));
    }

    public void removeTemplate(String name) {
        templateScripts.remove(name);
    }
}
