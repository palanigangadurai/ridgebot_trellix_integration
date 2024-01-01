package com.ridgebot.ext.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mcafee.orion.core.auth.OrionUser;
import com.mcafee.orion.core.auth.UserAware;
import com.mcafee.orion.core.cmd.CommandException;
import com.mcafee.orion.core.cmd.SchedulableCommandBase;
import com.mcafee.orion.core.cmd.SchedulableLogger;
import com.mcafee.orion.core.db.ConnectionBean;
import com.mcafee.orion.core.util.IOUtil;
import com.mcafee.orion.core.util.OrionURI;
import com.mcafee.orion.core.util.resource.LocaleAware;
import com.mcafee.orion.core.util.resource.Resource;
import com.mcafee.orion.rs.servers.RegisteredServer;
import com.mcafee.orion.rs.servers.RegisteredServerService;
import com.ridgebot.ext.restclient.RidgeBotRestClient;
import com.ridgebot.ext.server.RidgeBotRegisteredServerInstance;
import com.ridgebot.ext.utils.RidgeBotTaskProcessorService;
import org.apache.log4j.Logger;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This command class is used to pull all the task details from the RidgeBot server by using the given Registered server instance information
 */
public class RidgeBotTaskProgressUpdateCommand extends SchedulableCommandBase implements ConnectionBean, LocaleAware, UserAware {

    public static final String RUN_PERMISSION = "RidgeBotServiceManager.run";
    private static final Logger LOGG = Logger.getLogger(RidgeBotTaskProgressUpdateCommand.class);
    protected volatile boolean terminated = false;
    private Connection connection = null;
    private String context = null;
    private String configURI = null;
    private String summaryURI = null;
    private Resource resource = null;
    private Locale locale = null;
    private Boolean commandStatus = false;
    private String path = null;
    private String errorStatus = null;
    private OrionUser m_user;
    private SchedulableLogger schedulableLogger = null;
    private String serverId = null;
    private RidgeBotRestClient restClientUtil = null;
    private RidgeBotRegisteredServerInstance registeredServer = null;
    private RegisteredServerService registeredServerService = null;
    private AtomicInteger reportsCompleted = new AtomicInteger(0);
    private AtomicInteger totalReports = new AtomicInteger(0);
    private String taskInfoSubPath = "/tasks/info";
    private String taskStatisticsSubPath = "/tasks/statistics?task_id=";
    private String TASK_INFO_TABLE = "RidgeBotTaskInfo";
    private String TASK_STATISTICS_TABLE = "RidgeBotStatistics";

    /**
     * This method used by EPO server to decide whether the calling user is authorized to call this registered command.
     *
     * @param orionUser
     * @return true will enable the user to call the registered command, false will not allow.
     * @throws CommandException
     * @throws URISyntaxException
     */
    public boolean authorize(OrionUser orionUser) throws CommandException, URISyntaxException {
        m_user = orionUser;
        return orionUser.isAllowed("perm:" + RUN_PERMISSION);
    }

    public RidgeBotRegisteredServerInstance getRegisteredServer() {
        return registeredServer;
    }

    public void setRegisteredServer(RidgeBotRegisteredServerInstance registeredServer) {
        this.registeredServer = registeredServer;
    }

    public Object invoke() throws Exception {
        try {
            checkTerminateStatus();
            reportsCompleted.set(0);
            totalReports.set(0);
            commandStatus = updateTaskProgress();
        } catch (Exception ex) {
            commandStatus = false;
            errorStatus = ex.getMessage();
            this.terminated = false;
            schedulableLogger.writeMessage(SchedulableLogger.STATUS_FAILED,
                    getResource().formatString("ServerTask.Failure.Description", locale, ex.getMessage()));
            schedulableLogger.updateStatus(SchedulableLogger.STATUS_FAILED);
            LOGG.debug("RidgeBot :: Error Occurred while  updating Task Progress : invoke() " + ex);
            return false;
        }
        return commandStatus;
    }

    /**
     * This method will delete all the data from RidgeBotTaskInfo table for a given registered server name
     * @param registeredServerName
     * @return Boolean
     * @throws Exception
     */
    private Boolean deleteTaskInfoBeforeUpdate(String registeredServerName) throws Exception {
        PreparedStatement taskInfoDeletePrepare = null;
        String taskInfoDeleteQuery = "DELETE  FROM " + TASK_INFO_TABLE + " where ServerName='" + registeredServerName + "'";
        try {
            checkTerminateStatus();
            taskInfoDeletePrepare = connection.prepareStatement(taskInfoDeleteQuery);
            taskInfoDeletePrepare.execute();
            connection.commit();
        } catch (SQLException sqle) {
            commandStatus = false;
            errorStatus = sqle.getMessage();
            this.terminated = false;
            schedulableLogger.writeMessage(SchedulableLogger.STATUS_FAILED,
                    getResource().formatString("RideBot.Failure.Description", locale, sqle.getMessage()));
            LOGG.debug("RidgeBot :: Error Occurred while  updating Task Progress : updateTaskProgress() " + sqle);
            throw sqle;
        } catch (Exception ex) {
            commandStatus = false;
            errorStatus = ex.getMessage();
            this.terminated = false;
            schedulableLogger.writeMessage(SchedulableLogger.STATUS_FAILED,
                    getResource().formatString("RideBot.Failure.Description", locale, ex.getMessage()));
            LOGG.debug("RidgeBot :: Error Occurred while  updating Task Progress : updateTaskProgress() " + ex);
            throw new Exception(errorStatus);
        } finally {
            IOUtil.close(taskInfoDeletePrepare);
        }
        return commandStatus;
    }

    /**
     * This method will delete all the data from RidgeBotTaskStatistics table for a given registered server name
     * @param registeredServerName
     * @return Boolean
     * @throws Exception
     */
    private Boolean deleteTaskStatisticsBeforeUpdate(String registeredServerName) throws Exception {

        PreparedStatement taskStatisticDeletePrepare = null;
        String taskStatisticsDeleteQuery = "DELETE FROM " + TASK_STATISTICS_TABLE + " where ServerName='" + registeredServerName + "'";
        try {
            checkTerminateStatus();
            taskStatisticDeletePrepare = connection.prepareStatement(taskStatisticsDeleteQuery);
            taskStatisticDeletePrepare.execute();
            connection.commit();
        } catch (SQLException sqle) {
            commandStatus = false;
            errorStatus = sqle.getMessage();
            this.terminated = false;
            schedulableLogger.writeMessage(SchedulableLogger.STATUS_FAILED,
                    getResource().formatString("RideBot.Failure.Description", locale, sqle.getMessage()));
            LOGG.debug("RidgeBot :: Error Occurred while  updating Task Progress : updateTaskProgress() " + sqle);
            throw sqle;
        } catch (Exception ex) {
            commandStatus = false;
            errorStatus = ex.getMessage();
            this.terminated = false;
            schedulableLogger.writeMessage(SchedulableLogger.STATUS_FAILED,
                    getResource().formatString("RideBot.Failure.Description", locale, ex.getMessage()));
            LOGG.debug("RidgeBot :: Error Occurred while  updating Task Progress : updateTaskProgress() " + ex);
            throw new Exception(errorStatus);
        } finally {
            IOUtil.close(taskStatisticDeletePrepare);
        }
        return commandStatus;
    }

    /**
     * This method will pull all the task info and task statistics information from RidgeBot server by using the given registered server instance details
     * @return Boolean
     * @throws Exception
     */
    private Boolean updateTaskProgress() throws Exception {
        //clear DB to freshly insert new data
        int serverId = Integer.parseInt(getServerId());
        LOGG.debug("RidgeBot :: RidgeBotTaskProgressUpdateCommand :: getting data from RidgeBot server" + serverId);
        PreparedStatement taskInfoPrepare = null;
        PreparedStatement taskStatisticPrepare = null;
        String taskInfoInsertQuery = "INSERT INTO " + TASK_INFO_TABLE + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String taskStatisticsInsertQuery = "INSERT INTO " + TASK_STATISTICS_TABLE + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        JsonParser jsonParse = null;
        RidgeBotRestClient restClient = null;
        JsonObject taskJasonObject = null;
        try {
            jsonParse = new JsonParser();
            RegisteredServer rs = getRegisteredServerService().getServerById(serverId,m_user);
            restClient = getRestClientUtil();
            if (null != rs && null != restClient) {
                OrionURI uri = rs.getURI();
                String serverName = rs.getName();
                Map<String, String> rsParams = uri.getParams();
                String apiKey = rsParams.get("apiKey");
                String restURL = rsParams.get("restURL");
                restClient.setApiKey(apiKey);
                restClient.setRestURL(restURL);
                String taskInfoJson = restClient.sendGetRequest(taskInfoSubPath);
                LOGG.debug("RidgeBot :: taskInfoJson before parsing  ==" + taskInfoJson);
                taskJasonObject = jsonParse.parse(taskInfoJson).getAsJsonObject();
                checkTerminateStatus();
                String message = taskJasonObject.get("msg").getAsString();
                if (null != taskJasonObject && "success".equals(message)) {
                    JsonArray data = taskJasonObject.get("data").getAsJsonArray();
                    if (null != data) {
                        LOGG.debug("RidgeBot :: processing task info JSON ======");
                        taskInfoPrepare = connection.prepareStatement(taskInfoInsertQuery);
                        taskStatisticPrepare = connection.prepareStatement(taskStatisticsInsertQuery);
                        totalReports.set(data.size());
                        for (JsonElement jsonElement : data) {
                            if (null != jsonElement) {
                                String taskId = RidgeBotTaskProcessorService.processTaskInfo(jsonElement, taskInfoPrepare, serverName, serverId);
                                LOGG.debug("RidgeBot :: After processing individual task object = " + taskId);
                                if (null != taskId && !("".equals(taskId))) {
                                    LOGG.debug("RidgeBot :: task Id is not null proceeding with task statistics update == " + taskId);
                                    checkTerminateStatus();
                                    String taskStatisticsJson = restClient.sendGetRequest(taskStatisticsSubPath + taskId);
                                    LOGG.debug("RidgeBot :: taskStatisticsJson before parsing  ==" + taskStatisticsJson);
                                    JsonObject taskStatisticsJasonObject = jsonParse.parse(taskStatisticsJson).getAsJsonObject();
                                    if (null != taskStatisticsJasonObject) {
                                        LOGG.debug("RidgeBot :: Before Processing Task Statistics Object == " + taskId);
                                        RidgeBotTaskProcessorService.processTaskStatistics(taskStatisticsJasonObject, taskStatisticPrepare, taskId, serverName, serverId);
                                        LOGG.debug("RidgeBot :: After Processing Task Statistics Object == " + taskId);
                                        checkTerminateStatus();
                                    }
                                }
                                reportsCompleted.getAndIncrement();
                                checkTerminateStatus();
                            }
                        }
                        LOGG.debug("RidgeBot ::  processing task info JSON completed ======");
                        deleteTaskStatisticsBeforeUpdate(serverName);
                        deleteTaskInfoBeforeUpdate(serverName);
                        taskInfoPrepare.executeBatch();
                        taskStatisticPrepare.executeBatch();
                        connection.commit();
                        LOGG.debug("RidgeBot ::  Task progress Calculation Inserted Into DB Successfully======");
                        commandStatus = true;
                        this.terminated = true;
                    }
                }
            }
        } catch (SQLException sqle) {
            commandStatus = false;
            errorStatus = sqle.getMessage();
            this.terminated = false;
            schedulableLogger.writeMessage(SchedulableLogger.STATUS_FAILED,
                    getResource().formatString("RideBot.Failure.Description", locale, sqle.getMessage()));
            LOGG.debug("RidgeBot :: Error Occurred while  updating Task Progress : updateTaskProgress() " + sqle);
            throw sqle;
        } catch (Exception ex) {
            commandStatus = false;
            errorStatus = ex.getMessage();
            this.terminated = false;
            schedulableLogger.writeMessage(SchedulableLogger.STATUS_FAILED,
                    getResource().formatString("RideBot.Failure.Description", locale, ex.getMessage()));
            LOGG.debug("RidgeBot :: Error Occurred while  updating Task Progress : updateTaskProgress() " + ex);
            throw new Exception(errorStatus);
        } finally {
            IOUtil.close(taskInfoPrepare);
            IOUtil.close(taskStatisticPrepare);
            jsonParse = null;
            restClient = null;
            taskJasonObject = null;
        }
        return commandStatus;
    }

    public void checkTerminateStatus() throws Exception {
        if (isTerminated()) {
            errorStatus = "Command has been terminated by the user";
            commandStatus = false;
            schedulableLogger.writeMessage(SchedulableLogger.STATUS_FAILED,
                    getResource().formatString("RideBot.Failure.Description", locale, errorStatus));
            LOGG.debug("RidgeBot :: Error Occurred while  updating Task Progress : checkTerminateStatus() " + commandStatus);
            throw new Exception(errorStatus);
        }
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale var1) {
        this.locale = var1;
    }


    public String getDescription() {
        if (commandStatus) {
            return resource.getString("ServerTaskCommand.Description", locale);
        } else {
            return resource.getString("ServerTask.Failure.Description", locale) + ":" + errorStatus;
        }
    }

    public String getDisplayName() {
        return resource.getString("ServerTask.DisplayName", locale);
    }


    public Connection getConnection() {
        return connection;
    }

    /**
     * The Spring container will call this method and inject the database connection object of EPO database to this method
     *
     * @param connection, it is the connection object for the ePO DB.
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * This method used by the EPO server to know whether this command is schedulable or not
     *
     * @param orionUser
     * @return Returns true if this user has permissions to schedule this command at all, false if not
     */
    public boolean authorizedToSchedule(OrionUser orionUser) {
        boolean status = false;
        try {
            status = orionUser.isAllowed("perm:" + RUN_PERMISSION);
        } catch (Exception ex) {
            LOGG.debug(ex.getMessage());
            LOGG.debug(getResource().getString("ServerTask.user.error", locale));
        }

        return status;
    }

    public String getContext() {
        return context;
    }

    /**
     * This method will call by Spring to inject the context property from beans.xml.
     *
     * @param context is the context path of the web application.
     */
    public void setContext(String context) {
        this.context = context;
    }

    public String getConfigURI() {
        return configURI;
    }

    /**
     * This method will call by Spring to inject the configURI property.
     *
     * @param configURI
     */
    public void setConfigURI(String configURI) {
        this.configURI = configURI;
    }

    public String getSummaryURI() {
        return summaryURI;
    }

    /**
     * This method will call by Spring to inject the summaryURI property.
     *
     * @param summaryURI
     */
    public void setSummaryURI(String summaryURI) {
        this.summaryURI = summaryURI;
    }

    /**
     * This method is used to add the terminate functionality to the registered command
     *
     * @return true will enable the terminate functionality, false will disable.
     */

    public boolean isTerminated() {
        return this.terminated;
    }

    public boolean terminate() {
        if ((!terminated) || commandStatus) {
            this.terminated = true;
        }
        return this.terminated;
    }

    public double getPercentComplete() {
        if (totalReports.get() == 0) {
            return 0.0;
        }
        return ((double) reportsCompleted.get() / (double) totalReports.get());
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDetailLogName() {
        return getResource().getString("ServerTask.Log.Name", locale);
    }

    public SchedulableLogger getLogger() {
        return schedulableLogger;
    }

    public void setLogger(SchedulableLogger schedulableLogger) {

        this.schedulableLogger = schedulableLogger;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public RidgeBotRestClient getRestClientUtil() {
        return restClientUtil;
    }

    public void setRestClientUtil(RidgeBotRestClient restClientUtil) {
        this.restClientUtil = restClientUtil;
    }

    public RegisteredServerService getRegisteredServerService() {
        return registeredServerService;
    }

    public void setRegisteredServerService(RegisteredServerService registeredServerService) {
        this.registeredServerService = registeredServerService;
    }
}
