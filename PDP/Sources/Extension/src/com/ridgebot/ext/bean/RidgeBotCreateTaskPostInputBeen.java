package com.ridgebot.ext.bean;

/**
 * This bean class to set all the parameters to invoke create new task REST API
 */
public class RidgeBotCreateTaskPostInputBeen {

    private String name;
    private String summary;
    private String[] targets;
    private int template_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String[] getTargets() {
        return targets;
    }

    public void setTargets(String[] targets) {
        this.targets = targets;
    }

    public int getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(int template_id) {
        this.template_id = template_id;
    }
}
