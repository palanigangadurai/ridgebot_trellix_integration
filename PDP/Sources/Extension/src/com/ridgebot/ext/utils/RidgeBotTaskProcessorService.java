package com.ridgebot.ext.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * This utility class will help to process the RidgeBot tasks that are fetched from RidgeBot server
 * and create a database batch, then update all the task details into the custom table
 */
public class RidgeBotTaskProcessorService {
    private static final Logger LOGG = Logger.getLogger(RidgeBotTaskProcessorService.class);
    private static int securityHttpsRatio, securityVulTransform, securityAttackSurfaceDensity, securityRiskTransform, securityAliveRatio, securityVulTypeCoverageRatio, taskExploitCount, taskDigCount, taskScanCount, riskCredentialsNum, riskPenetrationNum, riskInfiltratedAssetsNum, riskExploitNum, riskCodeNum, riskNumber, riskShellNum, riskDatabaseNum, chainSensitiveAttackAverage, chainVulAttackAverage, vulNumber, vulInfo, vulTypeNumber, vulHigh, vulMedium, vulLow, attackIpAliveNum, attackIpAsstesNum, attackIpUnaliveNum, attackSiteAsstesNum, attackSiteAliveNum, attackSiteUnaliveNum, attackAsstesNum, attackSurfaceNum = 0;
    private static int securitySafetyIndex = 0;

    public static String processTaskStatistics(JsonObject taskJasonObject, PreparedStatement taskStatisticPrepare, String taskId, String serverName, int serverId) throws Exception {
        JsonObject data = null;
        JsonObject securityJsonObject = null;
        JsonObject taskJsonObject = null;
        JsonObject riskJsonObject = null;
        JsonObject chainJsonObject = null;
        JsonObject attackJsonObject = null;
        try {
            LOGG.debug("RidgeBot :: Started processing Task statistic for task id = " + taskId);
            String message = taskJasonObject.get("msg").getAsString();
            LOGG.debug("RidgeBot :: Started processing Task statistics message  = " + message);
            if (null != taskJasonObject && "success".equals(message) && null != taskStatisticPrepare) {
                data = taskJasonObject.get("data").getAsJsonObject();
                if (null != data) {
                    taskStatisticPrepare.setString(1, taskId);
                    taskStatisticPrepare.setString(2, serverName);
                    taskStatisticPrepare.setInt(3, serverId);
                    securityJsonObject = data.get("security_module").getAsJsonObject();
                    if (null != securityJsonObject) {
                        securityHttpsRatio = isJSONObjectInt(securityJsonObject, "https_ratio") ? securityJsonObject.get("https_ratio").getAsInt() : 0;
                        securityVulTransform = isJSONObjectInt(securityJsonObject, "vul_transform") ? securityJsonObject.get("vul_transform").getAsInt() : 0;
                        securityAttackSurfaceDensity = isJSONObjectInt(securityJsonObject, "attack_surface_density") ? securityJsonObject.get("attack_surface_density").getAsInt() : 0;
                        securityRiskTransform = isJSONObjectInt(securityJsonObject, "risk_transform") ? securityJsonObject.get("risk_transform").getAsInt() : 0;
                        securitySafetyIndex = isJSONObjectInt(securityJsonObject, "safety_index") ? securityJsonObject.get("safety_index").getAsInt() : 404;
                        securityAliveRatio = isJSONObjectInt(securityJsonObject, "alive_ratio") ? securityJsonObject.get("alive_ratio").getAsInt() : 0;
                        securityVulTypeCoverageRatio = isJSONObjectInt(securityJsonObject, "vultype_coverage_ratio") ? securityJsonObject.get("vultype_coverage_ratio").getAsInt() : 0;
                    }
                    taskStatisticPrepare.setInt(4, securityHttpsRatio);
                    taskStatisticPrepare.setInt(5, securityVulTransform);
                    taskStatisticPrepare.setInt(6, securityAttackSurfaceDensity);
                    taskStatisticPrepare.setInt(7, securityRiskTransform);
                    taskStatisticPrepare.setInt(8, securitySafetyIndex);
                    taskStatisticPrepare.setInt(9, securityAliveRatio);
                    taskStatisticPrepare.setInt(10, securityVulTypeCoverageRatio);
                    taskJsonObject = data.get("task_info").getAsJsonObject();
                    if (null != taskJsonObject) {
                        taskExploitCount = isJSONObjectInt(taskJsonObject, "task_exploit_count") ? taskJsonObject.get("task_exploit_count").getAsInt() : 0;
                        taskDigCount = isJSONObjectInt(taskJsonObject, "task_dig_count") ? taskJsonObject.get("task_dig_count").getAsInt() : 0;
                        taskScanCount = isJSONObjectInt(taskJsonObject, "task_scan_count") ? taskJsonObject.get("task_scan_count").getAsInt() : 0;
                    }
                    taskStatisticPrepare.setInt(11, taskExploitCount);
                    taskStatisticPrepare.setInt(12, taskDigCount);
                    taskStatisticPrepare.setInt(13, taskScanCount);
                    riskJsonObject = data.get("risk").getAsJsonObject();
                    if (null != riskJsonObject) {
                        riskCredentialsNum = isJSONObjectInt(riskJsonObject, "risk_credentials_num") ? riskJsonObject.get("risk_credentials_num").getAsInt() : 0;
                        riskPenetrationNum = isJSONObjectInt(riskJsonObject, "penetration_num") ? riskJsonObject.get("penetration_num").getAsInt() : 0;
                        riskInfiltratedAssetsNum = isJSONObjectInt(riskJsonObject, "infiltrated_assets_num") ? riskJsonObject.get("infiltrated_assets_num").getAsInt() : 0;
                        riskExploitNum = isJSONObjectInt(riskJsonObject, "exploit_num") ? riskJsonObject.get("exploit_num").getAsInt() : 0;
                        riskCodeNum = isJSONObjectInt(riskJsonObject, "risk_code_num") ? riskJsonObject.get("risk_code_num").getAsInt() : 0;
                        riskNumber = isJSONObjectInt(riskJsonObject, "risk_number") ? riskJsonObject.get("risk_number").getAsInt() : 0;
                        riskShellNum = isJSONObjectInt(riskJsonObject, "risk_shell_num") ? riskJsonObject.get("risk_shell_num").getAsInt() : 0;
                        riskDatabaseNum = isJSONObjectInt(riskJsonObject, "risk_database_num") ? riskJsonObject.get("risk_database_num").getAsInt() : 0;
                    }
                    taskStatisticPrepare.setInt(14, riskCredentialsNum);
                    taskStatisticPrepare.setInt(15, riskPenetrationNum);
                    taskStatisticPrepare.setInt(16, riskInfiltratedAssetsNum);
                    taskStatisticPrepare.setInt(17, riskExploitNum);
                    taskStatisticPrepare.setInt(18, riskCodeNum);
                    taskStatisticPrepare.setInt(19, riskNumber);
                    taskStatisticPrepare.setInt(20, riskShellNum);
                    taskStatisticPrepare.setInt(21, riskDatabaseNum);

                    chainJsonObject = data.get("chain").getAsJsonObject();
                    if (null != chainJsonObject) {
                        chainSensitiveAttackAverage = isJSONObjectInt(chainJsonObject, "sensitive_attack_average") ? chainJsonObject.get("sensitive_attack_average").getAsInt() : 0;
                        chainVulAttackAverage = isJSONObjectInt(chainJsonObject, "vul_attack_average") ? chainJsonObject.get("vul_attack_average").getAsInt() : 0;
                    }
                    taskStatisticPrepare.setInt(22, chainSensitiveAttackAverage);
                    taskStatisticPrepare.setInt(23, chainVulAttackAverage);
                    JsonObject vulJsonObject = data.get("vul").getAsJsonObject();
                    if (null != vulJsonObject) {
                        vulNumber = isJSONObjectInt(vulJsonObject, "vul_number") ? vulJsonObject.get("vul_number").getAsInt() : 0;
                        vulInfo = isJSONObjectInt(vulJsonObject, "vul_info") ? vulJsonObject.get("vul_info").getAsInt() : 0;
                        vulTypeNumber = isJSONObjectInt(vulJsonObject, "vultype_number") ? vulJsonObject.get("vultype_number").getAsInt() : 0;
                        vulHigh = isJSONObjectInt(vulJsonObject, "vul_high") ? vulJsonObject.get("vul_high").getAsInt() : 0;
                        vulMedium = isJSONObjectInt(vulJsonObject, "vul_medium") ? vulJsonObject.get("vul_medium").getAsInt() : 0;
                        vulLow = isJSONObjectInt(vulJsonObject, "vul_low") ? vulJsonObject.get("vul_low").getAsInt() : 0;
                    }
                    taskStatisticPrepare.setInt(24, vulNumber);
                    taskStatisticPrepare.setInt(25, vulInfo);
                    taskStatisticPrepare.setInt(26, vulTypeNumber);
                    taskStatisticPrepare.setInt(27, vulHigh);
                    taskStatisticPrepare.setInt(28, vulMedium);
                    taskStatisticPrepare.setInt(29, vulLow);
                    attackJsonObject = data.get("attack").getAsJsonObject();
                    if (null != attackJsonObject) {
                        attackIpAliveNum = isJSONObjectInt(attackJsonObject, "ip_alive_num") ? attackJsonObject.get("ip_alive_num").getAsInt() : 0;
                        attackIpAsstesNum = isJSONObjectInt(attackJsonObject, "ip_asstes_num") ? attackJsonObject.get("ip_asstes_num").getAsInt() : 0;
                        attackIpUnaliveNum = isJSONObjectInt(attackJsonObject, "ip_unalive_num") ? attackJsonObject.get("ip_unalive_num").getAsInt() : 0;
                        attackSiteAsstesNum = isJSONObjectInt(attackJsonObject, "site_asstes_num") ? attackJsonObject.get("site_asstes_num").getAsInt() : 0;
                        attackSiteAliveNum = isJSONObjectInt(attackJsonObject, "site_alive_num") ? attackJsonObject.get("site_alive_num").getAsInt() : 0;
                        attackSiteUnaliveNum = isJSONObjectInt(attackJsonObject, "site_unalive_num") ? attackJsonObject.get("site_unalive_num").getAsInt() : 0;
                        attackAsstesNum = isJSONObjectInt(attackJsonObject, "asstes_num") ? attackJsonObject.get("asstes_num").getAsInt() : 0;
                        attackSurfaceNum = isJSONObjectInt(attackJsonObject, "attack_surface_num") ? attackJsonObject.get("attack_surface_num").getAsInt() : 0;
                    }
                    taskStatisticPrepare.setInt(30, attackIpAliveNum);
                    taskStatisticPrepare.setInt(31, attackIpAsstesNum);
                    taskStatisticPrepare.setInt(32, attackIpUnaliveNum);
                    taskStatisticPrepare.setInt(33, attackSiteAsstesNum);
                    taskStatisticPrepare.setInt(34, attackSiteAliveNum);
                    taskStatisticPrepare.setInt(35, attackSiteUnaliveNum);
                    taskStatisticPrepare.setInt(36, attackAsstesNum);
                    taskStatisticPrepare.setInt(37, attackSurfaceNum);
                    taskStatisticPrepare.addBatch();
                    LOGG.debug("RidgeBot :: Task statistic object added to the batch ==== ");
                }
            }
        } catch (Exception ex) {
            LOGG.debug("RidgeBot :: Error Occurred while processing Task statistics Object== " + ex);
            throw ex;
        }
        finally {
            data = null;
            securityJsonObject = null;
            taskJsonObject = null;
            riskJsonObject = null;
            chainJsonObject = null;
            attackJsonObject = null;
        }
        return taskId;
    }

    private static boolean isJSONObjectInt(JsonObject jsonObject, String objectName) {
        return (!jsonObject.get(objectName).isJsonNull()) && NumberUtils.isNumber(jsonObject.get(objectName).getAsString());
    }

    public static String processTaskInfo(JsonElement jsonElement, PreparedStatement taskInfoPrepare, String serverName, int serverId) throws Exception {
        String taskId = "";
        JsonObject taskInfoJsonObj = null;
        try {
            if (null != jsonElement && null != taskInfoPrepare) {

                taskInfoJsonObj = jsonElement.getAsJsonObject();
                if (null != taskInfoJsonObj) {
                    taskId = taskInfoJsonObj.get("task_id").getAsString();
                    LOGG.debug("RidgeBot :: Started processing TaskInfo for task id = " + taskId);
                    int taskJobCount = isJSONObjectInt(taskInfoJsonObj, "task_job_count") ? taskInfoJsonObj.get("task_job_count").getAsInt() : 0;
                    int taskJobTotal = isJSONObjectInt(taskInfoJsonObj, "task_job_total") ? taskInfoJsonObj.get("task_job_total").getAsInt() : 0;
                    int taskStatus = isJSONObjectInt(taskInfoJsonObj, "task_status") ? taskInfoJsonObj.get("task_status").getAsInt() : 0;
                    int percentage = -1;
                    if (taskJobTotal > 0) {
                        percentage = (int) Math.ceil((taskJobCount * 100 / taskJobTotal));
                    }
                    double startTime = 0.0;
                    double stopTime = 0.0;
                    Timestamp startTimeTimeStamp = null;
                    Timestamp stopTimeTimeStamp = null;
                    if (!taskInfoJsonObj.get("task_start_time").isJsonNull()) {
                        startTime = taskInfoJsonObj.get("task_start_time").getAsDouble();
                        startTimeTimeStamp = getTaskTimeStamp((long) startTime);
                        taskInfoPrepare.setString(11, startTimeTimeStamp.toString());
                    } else {
                        taskInfoPrepare.setString(11, "N/A");
                    }
                    if (!taskInfoJsonObj.get("task_stop_time").isJsonNull()) {
                        stopTime = taskInfoJsonObj.get("task_stop_time").getAsDouble();
                        stopTimeTimeStamp = getTaskTimeStamp((long) stopTime);
                        taskInfoPrepare.setString(14, stopTimeTimeStamp.toString());
                    } else {
                        taskInfoPrepare.setString(14, "N/A");
                    }
                    taskInfoPrepare.setString(1, taskId);
                    taskInfoPrepare.setString(2, serverName);
                    taskInfoPrepare.setInt(3, serverId);
                    taskInfoPrepare.setInt(4, taskJobTotal);
                    taskInfoPrepare.setString(5, taskInfoJsonObj.get("detect_type").getAsString());
                    taskInfoPrepare.setInt(6, taskJobCount);
                    taskInfoPrepare.setString(7, taskInfoJsonObj.get("task_summary").getAsString());
                    taskInfoPrepare.setString(8, taskInfoJsonObj.get("task_name").getAsString());
                    taskInfoPrepare.setString(9, taskInfoJsonObj.get("task_targets").getAsJsonArray().toString());
                    taskInfoPrepare.setString(10, taskInfoJsonObj.get("task_nodes").getAsJsonArray().toString());
                    taskInfoPrepare.setString(12, (percentage) + "%");
                    taskInfoPrepare.setString(13, getTaskStatus(taskStatus));
                    taskInfoPrepare.setString(15, taskInfoJsonObj.get("task_type").getAsString());
                    taskInfoPrepare.addBatch();
                    LOGG.debug("RidgeBot :: TaskInfo object added to the batch ======= ");
                }
            }
        } catch (Exception ex) {
            LOGG.debug("RidgeBot :: Error Occurred while processing Task Info Object== " + ex);
            throw ex;
        }finally {
            taskInfoJsonObj = null;
        }
        return taskId;
    }

    public static Timestamp getTaskTimeStamp(long epochTime) {
        LocalDateTime date = Instant.ofEpochSecond(epochTime).atZone(ZoneId.systemDefault()).toLocalDateTime();
        Timestamp timestamp = Timestamp.valueOf(date);
        return timestamp;
    }

    public static String getTaskStatus(int status) {
        Map<Integer, String> taskStatusMap = new HashMap<Integer, String>();
        taskStatusMap.put(0, "WAITING");
        taskStatusMap.put(1, "RUNNING");
        taskStatusMap.put(2, "PAUSE");
        taskStatusMap.put(3, "CANCEL");
        taskStatusMap.put(4, "FINISH");
        taskStatusMap.put(5, "QUEUING");
        taskStatusMap.put(6, "UNACK");
        taskStatusMap.put(7, "DRAFT");
        taskStatusMap.put(8, "SCHEDULED");
        taskStatusMap.put(9, "FINISH_ALL");
        taskStatusMap.put(10, "ERROR");
        if (null != taskStatusMap.get(status)) {
            return taskStatusMap.get(status);
        } else {
            return "UNKNOWN";
        }
    }
}

