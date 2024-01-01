package com.ridgebot.ext.server;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mcafee.epo.computermgmt.ui.datasource.QueryTableSearchableDS;
import com.mcafee.epo.core.util.ServerConfiguration;
import com.mcafee.orion.core.auth.OrionUser;
import com.mcafee.orion.core.auth.UserAware;
import com.mcafee.orion.core.cmd.SchedulableLogger;
import com.mcafee.orion.core.db.ConnectionBean;
import com.mcafee.orion.core.db.base.DatabaseMapper;
import com.mcafee.orion.core.query.QueryBuilderService;
import com.mcafee.orion.core.query.QueryListDataSource;
import com.mcafee.orion.core.query.table.TableService;
import com.mcafee.orion.core.servlet.ActionResponse;
import com.mcafee.orion.core.ui.DisplayAdapter;
import com.mcafee.orion.core.ui.MultipartFormAction;
import com.mcafee.orion.core.ui.MultipartFormPolicy;
import com.mcafee.orion.core.ui.UIActionHandler;
import com.mcafee.orion.core.ui.uiaction.AsyncUIActionMessageResponse;
import com.mcafee.orion.core.ui.uiaction.MessageType;
import com.mcafee.orion.core.ui.uiaction.ResultCategory;
import com.mcafee.orion.core.util.IOUtil;
import com.mcafee.orion.core.util.OrionURI;
import com.mcafee.orion.core.util.OrionUploadFileItem;
import com.mcafee.orion.core.util.ioc.ContextInjectible;
import com.mcafee.orion.core.util.resource.LocaleAware;
import com.mcafee.orion.core.util.resource.Resource;
import com.mcafee.orion.rs.servers.RegisteredServer;
import com.mcafee.orion.rs.servers.RegisteredServerService;
import com.ridgebot.ext.bean.RidgeBotCreateTaskPostInputBeen;
import com.ridgebot.ext.bean.RidgeBotDeleteTaskPostInputBean;
import com.ridgebot.ext.bean.RidgeBotStopTaskPostInputBean;
import com.ridgebot.ext.model.RidgeBotTaskDetailsMapper;
import com.ridgebot.ext.model.RidgeBotTaskStatisticsMapper;
import com.ridgebot.ext.restclient.RidgeBotRestClient;
import com.ridgebot.ext.scheduler.RidgeBotSelectBeans;
import com.ridgebot.ext.utils.RidgeBotTaskProcessorService;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Null;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * This class will handle all the UI actions that are available in RidgeBot custom tab UI page.
 */
public class RidgeBotRegisteredServerDataActionHandler extends UIActionHandler implements UserAware, LocaleAware, ConnectionBean, ContextInjectible, MultipartFormAction {
    private static final Logger LOGG = Logger.getLogger(RidgeBotRegisteredServerDataActionHandler.class);
    private static final String DATASOURCE_ATTR = "RidgeBotServiceManager" + "RegisteredServerDataSource";
    private static final String RIDGEBOT_DATASOURCE_ATTR = "RidgeBotTaskDataSource";
    private static final String tableName = "RidgeBotServiceManagerRSPartnerData";
    private String TASK_INFO_TABLE = "RidgeBotTaskInfo";
    private String TASK_STATISTICS_TABLE = "RidgeBotStatistics";
    private OrionUser user = null;
    private Connection connection = null;
    private Locale locale = null;
    private QueryTableSearchableDS registeredServerDataSource;
    private QueryBuilderService queryBuilderService;
    private TableService tableService;
    private QueryListDataSource ridgeBotTaskDataSource;
    private RidgeBotRegisteredServerInstance registeredServer = null;
    private RegisteredServerService registeredServerService = null;
    private RidgeBotRestClient restClientUtil = null;
    private Resource resource;
    private String createTaskPath = "/tasks";
    private String deleteTaskPath = "/tasks/delete";
    private String stopTaskPath = "/tasks/stop";
    private String startTaskPath = "/tasks/start";
    private String reStartTaskPath = "/tasks/restart";
    private String pauseTaskPath = "/tasks/pause";
    private String taskInfoSubPath = "/tasks/info?task_id=";
    private String taskStatisticsSubPath = "/tasks/statistics?task_id=";
    private DisplayAdapter displayer = null;
    private DatabaseMapper<RidgeBotTaskDetailsMapper> dataBaseMapper;
    private ServerConfiguration serverConfiguration = null;
    private OrionUploadFileItem fileItem = null;
    private byte[] byteFileContent = null;

    /**
     * This method will display RidgeBot custom tabs on the UI by redirecting the request to the corresponding JSP.
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */

    public ActionResponse showPartnerData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        getRidgeBotTaskDataSource().setUser(user);
        user.setAttribute(RIDGEBOT_DATASOURCE_ATTR, getRidgeBotTaskDataSource());
        return ActionResponse.forward("/S_RIDGBTMETA", "/RidgeBotTaskMangerCustomTab.jsp");
    }

    /**
     * This method will display crate new task UI by redirecting the request to the corresponding JSP.
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionResponse createNewTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            List registeredServerList = null;
            List<RegisteredServer> serverNameList = new ArrayList<RegisteredServer>();
            String serverType = null;
            if (registeredServer != null) {
                LOGG.debug("RidgeBot : createNewTask : registered server is not null");
                serverType = registeredServer.getName();
            }
            registeredServerList = getRegisteredServerService().getServersOfType(serverType,getUser());
            if (registeredServerList != null) {
                for (Object aRegisteredServerList : registeredServerList) {
                    RegisteredServer regServer = (RegisteredServer) aRegisteredServerList;
                    serverNameList.add(regServer);
                }
            }
            LOGG.debug("RidgeBot : createNewTask : server name list " + registeredServerList);
            //SelectBean is used map the values for orion:select tag in the JSP page.
            RidgeBotSelectBeans beansForSelect = new RidgeBotSelectBeans(serverNameList, "id", "name");

            if (serverNameList.size() > 0) {
                request.setAttribute("disallowInput", false);
            } else {
                request.setAttribute("disallowInput", true);
            }
            LOGG.debug("RidgeBot : createNewTask : server name list bean ==" + beansForSelect);
            request.setAttribute("serverNameList", beansForSelect);
        } catch (Exception ex) {
            LOGG.error(ex.getMessage(), ex);
        }
        return ActionResponse.forward("/S_RIDGBTMETA", "/RidgeBotCreateNewTask.jsp");
    }

    /**
     * This method will handle the actions that are performed on the create new task UI
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionResponse createNewTaskAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String taskCreationStatus = "Task Creation Failed";
        String serverIdStr = request.getParameter("serverId");
        LOGG.debug("RidgeBot :: Create new task server id str ===" + serverIdStr);
        String taskName = request.getParameter("taskName");
        String taskDescription = request.getParameter("description");
        String ipAddress = request.getParameter("ipAddress");
        PreparedStatement taskInfoPrepare = null;
        PreparedStatement taskStatisticPrepare = null;
        String taskInfoInsertQuery = "INSERT INTO " + TASK_INFO_TABLE + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String taskStatisticsInsertQuery = "INSERT INTO " + TASK_STATISTICS_TABLE + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        RidgeBotRestClient restClient = null;
        try {
            int serverId = Integer.parseInt(serverIdStr);
            RegisteredServer rs = getRegisteredServerService().getServerById(serverId,getUser());
            restClient = getRestClientUtil();
            if (null != rs && null != restClient) {
                OrionURI uri = rs.getURI();
                String serverName = rs.getName();
                Map<String, String> rsParams = uri.getParams();
                String apiKey = rsParams.get("apiKey");
                String restURL = rsParams.get("restURL");
                restClient.setApiKey(apiKey);
                restClient.setRestURL(restURL + createTaskPath);
                RidgeBotCreateTaskPostInputBeen inputJsonObj = new RidgeBotCreateTaskPostInputBeen();
                inputJsonObj.setName(taskName);
                if (null != ipAddress) {
                    inputJsonObj.setTargets(ipAddress.split(","));
                }
                inputJsonObj.setSummary(taskDescription);
                inputJsonObj.setTemplate_id(1);
                String taskInfoJson = restClient.sendPostRequest(inputJsonObj);
                JsonParser jsonParse = new JsonParser();
                JsonObject taskJasonObject = jsonParse.parse(taskInfoJson).getAsJsonObject();
                String message = taskJasonObject.get("msg").getAsString();
                LOGG.debug("RidgeBot :: Create new task response ==" + message);
                if (null != taskJasonObject && "success".equals(message)) {
                    JsonObject data = taskJasonObject.get("data").getAsJsonObject();
                    if (null != data) {
                        LOGG.debug("RidgeBot :: processing New Tas task info JSON ======");
                        LOGG.debug("RidgeBot :: Created task id ======" + data.get("task_id").getAsString());
                        taskCreationStatus = StringEscapeUtils.escapeHtml(this.resource.getString("RidgeBot.createtask.success", locale));
                        String taskId = data.get("task_id").getAsString();
                        restClient.setRestURL(restURL);
                        taskInfoJson = restClient.sendGetRequest(taskInfoSubPath + taskId);
                        taskJasonObject = jsonParse.parse(taskInfoJson).getAsJsonObject();
                        message = taskJasonObject.get("msg").getAsString();
                        if (null != taskJasonObject && "success".equals(message)) {
                            JsonArray arrayData = taskJasonObject.get("data").getAsJsonArray();
                            if (null != arrayData) {
                                LOGG.debug("RidgeBot :: Create new task processing task info JSON ======");
                                taskInfoPrepare = connection.prepareStatement(taskInfoInsertQuery);
                                taskStatisticPrepare = connection.prepareStatement(taskStatisticsInsertQuery);
                                for (JsonElement jsonElement : arrayData) {
                                    if (null != jsonElement) {
                                        taskId = RidgeBotTaskProcessorService.processTaskInfo(jsonElement, taskInfoPrepare, serverName, serverId);
                                        LOGG.debug("RidgeBot :: Create new task After processing individual task object = " + taskId);
                                        if (null != taskId && !("".equals(taskId))) {
                                            LOGG.debug("RidgeBot ::Create new task task Id is not null proceeding with task statistics update == " + taskId);
                                            String taskStatisticsJson = restClient.sendGetRequest(taskStatisticsSubPath + taskId);
                                            LOGG.debug("RidgeBot :: Create new task taskStatisticsJson before parsing  ==" + taskStatisticsJson);
                                            JsonObject taskStatisticsJasonObject = jsonParse.parse(taskStatisticsJson).getAsJsonObject();
                                            if (null != taskStatisticsJasonObject) {
                                                LOGG.debug("RidgeBot ::Create new task  Before Processing Task Statistics Object == " + taskId);
                                                RidgeBotTaskProcessorService.processTaskStatistics(taskStatisticsJasonObject, taskStatisticPrepare, taskId, serverName, serverId);
                                                LOGG.debug("RidgeBot :: Create new task After Processing Task Statistics Object == " + taskId);
                                            }
                                        }
                                    }
                                }
                                LOGG.debug("RidgeBot ::  Create new task processing task info JSON completed ======");
                                taskInfoPrepare.executeBatch();
                                taskStatisticPrepare.executeBatch();
                                connection.commit();
                                restClient = null;
                                LOGG.debug("RidgeBot :: Create new task Task progress Calculation Inserted Into DB Successfully======");
                                response.getWriter().write(taskCreationStatus + ":Task_Id =" + taskId);
                            }
                        } else {
                            taskCreationStatus = StringEscapeUtils.escapeHtml(this.resource.getString("RidgeBot.createtask.failed", locale));
                            response.getWriter().write(taskCreationStatus);
                        }
                    } else {
                        taskCreationStatus = StringEscapeUtils.escapeHtml(this.resource.getString("RidgeBot.createtask.failed", locale));
                        response.getWriter().write(taskCreationStatus);
                    }
                } else {
                    taskCreationStatus = StringEscapeUtils.escapeHtml(this.resource.getString("RidgeBot.createtask.failed", locale));
                    response.getWriter().write(taskCreationStatus);
                }
            }
        } catch (Exception ex) {
            taskCreationStatus = StringEscapeUtils.escapeHtml(this.resource.getString("RidgeBot.createtask.failed", locale));
            LOGG.error("RidgeBot: Error occurred while creating a new task:=" + ex.getMessage(), ex);
            response.getWriter().write(taskCreationStatus);
        } finally {
            IOUtil.close(taskInfoPrepare);
            IOUtil.close(taskStatisticPrepare);
            restClient = null;
        }
        return ActionResponse.none();
    }

    /**
     * his method will handle the file upload action in the create new task UI
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionResponse importFileContent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fileImportStatus = "File Import failed";
        OrionUploadFileItem ipaddressItem = getFileItem();
        if (null != ipaddressItem && ipaddressItem.getSize() == 0) {
            response.getWriter().write(fileImportStatus);
            return ActionResponse.none();
        }
        if (null != ipaddressItem) {
            String fileContent = ipaddressItem.getAsString("UtF-8");
            LOGG.debug("RidgeBot :: File upload data == " + fileContent);
            response.getWriter().write(fileContent);
        }
        return ActionResponse.none();
    }

    public String getFileNameOnly(String fileName) {
        String tempName = fileName.replaceAll("\\\\", "/");
        tempName = tempName.substring(tempName.lastIndexOf("/") + 1);
        return tempName;
    }

    /**
     * This method will handle delete task UI action on the custom tab.
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public AsyncUIActionMessageResponse deleteTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uids = request.getParameter("UIDs");
        //datasrcAttr user this request parameter to get the registered type object to get selected all UIDS
        //getDatasource(HttpServletRequest request)
        ResultCategory resultCategory = ResultCategory.DELETE_OCCURRED;
        MessageType messageType = MessageType.PLAIN;
        String str = "";
        LOGG.debug("RidgeBot :: Delete Task uid string  ==" + uids);
        if (uids != null) {
            String[] uidArray = uids.split(",");
            PreparedStatement taskInfoSelectPrepare = null;
            String taskInfoSelectQuery = "SELECT * FROM " + TASK_INFO_TABLE + " where AutoId IN (";
            PreparedStatement taskInfoDeletePrepare = null;
            String taskInfoDeleteQuery = "DELETE FROM " + TASK_INFO_TABLE + " where AutoId = ?";
            ResultSet resultSet = null;
            RidgeBotRestClient restClient = null;
            try {
                taskInfoDeletePrepare = connection.prepareStatement(taskInfoDeleteQuery);
                for (String id : uidArray) {
                    int uidVal = Integer.parseInt(id);
                    taskInfoSelectQuery = taskInfoSelectQuery + id + ",";
                    taskInfoDeletePrepare.setInt(1, uidVal);
                    taskInfoDeletePrepare.addBatch();
                }
                String taskInfoSelectQueryTemp = taskInfoSelectQuery.substring(0, taskInfoSelectQuery.length() - 1);
                String taskInfoSelectQueryFinal = taskInfoSelectQueryTemp + ")";
                LOGG.debug("RidgeBot: Task Delete Select Query == " + taskInfoSelectQueryFinal);
                taskInfoSelectPrepare = connection.prepareStatement(taskInfoSelectQueryFinal);
                resultSet = taskInfoSelectPrepare.executeQuery();
                Map<Integer, List<String>> taskInfoMap = new HashMap<Integer, List<String>>();
                while (resultSet.next()) {
                    int serverId = resultSet.getInt("ServerId");
                    String taskId = resultSet.getString("TaskId");
                    if (taskInfoMap.containsKey(serverId)) {
                        List<String> taskListObj = taskInfoMap.get(serverId);
                        taskListObj.add(taskId);
                        taskInfoMap.put(serverId, taskListObj);
                    } else {
                        List<String> taskList = new ArrayList<String>();
                        taskList.add(taskId);
                        taskInfoMap.put(serverId, taskList);
                    }
                }
                JsonParser jsonParse = new JsonParser();
                restClient = getRestClientUtil();
                LOGG.debug("RideBot :: Full delete task info map == " + taskInfoMap);
                for (Map.Entry<Integer, List<String>> entry : taskInfoMap.entrySet()) {
                    int serverId = entry.getKey();
                    RidgeBotDeleteTaskPostInputBean inputJsonObj = new RidgeBotDeleteTaskPostInputBean();
                    inputJsonObj.setTasks_id(entry.getValue());
                    RegisteredServer rs = getRegisteredServerService().getServerById(serverId,getUser());
                    if (null != rs && null != restClient) {
                        OrionURI uri = rs.getURI();
                        Map<String, String> rsParams = uri.getParams();
                        String apiKey = rsParams.get("apiKey");
                        String restURL = rsParams.get("restURL");
                        restClient.setApiKey(apiKey);
                        restClient.setRestURL(restURL + deleteTaskPath);
                        String taskInfoJson = restClient.sendPostRequest(inputJsonObj);
                        JsonObject taskJasonObject = jsonParse.parse(taskInfoJson).getAsJsonObject();
                        String message = taskJasonObject.get("msg").getAsString();
                        LOGG.debug("RidgeBot :: Delete task response ==" + message);
                        if (null != taskJasonObject && "success".equals(message)) {
                            LOGG.debug("RidgeBot: Delete Task Completed in RidgeBot server for Task Ids ====" + inputJsonObj.getTasks_id());
                            taskInfoDeletePrepare.executeBatch();
                            connection.commit();
                            LOGG.debug("RidgeBot: Delete Task Completed in ePO server ====");
                            str = getResource().formatString("RidgeBot.Task.Delete.PostAction", getLocale(), new Object[0]);
                        } else {
                            str = getResource().formatString("RidgeBot.Task.Delete.Error", getLocale(), new Object[0]);
                            resultCategory = ResultCategory.NO_CHANGE;
                            messageType = MessageType.ERROR;
                        }
                    }
                }
            } catch (SQLException sqle) {
                LOGG.debug("RidgeBot :: Error Occurred while deleting task Info : deleteTask() " + sqle);
                str = getResource().formatString("RidgeBot.Task.Delete.Error", getLocale(), new Object[0]);
                resultCategory = ResultCategory.NO_CHANGE;
                messageType = MessageType.ERROR;
                throw sqle;
            } catch (Exception ex) {
                LOGG.debug("RidgeBot :: Error Occurred while deleting task Info : deleteTask() " + ex);
                str = getResource().formatString("RidgeBot.Task.Delete.Error", getLocale(), new Object[0]);
                resultCategory = ResultCategory.NO_CHANGE;
                messageType = MessageType.ERROR;
            } finally {
                IOUtil.close(resultSet);
                IOUtil.close(taskInfoDeletePrepare);
                IOUtil.close(taskInfoSelectPrepare);
                restClient = null;
            }
        }
        return new AsyncUIActionMessageResponse(str, messageType, resultCategory);
    }

    /**
     * This method will render purge task UI by redirect the URL to a specific JSP.
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionResponse purgeTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            List registeredServerList = null;
            List<RegisteredServer> serverNameList = new ArrayList<RegisteredServer>();
            String serverType = null;
            if (registeredServer != null) {
                LOGG.debug("RidgeBot : purgeTask : registered server is not null");
                serverType = registeredServer.getName();
            }
            registeredServerList = getRegisteredServerService().getServersOfType(serverType,getUser());
            if (registeredServerList != null) {
                for (Object aRegisteredServerList : registeredServerList) {
                    RegisteredServer regServer = (RegisteredServer) aRegisteredServerList;
                    serverNameList.add(regServer);
                }
            }
            LOGG.debug("RidgeBot : purgeTask : server name list " + registeredServerList);
            //SelectBean is used map the values for orion:select tag in the JSP page.
            RidgeBotSelectBeans beansForSelect = new RidgeBotSelectBeans(serverNameList, "id", "name");

            if (serverNameList.size() > 0) {
                request.setAttribute("disallowInput", false);
            } else {
                request.setAttribute("disallowInput", true);
            }
            LOGG.debug("RidgeBot : purgeTask : server name list bean ==" + beansForSelect);
            request.setAttribute("serverNameList", beansForSelect);
        } catch (Exception ex) {
            LOGG.error(ex.getMessage(), ex);
        }
        return ActionResponse.forward("/S_RIDGBTMETA", "/RidgeBotPurgeTask.jsp");
    }

    /**
     * This method will handle purge task UI action on the custom tab.
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionResponse purgeTaskAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String taskStatus = "Purge Task Failed";
        String serverIdStr = request.getParameter("serverId");
        String registeredServerName = request.getParameter("serverName");
        LOGG.debug("RidgeBot :: purge task server name str ===" + registeredServerName);
        if(null != registeredServerName)
        {
            registeredServerName = registeredServerName.trim();
            PreparedStatement taskInfoDeletePrepare = null;
            String taskInfoDeleteQuery = "DELETE  FROM " + TASK_INFO_TABLE + " where ServerName='" + registeredServerName + "'";
            PreparedStatement taskStatisticDeletePrepare = null;
            String taskStatisticsDeleteQuery = "DELETE FROM " + TASK_STATISTICS_TABLE + " where ServerName='" + registeredServerName + "'";
            try {
                taskInfoDeletePrepare = connection.prepareStatement(taskInfoDeleteQuery);
                taskInfoDeletePrepare.execute();
               // connection.commit();
                taskStatisticDeletePrepare = connection.prepareStatement(taskStatisticsDeleteQuery);
                taskStatisticDeletePrepare.execute();
                connection.commit();
                LOGG.debug("RidgeBot :: Purge task is completed.");
                taskStatus = StringEscapeUtils.escapeHtml(this.resource.getString("RidgeBot.purgetask.success", locale));
            } catch (SQLException sqlex) {
                LOGG.debug("RidgeBot :: Error Occurred while purging task info" + sqlex);
                taskStatus = StringEscapeUtils.escapeHtml(this.resource.getString("RidgeBot.purgetask.failed", locale));
            } catch (Exception ex) {
                LOGG.debug("RidgeBot :: Error Occurred while  purging task info" + ex);
                taskStatus = StringEscapeUtils.escapeHtml(this.resource.getString("RidgeBot.purgetask.failed", locale));
            } finally {
                IOUtil.close(taskInfoDeletePrepare);
                IOUtil.close(taskStatisticDeletePrepare);
            }
        }
        else
        {
            taskStatus = StringEscapeUtils.escapeHtml(this.resource.getString("RidgeBot.purgetask.failed", locale));
        }
        response.getWriter().write(taskStatus);
        return ActionResponse.none();
    }

    /**
     * This method will handle stop task UI action on the custom tab.
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public AsyncUIActionMessageResponse stopTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uids = request.getParameter("UIDs");
        ResultCategory resultCategory = ResultCategory.DELETE_OCCURRED;
        MessageType messageType = MessageType.PLAIN;
        String str = "";
        String taskStatus = "CANCEL";
        LOGG.debug("RidgeBot :: Stop Task uid string  ==" + uids);
        if (uids != null) {
            int autoId = Integer.parseInt(uids);
            String taskId = "";
            int serverId = 0;
            PreparedStatement taskInfoSelectPrepare = null;
            String taskInfoSelectQuery = "SELECT * FROM " + TASK_INFO_TABLE + " where AutoId=" + autoId;
            String taskInfoUpdateQuery = "UPDATE " + TASK_INFO_TABLE + " set TaskStatus='" + taskStatus + "' where AutoId=" + autoId;
            PreparedStatement taskInfoUpdatePrepare = null;
            ResultSet resultSet = null;
            try {
                taskInfoSelectPrepare = connection.prepareStatement(taskInfoSelectQuery);
                resultSet = taskInfoSelectPrepare.executeQuery();
                while (resultSet.next()) {
                    serverId = resultSet.getInt("ServerId");
                    taskId = resultSet.getString("TaskId");
                }
                RidgeBotStopTaskPostInputBean inputJsonObj = new RidgeBotStopTaskPostInputBean();
                inputJsonObj.setTask_id(taskId);
                RegisteredServer rs = getRegisteredServerService().getServerById(serverId,getUser());
                RidgeBotRestClient restClient = getRestClientUtil();
                if (null != rs && null != restClient) {
                    OrionURI uri = rs.getURI();
                    Map<String, String> rsParams = uri.getParams();
                    String apiKey = rsParams.get("apiKey");
                    String restURL = rsParams.get("restURL");
                    restClient.setApiKey(apiKey);
                    restClient.setRestURL(restURL + stopTaskPath);
                    String taskInfoJson = restClient.sendPostRequest(inputJsonObj);
                    JsonParser jsonParse = new JsonParser();
                    JsonObject taskJasonObject = jsonParse.parse(taskInfoJson).getAsJsonObject();
                    String message = taskJasonObject.get("msg").getAsString();
                    LOGG.debug("RidgeBot :: Stop task response ==" + message);
                    if (null != taskJasonObject && "success".equals(message)) {
                        LOGG.debug("RidgeBot: Stop Task Completed in RidgeBot server for Task Ids ====" + inputJsonObj.getTask_id());
                        taskInfoUpdatePrepare = connection.prepareStatement(taskInfoUpdateQuery);
                        taskInfoUpdatePrepare.executeUpdate();
                        connection.commit();
                        LOGG.debug("RidgeBot: Stop Task Completed in ePO server ====");
                        str = getResource().formatString("RidgeBot.Task.Stop.PostAction", getLocale(), new Object[0]);
                    } else {
                        str = getResource().formatString("RidgeBot.Task.Stop.Error", getLocale(), new Object[0]);
                        resultCategory = ResultCategory.NO_CHANGE;
                        messageType = MessageType.ERROR;
                    }
                }
            } catch (SQLException sqle) {
                LOGG.debug("RidgeBot :: Error Occurred while Stopping task Info : stopTask() " + sqle);
                str = getResource().formatString("RidgeBot.Task.Stop.Error", getLocale(), new Object[0]);
                resultCategory = ResultCategory.NO_CHANGE;
                messageType = MessageType.ERROR;
                throw sqle;
            } catch (Exception ex) {
                LOGG.debug("RidgeBot :: Error Occurred while Stopping task Info : stopTask() " + ex);
                str = getResource().formatString("RidgeBot.Task.Stop.Error", getLocale(), new Object[0]);
                resultCategory = ResultCategory.NO_CHANGE;
                messageType = MessageType.ERROR;
            } finally {
                IOUtil.close(resultSet);
                IOUtil.close(taskInfoUpdatePrepare);
                IOUtil.close(taskInfoSelectPrepare);
            }
        }
        return new AsyncUIActionMessageResponse(str, messageType, resultCategory);
    }

    /**
     * This method will handle start task UI action on the custom tab.
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public AsyncUIActionMessageResponse startTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uids = request.getParameter("UIDs");
        ResultCategory resultCategory = ResultCategory.DELETE_OCCURRED;
        MessageType messageType = MessageType.PLAIN;
        String str = "";
        String taskStatus = "RUNNING";
        String taskStatusInePO ="";
        LOGG.debug("RidgeBot :: Start Task uid string  ==" + uids);
        if (uids != null) {
            int autoId = Integer.parseInt(uids);
            String taskId = "";
            int serverId = 0;
            PreparedStatement taskInfoSelectPrepare = null;
            String taskInfoSelectQuery = "SELECT * FROM " + TASK_INFO_TABLE + " where AutoId=" + autoId;
            String taskInfoUpdateQuery = "UPDATE " + TASK_INFO_TABLE + " set TaskStatus='" + taskStatus + "' where AutoId=" + autoId;
            PreparedStatement taskInfoUpdatePrepare = null;
            ResultSet resultSet = null;
            try {
                taskInfoSelectPrepare = connection.prepareStatement(taskInfoSelectQuery);
                resultSet = taskInfoSelectPrepare.executeQuery();
                while (resultSet.next()) {
                    serverId = resultSet.getInt("ServerId");
                    taskId = resultSet.getString("TaskId");
                    taskStatusInePO = resultSet.getString("TaskStatus");
                }
                if(! "CANCEL".equalsIgnoreCase(taskStatusInePO))
                {
                    RidgeBotStopTaskPostInputBean inputJsonObj = new RidgeBotStopTaskPostInputBean();
                    inputJsonObj.setTask_id(taskId);
                    RegisteredServer rs = getRegisteredServerService().getServerById(serverId,getUser());
                    RidgeBotRestClient restClient = getRestClientUtil();
                    if (null != rs && null != restClient) {
                        OrionURI uri = rs.getURI();
                        Map<String, String> rsParams = uri.getParams();
                        String apiKey = rsParams.get("apiKey");
                        String restURL = rsParams.get("restURL");
                        restClient.setApiKey(apiKey);
                        restClient.setRestURL(restURL + startTaskPath);
                        String taskInfoJson = restClient.sendPostRequest(inputJsonObj);
                        JsonParser jsonParse = new JsonParser();
                        JsonObject taskJasonObject = jsonParse.parse(taskInfoJson).getAsJsonObject();
                        String message = taskJasonObject.get("msg").getAsString();
                        LOGG.debug("RidgeBot :: Start task response ==" + message);
                        if (null != taskJasonObject && "success".equals(message)) {
                            LOGG.debug("RidgeBot: Start Task Completed in RidgeBot server for Task Ids ====" + inputJsonObj.getTask_id());
                            taskInfoUpdatePrepare = connection.prepareStatement(taskInfoUpdateQuery);
                            taskInfoUpdatePrepare.executeUpdate();
                            connection.commit();
                            LOGG.debug("RidgeBot: Start Task Completed in ePO server ====");
                            str = getResource().formatString("RidgeBot.Task.Start.PostAction", getLocale(), new Object[0]);
                        } else {
                            str = getResource().formatString("RidgeBot.Task.Start.Error", getLocale(), new Object[0]);
                            resultCategory = ResultCategory.NO_CHANGE;
                            messageType = MessageType.ERROR;
                            LOGG.debug("RidgeBot: Start Task failed in ePO server ====");
                        }
                    }
                }
                else
                {
                    str = getResource().formatString("RidgeBot.Task.Start.Error", getLocale(), new Object[0]);
                    resultCategory = ResultCategory.NO_CHANGE;
                    messageType = MessageType.ERROR;
                    LOGG.debug("RidgeBot: Start Task failed because the task status is in CANCEL state ====");
                }

            } catch (SQLException sqle) {
                LOGG.debug("RidgeBot :: Error Occurred while Stopping task Info : startTask() " + sqle);
                str = getResource().formatString("RidgeBot.Task.Start.Error", getLocale(), new Object[0]);
                resultCategory = ResultCategory.NO_CHANGE;
                messageType = MessageType.ERROR;
                throw sqle;
            } catch (Exception ex) {
                LOGG.debug("RidgeBot :: Error Occurred while Stopping task Info : startTask() " + ex);
                str = getResource().formatString("RidgeBot.Task.Start.Error", getLocale(), new Object[0]);
                resultCategory = ResultCategory.NO_CHANGE;
                messageType = MessageType.ERROR;
            } finally {
                IOUtil.close(resultSet);
                IOUtil.close(taskInfoSelectPrepare);
                IOUtil.close(taskInfoUpdatePrepare);
            }
        }
        return new AsyncUIActionMessageResponse(str, messageType, resultCategory);
    }

    /**
     * This method will handle restart task UI action on the custom tab.
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public AsyncUIActionMessageResponse reStartTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uids = request.getParameter("UIDs");
        ResultCategory resultCategory = ResultCategory.DELETE_OCCURRED;
        MessageType messageType = MessageType.PLAIN;
        String str = "";
        String taskStatus = "RUNNING";
        LOGG.debug("RidgeBot :: ReStart Task uid string  ==" + uids);
        if (uids != null) {
            int autoId = Integer.parseInt(uids);
            String taskId = "";
            int serverId = 0;
            PreparedStatement taskInfoSelectPrepare = null;
            String taskInfoSelectQuery = "SELECT * FROM " + TASK_INFO_TABLE + " where AutoId=" + autoId;
            String taskInfoUpdateQuery = "UPDATE " + TASK_INFO_TABLE + " set TaskStatus='" + taskStatus + "' where AutoId=" + autoId;
            PreparedStatement taskInfoUpdatePrepare = null;
            ResultSet resultSet = null;
            JsonParser jsonParse = new JsonParser();
            try {
                taskInfoSelectPrepare = connection.prepareStatement(taskInfoSelectQuery);
                resultSet = taskInfoSelectPrepare.executeQuery();
                while (resultSet.next()) {
                    serverId = resultSet.getInt("ServerId");
                    taskId = resultSet.getString("TaskId");
                }
                RidgeBotStopTaskPostInputBean inputJsonObj = new RidgeBotStopTaskPostInputBean();
                inputJsonObj.setTask_id(taskId);
                RegisteredServer rs = getRegisteredServerService().getServerById(serverId,getUser());
                RidgeBotRestClient restClient = getRestClientUtil();
                if (null != rs && null != restClient) {
                    OrionURI uri = rs.getURI();
                    Map<String, String> rsParams = uri.getParams();
                    String apiKey = rsParams.get("apiKey");
                    String restURL = rsParams.get("restURL");
                    restClient.setApiKey(apiKey);
                    restClient.setRestURL(restURL + reStartTaskPath);
                    String taskInfoJson = restClient.sendPostRequest(inputJsonObj);
                    JsonObject taskJasonObject = jsonParse.parse(taskInfoJson).getAsJsonObject();
                    String message = taskJasonObject.get("msg").getAsString();
                    LOGG.debug("RidgeBot :: ReStart task response ==" + message);
                    if (null != taskJasonObject && "success".equals(message)) {
                        LOGG.debug("RidgeBot: ReStart Task Completed in RidgeBot server for Task Ids ====" + inputJsonObj.getTask_id());
                        taskInfoUpdatePrepare = connection.prepareStatement(taskInfoUpdateQuery);
                        taskInfoUpdatePrepare.executeUpdate();
                        connection.commit();
                        LOGG.debug("RidgeBot: ReStart Task Completed in ePO server ====");
                        str = getResource().formatString("RidgeBot.Task.ReStart.PostAction", getLocale(), new Object[0]);
                    } else {
                        str = getResource().formatString("RidgeBot.Task.ReStart.Error", getLocale(), new Object[0]);
                        resultCategory = ResultCategory.NO_CHANGE;
                        messageType = MessageType.ERROR;
                    }
                }

            } catch (SQLException sqle) {
                LOGG.debug("RidgeBot :: Error Occurred while Restart task Info : restartTask() " + sqle);
                str = getResource().formatString("RidgeBot.Task.ReStart.Error", getLocale(), new Object[0]);
                resultCategory = ResultCategory.NO_CHANGE;
                messageType = MessageType.ERROR;
                throw sqle;
            } catch (Exception ex) {
                LOGG.debug("RidgeBot :: Error Occurred while Restart task Info : restartTask() " + ex);
                str = getResource().formatString("RidgeBot.Task.ReStart.Error", getLocale(), new Object[0]);
                resultCategory = ResultCategory.NO_CHANGE;
                messageType = MessageType.ERROR;
            } finally {
                IOUtil.close(resultSet);
                IOUtil.close(taskInfoSelectPrepare);
                IOUtil.close(taskInfoUpdatePrepare);
                jsonParse = null;
            }
        }
        return new AsyncUIActionMessageResponse(str, messageType, resultCategory);
    }

    /**
     * This method will handle pause task UI action on the custom tab.
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public AsyncUIActionMessageResponse pauseTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uids = request.getParameter("UIDs");
        ResultCategory resultCategory = ResultCategory.DELETE_OCCURRED;
        MessageType messageType = MessageType.PLAIN;
        String str = "";
        String taskStatus = "PAUSE";
        LOGG.debug("RidgeBot :: Pause Task uid string  ==" + uids);
        if (uids != null) {
            int autoId = Integer.parseInt(uids);
            String taskId = "";
            int serverId = 0;
            PreparedStatement taskInfoSelectPrepare = null;
            String taskInfoSelectQuery = "SELECT * FROM " + TASK_INFO_TABLE + " where AutoId=" + autoId;
            String taskInfoUpdateQuery = "UPDATE " + TASK_INFO_TABLE + " set TaskStatus='" + taskStatus + "' where AutoId=" + autoId;
            PreparedStatement taskInfoUpdatePrepare = null;
            ResultSet resultSet = null;
            try {
                taskInfoSelectPrepare = connection.prepareStatement(taskInfoSelectQuery);
                resultSet = taskInfoSelectPrepare.executeQuery();
                while (resultSet.next()) {
                    serverId = resultSet.getInt("ServerId");
                    taskId = resultSet.getString("TaskId");
                }
                RidgeBotStopTaskPostInputBean inputJsonObj = new RidgeBotStopTaskPostInputBean();
                inputJsonObj.setTask_id(taskId);
                RegisteredServer rs = getRegisteredServerService().getServerById(serverId,getUser());
                RidgeBotRestClient restClient = getRestClientUtil();
                if (null != rs && null != restClient) {
                    OrionURI uri = rs.getURI();
                    Map<String, String> rsParams = uri.getParams();
                    String apiKey = rsParams.get("apiKey");
                    String restURL = rsParams.get("restURL");
                    restClient.setApiKey(apiKey);
                    restClient.setRestURL(restURL + pauseTaskPath);
                    String taskInfoJson = restClient.sendPostRequest(inputJsonObj);
                    JsonParser jsonParse = new JsonParser();
                    JsonObject taskJasonObject = jsonParse.parse(taskInfoJson).getAsJsonObject();
                    String message = taskJasonObject.get("msg").getAsString();
                    LOGG.debug("RidgeBot :: Pause task response ==" + message);
                    if (null != taskJasonObject && "success".equals(message)) {
                        //taskDeletionStatus = StringEscapeUtils.escapeHtml(this.resource.getString("RidgeBot.deletion.success", locale));
                        LOGG.debug("RidgeBot: Pause Task Completed in RidgeBot server for Task Ids ====" + inputJsonObj.getTask_id());
                        taskInfoUpdatePrepare = connection.prepareStatement(taskInfoUpdateQuery);
                        taskInfoUpdatePrepare.executeUpdate();
                        connection.commit();
                        // taskDeletionStatus = StringEscapeUtils.escapeHtml(this.resource.getString("RidgeBot.deletion.success", locale));
                        LOGG.debug("RidgeBot: Pause Task Completed in ePO server ====");
                        str = getResource().formatString("RidgeBot.Task.Pause.PostAction", getLocale(), new Object[0]);
                    } else {
                        str = getResource().formatString("RidgeBot.Task.Pause.Error", getLocale(), new Object[0]);
                        resultCategory = ResultCategory.NO_CHANGE;
                        messageType = MessageType.ERROR;
                    }
                }

            } catch (SQLException sqle) {
                LOGG.debug("RidgeBot :: Error Occurred while Pause task Info : pauseTask() " + sqle);
                str = getResource().formatString("RidgeBot.Task.Pause.Error", getLocale(), new Object[0]);
                resultCategory = ResultCategory.NO_CHANGE;
                messageType = MessageType.ERROR;
                throw sqle;
            } catch (Exception ex) {
                LOGG.debug("RidgeBot :: Error Occurred while Pause task Info : pauseTask() " + ex);
                str = getResource().formatString("RidgeBot.Task.Pause.Error", getLocale(), new Object[0]);
                resultCategory = ResultCategory.NO_CHANGE;
                messageType = MessageType.ERROR;
            } finally {
                IOUtil.close(resultSet);
                IOUtil.close(taskInfoSelectPrepare);
                IOUtil.close(taskInfoUpdatePrepare);
            }
        }
        return new AsyncUIActionMessageResponse(str, messageType, resultCategory);
    }

    /**
     * This method will render the detailed information for the given RidgeBot task object
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionResponse showTaskDetails(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uid = request.getParameter("uid");
        if (uid != null) {
            PreparedStatement taskInfoSelectPrepare = null;
            ResultSet resultSet = null;
            String taskId = "";
            int serverId = 0;
            try {
                RidgeBotTaskStatisticsMapper ridgeMapper = new RidgeBotTaskStatisticsMapper();
                int u_id = Integer.parseInt(uid);
                String taskInfoSelectQuery = "SELECT * FROM " + TASK_INFO_TABLE + " where AutoId=" + u_id;
                taskInfoSelectPrepare = connection.prepareStatement(taskInfoSelectQuery);
                resultSet = taskInfoSelectPrepare.executeQuery();
                while (resultSet.next()) {
                    serverId = resultSet.getInt("ServerId");
                    taskId = resultSet.getString("TaskId");
                }
                List<RidgeBotTaskStatisticsMapper> detailList = ridgeMapper.getWhere(connection, "ServerId =" + serverId + " AND TaskId='" + taskId + "'");
                request.setAttribute("detailList", detailList.get(0));
                request.setAttribute("adapter", displayer);
            } catch (Exception ex) {
                LOGG.debug("RidgeBot :: Error occurred while getting task details for the given task in showTaskDetails() " + ex);
                LOGG.error(ex.getMessage(), ex);
            } finally {
                IOUtil.close(taskInfoSelectPrepare);
                IOUtil.close(resultSet);
            }
        }

        return ActionResponse.forward("/S_RIDGBTMETA", "/RidgeBotTaskDetailedReport.jsp");
    }

    public RidgeBotRestClient getRestClientUtil() {
        return restClientUtil;
    }

    public void setRestClientUtil(RidgeBotRestClient restClientUtil) {
        this.restClientUtil = restClientUtil;
    }


    public OrionUser getUser() {
        return user;
    }


    public void setUser(OrionUser user) {
        this.user = user;
    }


    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(@Null Connection connection) {
        this.connection = connection;
    }

    public QueryListDataSource getRegisteredServerDataSource() {
        return registeredServerDataSource;
    }

    public void setRegisteredServerDataSource(QueryTableSearchableDS registeredServerDataSource) {
        this.registeredServerDataSource = registeredServerDataSource;
    }

    public QueryBuilderService getQueryBuilderService() {
        return queryBuilderService;
    }

    public void setQueryBuilderService(QueryBuilderService queryBuilderService) {
        this.queryBuilderService = queryBuilderService;
    }

    public TableService getTableService() {
        return tableService;
    }

    public void setTableService(TableService tableService) {
        this.tableService = tableService;
    }

    public QueryListDataSource getRidgeBotTaskDataSource() {
        return ridgeBotTaskDataSource;
    }

    public void setRidgeBotTaskDataSource(QueryListDataSource ridgeBotTaskDataSource) {
        this.ridgeBotTaskDataSource = ridgeBotTaskDataSource;
    }

    public RidgeBotRegisteredServerInstance getRegisteredServer() {
        return registeredServer;
    }

    public void setRegisteredServer(RidgeBotRegisteredServerInstance registeredServer) {
        this.registeredServer = registeredServer;
    }

    @Override
    public MultipartFormPolicy getMultipartFormPolicy() {
        MultipartFormPolicy policy = new MultipartFormPolicy();
        long mb = 1024L * 1024L;
        Integer byteValue = Integer.valueOf(serverConfiguration.getFileUploadLimit() * (int) mb);
        policy.setMaxUploadSize(byteValue);
        return policy;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setDisplayer(DisplayAdapter displayer) {
        this.displayer = displayer;
    }

    public DatabaseMapper<RidgeBotTaskDetailsMapper> getDataBaseMapper() {
        return dataBaseMapper;
    }

    public void setDataBaseMapper(DatabaseMapper<RidgeBotTaskDetailsMapper> dataBaseMapper) {
        this.dataBaseMapper = dataBaseMapper;
    }

    public RegisteredServerService getRegisteredServerService() {
        return registeredServerService;
    }

    public void setRegisteredServerService(RegisteredServerService registeredServerService) {
        this.registeredServerService = registeredServerService;
    }

    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    public void setServerConfiguration(ServerConfiguration serverConfiguration) {
        this.serverConfiguration = serverConfiguration;
    }

    public OrionUploadFileItem getFileItem() {
        return fileItem;
    }

    public void setFileItem(OrionUploadFileItem fileItem) {
        this.fileItem = fileItem;
        //setFileContent(fileItem.get());
    }

    private void setFileContent(byte[] ext) {
        byteFileContent = ext;
    }

    public Object getUploadFile() {
        return byteFileContent;
    }

    public void setUploadFile(Object fileContents) {
        if (null == fileContents) {
            throw new IllegalArgumentException("file cannot be null");
        }
        if (fileContents instanceof OrionUploadFileItem) {
            setFileItem((OrionUploadFileItem) fileContents);
        } else if (fileContents instanceof byte[]) {
            setFileContent((byte[]) fileContents);
        } else {
            throw new IllegalArgumentException("unsupported format" + fileContents.getClass().getName());
        }
    }
}

