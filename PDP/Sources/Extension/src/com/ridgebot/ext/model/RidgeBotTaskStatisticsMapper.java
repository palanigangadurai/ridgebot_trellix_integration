package com.ridgebot.ext.model;

import com.mcafee.orion.core.db.base.DatabaseMapper;
/**
 * This class is used to render the detail page for the given task object,
 * when the user clicks on the specific row in table2 on the custom tab UI.
 */
public class RidgeBotTaskStatisticsMapper extends DatabaseMapper {
    private static final String TABLENAME = "RidgeBotStatistics";
    private long autoId;
    private String taskId;
    private String serverName;
    private int serverId;
    private int securityHttpsRatio;
    private int securityVulTransform;
    private int securityAttackSurfaceDensity;
    private int securityRiskTransform;
    private int securitySafetyIndex;
    private int securityAliveRatio;
    private int securityVulTypeCoverageRatio;
    private int taskExploitCount;
    private int taskDigCount;
    private int taskScanCount;
    private int riskCredentialsNum;
    private int riskPenetrationNum;
    private int riskInfiltratedAssetsNum;
    private int riskExploitNum;
    private int riskCodeNum;
    private int riskNumber;
    private int riskShellNum;
    private int riskDatabaseNum;
    private int chainSensitiveAttackAverage;
    private int chainVulAttackAverage;
    private int vulNumber;
    private int vulInfo;
    private int vulTypeNumber;
    private int vulHigh;
    private int vulMedium;
    private int vulLow;
    private int attackIpAliveNum;
    private int attackIpAsstesNum;
    private int attackIpUnaliveNum;
    private int attackSiteAsstesNum;
    private int attackSiteAliveNum;
    private int attackSiteUnaliveNum;
    private int attackAsstesNum;
    private int attackSurfaceNum;

    public RidgeBotTaskStatisticsMapper() {
        super(RidgeBotTaskStatisticsMapper.class, TABLENAME,
                new DatabaseMapper.Column[]{
                        new DatabaseMapper.LongColumn("AutoId", "AutoId", 9),
                        new DatabaseMapper.StringColumn("TaskId", "TaskId", 0, true, 256),
                        new DatabaseMapper.StringColumn("ServerName", "ServerName", 0, true, 1024),
                        new DatabaseMapper.IntColumn("ServerId", "ServerId", 0),
                        new DatabaseMapper.IntColumn("SecurityHttpsRatio", "SecurityHttpsRatio", 0),
                        new DatabaseMapper.IntColumn("SecurityVulTransform", "SecurityVulTransform", 0),
                        new DatabaseMapper.IntColumn("SecurityAttackSurfaceDensity", "SecurityAttackSurfaceDensity", 0),
                        new DatabaseMapper.IntColumn("SecurityRiskTransform", "SecurityRiskTransform", 0),
                        new DatabaseMapper.IntColumn("SecuritySafetyIndex", "SecuritySafetyIndex", 0),
                        new DatabaseMapper.IntColumn("SecurityAliveRatio", "SecurityAliveRatio", 0),
                        new DatabaseMapper.IntColumn("SecurityVulTypeCoverageRatio", "SecurityVulTypeCoverageRatio", 0),
                        new DatabaseMapper.IntColumn("TaskExploitCount", "TaskExploitCount", 0),
                        new DatabaseMapper.IntColumn("TaskDigCount", "TaskDigCount", 0),
                        new DatabaseMapper.IntColumn("TaskScanCount", "TaskScanCount", 0),
                        new DatabaseMapper.IntColumn("RiskCredentialsNum", "RiskCredentialsNum", 0),
                        new DatabaseMapper.IntColumn("RiskPenetrationNum", "RiskPenetrationNum", 0),
                        new DatabaseMapper.IntColumn("RiskInfiltratedAssetsNum", "RiskInfiltratedAssetsNum", 0),
                        new DatabaseMapper.IntColumn("RiskExploitNum", "RiskExploitNum", 0),
                        new DatabaseMapper.IntColumn("RiskCodeNum", "RiskCodeNum", 0),
                        new DatabaseMapper.IntColumn("RiskNumber", "RiskNumber", 0),
                        new DatabaseMapper.IntColumn("RiskShellNum", "RiskShellNum", 0),
                        new DatabaseMapper.IntColumn("RiskDatabaseNum", "RiskDatabaseNum", 0),
                        new DatabaseMapper.IntColumn("ChainSensitiveAttackAverage", "ChainSensitiveAttackAverage", 0),
                        new DatabaseMapper.IntColumn("ChainVulAttackAverage", "ChainVulAttackAverage", 0),
                        new DatabaseMapper.IntColumn("VulNumber", "VulNumber", 0),
                        new DatabaseMapper.IntColumn("VulInfo", "VulInfo", 0),
                        new DatabaseMapper.IntColumn("VulTypeNumber", "VulTypeNumber", 0),
                        new DatabaseMapper.IntColumn("VulHigh", "VulHigh", 0),
                        new DatabaseMapper.IntColumn("VulMedium", "VulMedium", 0),
                        new DatabaseMapper.IntColumn("VulLow", "VulLow", 0),
                        new DatabaseMapper.IntColumn("AttackIpAliveNum", "AttackIpAliveNum", 0),
                        new DatabaseMapper.IntColumn("AttackIpAsstesNum", "AttackIpAsstesNum", 0),
                        new DatabaseMapper.IntColumn("AttackIpUnaliveNum", "AttackIpUnaliveNum", 0),
                        new DatabaseMapper.IntColumn("AttackSiteAsstesNum", "AttackSiteAsstesNum", 0),
                        new DatabaseMapper.IntColumn("AttackSiteAliveNum", "AttackSiteAliveNum", 0),
                        new DatabaseMapper.IntColumn("AttackSiteUnaliveNum", "AttackSiteUnaliveNum", 0),
                        new DatabaseMapper.IntColumn("AttackAsstesNum", "AttackAsstesNum", 0),
                        new DatabaseMapper.IntColumn("AttackSurfaceNum", "AttackSurfaceNum", 0)
                });
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getSecurityHttpsRatio() {
        return securityHttpsRatio;
    }

    public void setSecurityHttpsRatio(int securityHttpsRatio) {
        this.securityHttpsRatio = securityHttpsRatio;
    }

    public int getSecurityVulTransform() {
        return securityVulTransform;
    }

    public void setSecurityVulTransform(int securityVulTransform) {
        this.securityVulTransform = securityVulTransform;
    }

    public int getSecurityAttackSurfaceDensity() {
        return securityAttackSurfaceDensity;
    }

    public void setSecurityAttackSurfaceDensity(int securityAttackSurfaceDensity) {
        this.securityAttackSurfaceDensity = securityAttackSurfaceDensity;
    }

    public int getSecurityRiskTransform() {
        return securityRiskTransform;
    }

    public void setSecurityRiskTransform(int securityRiskTransform) {
        this.securityRiskTransform = securityRiskTransform;
    }

    public int getSecuritySafetyIndex() {
        return securitySafetyIndex;
    }

    public void setSecuritySafetyIndex(int securitySafetyIndex) {
        this.securitySafetyIndex = securitySafetyIndex;
    }

    public int getSecurityAliveRatio() {
        return securityAliveRatio;
    }

    public void setSecurityAliveRatio(int securityAliveRatio) {
        this.securityAliveRatio = securityAliveRatio;
    }

    public int getSecurityVulTypeCoverageRatio() {
        return securityVulTypeCoverageRatio;
    }

    public void setSecurityVulTypeCoverageRatio(int securityVulTypeCoverageRatio) {
        this.securityVulTypeCoverageRatio = securityVulTypeCoverageRatio;
    }

    public int getTaskExploitCount() {
        return taskExploitCount;
    }

    public void setTaskExploitCount(int taskExploitCount) {
        this.taskExploitCount = taskExploitCount;
    }

    public int getTaskDigCount() {
        return taskDigCount;
    }

    public void setTaskDigCount(int taskDigCount) {
        this.taskDigCount = taskDigCount;
    }

    public int getTaskScanCount() {
        return taskScanCount;
    }

    public void setTaskScanCount(int taskScanCount) {
        this.taskScanCount = taskScanCount;
    }

    public int getRiskCredentialsNum() {
        return riskCredentialsNum;
    }

    public void setRiskCredentialsNum(int riskCredentialsNum) {
        this.riskCredentialsNum = riskCredentialsNum;
    }

    public int getRiskPenetrationNum() {
        return riskPenetrationNum;
    }

    public void setRiskPenetrationNum(int riskPenetrationNum) {
        this.riskPenetrationNum = riskPenetrationNum;
    }

    public int getRiskInfiltratedAssetsNum() {
        return riskInfiltratedAssetsNum;
    }

    public void setRiskInfiltratedAssetsNum(int riskInfiltratedAssetsNum) {
        this.riskInfiltratedAssetsNum = riskInfiltratedAssetsNum;
    }

    public int getRiskExploitNum() {
        return riskExploitNum;
    }

    public void setRiskExploitNum(int riskExploitNum) {
        this.riskExploitNum = riskExploitNum;
    }

    public int getRiskCodeNum() {
        return riskCodeNum;
    }

    public void setRiskCodeNum(int riskCodeNum) {
        this.riskCodeNum = riskCodeNum;
    }

    public int getRiskNumber() {
        return riskNumber;
    }

    public void setRiskNumber(int riskNumber) {
        this.riskNumber = riskNumber;
    }

    public int getRiskShellNum() {
        return riskShellNum;
    }

    public void setRiskShellNum(int riskShellNum) {
        this.riskShellNum = riskShellNum;
    }

    public int getRiskDatabaseNum() {
        return riskDatabaseNum;
    }

    public void setRiskDatabaseNum(int riskDatabaseNum) {
        this.riskDatabaseNum = riskDatabaseNum;
    }

    public int getChainSensitiveAttackAverage() {
        return chainSensitiveAttackAverage;
    }

    public void setChainSensitiveAttackAverage(int chainSensitiveAttackAverage) {
        this.chainSensitiveAttackAverage = chainSensitiveAttackAverage;
    }

    public int getChainVulAttackAverage() {
        return chainVulAttackAverage;
    }

    public void setChainVulAttackAverage(int chainVulAttackAverage) {
        this.chainVulAttackAverage = chainVulAttackAverage;
    }

    public int getVulNumber() {
        return vulNumber;
    }

    public void setVulNumber(int vulNumber) {
        this.vulNumber = vulNumber;
    }

    public int getVulInfo() {
        return vulInfo;
    }

    public void setVulInfo(int vulInfo) {
        this.vulInfo = vulInfo;
    }

    public int getVulTypeNumber() {
        return vulTypeNumber;
    }

    public void setVulTypeNumber(int vulTypeNumber) {
        this.vulTypeNumber = vulTypeNumber;
    }

    public int getVulHigh() {
        return vulHigh;
    }

    public void setVulHigh(int vulHigh) {
        this.vulHigh = vulHigh;
    }

    public int getVulMedium() {
        return vulMedium;
    }

    public void setVulMedium(int vulMedium) {
        this.vulMedium = vulMedium;
    }

    public int getVulLow() {
        return vulLow;
    }

    public void setVulLow(int vulLow) {
        this.vulLow = vulLow;
    }

    public int getAttackIpAliveNum() {
        return attackIpAliveNum;
    }

    public void setAttackIpAliveNum(int attackIpAliveNum) {
        this.attackIpAliveNum = attackIpAliveNum;
    }

    public int getAttackIpAsstesNum() {
        return attackIpAsstesNum;
    }

    public void setAttackIpAsstesNum(int attackIpAsstesNum) {
        this.attackIpAsstesNum = attackIpAsstesNum;
    }

    public int getAttackIpUnaliveNum() {
        return attackIpUnaliveNum;
    }

    public void setAttackIpUnaliveNum(int attackIpUnaliveNum) {
        this.attackIpUnaliveNum = attackIpUnaliveNum;
    }

    public int getAttackSiteAsstesNum() {
        return attackSiteAsstesNum;
    }

    public void setAttackSiteAsstesNum(int attackSiteAsstesNum) {
        this.attackSiteAsstesNum = attackSiteAsstesNum;
    }

    public int getAttackSiteAliveNum() {
        return attackSiteAliveNum;
    }

    public void setAttackSiteAliveNum(int attackSiteAliveNum) {
        this.attackSiteAliveNum = attackSiteAliveNum;
    }

    public int getAttackSiteUnaliveNum() {
        return attackSiteUnaliveNum;
    }

    public void setAttackSiteUnaliveNum(int attackSiteUnaliveNum) {
        this.attackSiteUnaliveNum = attackSiteUnaliveNum;
    }

    public int getAttackAsstesNum() {
        return attackAsstesNum;
    }

    public void setAttackAsstesNum(int attackAsstesNum) {
        this.attackAsstesNum = attackAsstesNum;
    }

    public int getAttackSurfaceNum() {
        return attackSurfaceNum;
    }

    public void setAttackSurfaceNum(int attackSurfaceNum) {
        this.attackSurfaceNum = attackSurfaceNum;
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
