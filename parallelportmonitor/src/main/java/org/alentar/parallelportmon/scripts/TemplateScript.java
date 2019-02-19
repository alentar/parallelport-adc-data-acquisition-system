package org.alentar.parallelportmon.scripts;

public class TemplateScript implements Injectable {
    String script;

    public TemplateScript(String script) {
        this.script = script;
    }

    @Override
    public void inject(String pattern, String value) {
        script = script.replaceAll(pattern, value);
    }

    public String getScript() {
        return script;
    }

    @Override
    public String toString() {
        return script;
    }
}
