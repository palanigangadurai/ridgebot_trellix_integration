package com.ridgebot.ext.bean;

import java.sql.Timestamp;

/**
 * This bean class will be used to store all the JSON attribute for RidgeBot Task info
 */
public class RidgeBotTaskInfo {
    private String serverName;
    private String taskId;
    private int taskJobTotal;
    private String taskDetectType;
    private int taskJobCounts;
    private String taskSummary;
    private String taskName;
    private String taskTargets;
    private String taskNodes;
    private Timestamp taskStartTime;
    private String taskProgress;
    private String taskStatus;
    private Timestamp taskCompleteTime;
    private String taskType;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getTaskJobTotal() {
        return taskJobTotal;
    }

    public void setTaskJobTotal(int taskJobTotal) {
        this.taskJobTotal = taskJobTotal;
    }

    public String getTaskDetectType() {
        return taskDetectType;
    }

    public void setTaskDetectType(String taskDetectType) {
        this.taskDetectType = taskDetectType;
    }

    public int getTaskJobCounts() {
        return taskJobCounts;
    }

    public void setTaskJobCounts(int taskJobCounts) {
        this.taskJobCounts = taskJobCounts;
    }

    public String getTaskSummary() {
        return taskSummary;
    }

    public void setTaskSummary(String taskSummary) {
        this.taskSummary = taskSummary;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskTargets() {
        return taskTargets;
    }

    public void setTaskTargets(String taskTargets) {
        this.taskTargets = taskTargets;
    }

    public String getTaskNodes() {
        return taskNodes;
    }

    public void setTaskNodes(String taskNodes) {
        this.taskNodes = taskNodes;
    }

    public Timestamp getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(Timestamp taskStartTimedatetime) {
        this.taskStartTime = taskStartTimedatetime;
    }

    public String getTaskProgress() {
        return taskProgress;
    }

    public void setTaskProgress(String taskProgress) {
        this.taskProgress = taskProgress;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Timestamp getTaskCompleteTime() {
        return taskCompleteTime;
    }

    public void setTaskCompleteTime(Timestamp taskCompleteTime) {
        this.taskCompleteTime = taskCompleteTime;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
}