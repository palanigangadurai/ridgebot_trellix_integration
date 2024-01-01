package com.ridgebot.ext.bean;

import java.util.List;

/**
 * This bean class to set all the parameters to invoke delete task REST API
 */
public class RidgeBotDeleteTaskPostInputBean {
    private List tasks_id;

    public List getTasks_id() {
        return tasks_id;
    }

    public void setTasks_id(List tasks_id) {
        this.tasks_id = tasks_id;
    }
}
