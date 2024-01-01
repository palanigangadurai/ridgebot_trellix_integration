package com.ridgebot.ext.model;

import com.mcafee.orion.core.db.base.DatabaseMapper;

/**
 * This class is used to render the detail subpage for the given task object.
 */
public class RidgeBotTaskDetailsMapper extends DatabaseMapper {
    private static final String TABLENAME = "RidgeBotTaskInfo";
    private long autoId;
    private String serverName;
    private int serverId;
    private String taskId;
    private int taskJobTotal;
    private String taskDetectType;
    private int taskJobCounts;
    private String taskSummary;
    private String taskName;
    private String taskTargets;
    private String taskNodes;
    private String taskStartTime;
    private String taskProgress;
    private String taskStatus;
    private String taskCompleteTime;
    private String taskType;

    public RidgeBotTaskDetailsMapper() {
        super(RidgeBotTaskDetailsMapper.class, TABLENAME,
                new DatabaseMapper.Column[]{
                        new DatabaseMapper.LongColumn("AutoId", "AutoId", 0), new DatabaseMapper.StringColumn("TaskId", "TaskId", 9, 256),
                        new DatabaseMapper.IntColumn("ServerId", "ServerId", 0), new DatabaseMapper.StringColumn("ServerName", "ServerName", 9, 1024),
                        new DatabaseMapper.IntColumn("TaskJobCounts", "TaskJobCounts", 0), new DatabaseMapper.StringColumn("TaskName", "TaskName", 0, 512)
                });
    }

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

    public String getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(String taskStartTimedatetime) {
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

    public String getTaskCompleteTime() {
        return taskCompleteTime;
    }

    public void setTaskCompleteTime(String taskCompleteTime) {
        this.taskCompleteTime = taskCompleteTime;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public long getAutoId() {
        return autoId;
    }

    public void setAutoId(long autoId) {
        this.autoId = autoId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
}
