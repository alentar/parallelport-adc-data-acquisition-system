package org.alentar.parallelportmon.scripts;

import org.alentar.parallelportmon.utils.FileUtils;

import javax.script.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScriptManager {
    private static ScriptManager instance;
    private ScriptEngine scriptEngine;
    private Invocable invocable;
    private Compilable compilable;

    private ScriptManager() {
        this.scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            loadScriptFromResources("script_manager.js");
            loadScriptFromResources("math.js");

            invocable = (Invocable) scriptEngine;
            compilable = (Compilable) scriptEngine;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static ScriptManager getInstance() {
        if (instance == null) instance = new ScriptManager();
        return instance;
    }

    public void loadScriptFromResources(String name) throws Exception {
        scriptEngine.eval(FileUtils.readResourceAsString("/scripts/" + name));
    }

    public void validateScript(String script) throws ScriptException {
        compilable.compile(script);
    }

    public Invocable getInvocable() {
        return (Invocable) scriptEngine;
    }

    public void registerMethod(Object object, String name, String fn) throws ScriptException, NoSuchMethodException {
        invocable.invokeFunction("registerMethod", object.hashCode(), name, fn);
    }

    public void unregisterMethod(Object object, String name) throws ScriptException, NoSuchMethodException {
        invocable.invokeFunction("unregisterMethod", object.hashCode(), name);
    }

    public void unregisterObject(Object object) throws ScriptException, NoSuchMethodException {
        invocable.invokeFunction("unregisterObject", object.hashCode());
    }

    public void registerObject(Object object) throws ScriptException, NoSuchMethodException {
        invocable.invokeFunction("registerObject", object.hashCode());
    }

    public Object invokeMethod(Object object, String name, Object... params) throws ScriptException, NoSuchMethodException {
        List<Object> arr = new ArrayList<>();
        arr.add(object.hashCode());
        arr.add(name);
        arr.addAll(Arrays.asList(params));

        return invocable.invokeFunction("invoke", arr.toArray());
    }
}
