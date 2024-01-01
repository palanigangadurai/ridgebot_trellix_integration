package com.ridgebot.ext.scheduler;

import com.mcafee.orion.core.auth.OrionUser;
import com.mcafee.orion.core.db.ConnectionBean;
import com.mcafee.orion.core.servlet.ActionResponse;
import com.mcafee.orion.core.servlet.Response;
import com.mcafee.orion.core.servlet.util.UserUtil;
import com.mcafee.orion.rs.servers.RegisteredServer;
import com.mcafee.orion.rs.servers.RegisteredServerService;
import com.ridgebot.ext.server.RidgeBotRegisteredServerInstance;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**F
 * This scheduler class will manage the tasks of RidgeBot to periodically pull the task details from RidgeBot server
 */
public class RidgeBotServerTaskScheduler implements ConnectionBean {

    private static final Logger LOGG = Logger.getLogger(RidgeBotServerTaskScheduler.class);
    RidgeBotRegisteredServerInstance registeredServer = null;
    private Connection connection = null;
    private RegisteredServerService registeredServerService = null;

    public RidgeBotRegisteredServerInstance getRegisteredServer() {
        return registeredServer;
    }

    public void setRegisteredServer(RidgeBotRegisteredServerInstance registeredServer) {
        this.registeredServer = registeredServer;
    }

    /**
     * This method will be invoked to handle the "scheduleServerTaskCommand.do" action.
     *
     * @param request  - HttpServletRequest object
     * @param response - HttpServletResponse object
     * @return - Returns the Response object, which forwards the incoming request to "RidgeBotSchedulableServerTaskCommand.jsp" page.
     * @throws Exception
     */

    public Response scheduleServerTaskCommand(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List registeredServerList = null;
        List<RegisteredServer> serverNameList = new ArrayList<RegisteredServer>();
        String serverType = null;
        if (registeredServer != null) {
            LOGG.debug("RidgeBot : scheduleServerTaskCommand : registered server is not null");
            serverType = registeredServer.getName();
        }
        OrionUser user = UserUtil.getOrionUser(request);
        registeredServerList = getRegisteredServerService().getServersOfType(serverType,user);
        if (registeredServerList != null) {
            for (Object aRegisteredServerList : registeredServerList) {
                RegisteredServer regServer = (RegisteredServer) aRegisteredServerList;
                serverNameList.add(regServer);
            }
        }
        //SelectBean is used map the values for orion:select tag in the JSP page.
        RidgeBotSelectBeans beansForSelect = new RidgeBotSelectBeans(serverNameList, "id", "name");
        if (serverNameList.size() > 0) {
            request.setAttribute("disallowInput", false);
        } else {
            request.setAttribute("disallowInput", true);
        }
        LOGG.debug("RidgeBot : scheduleServerTaskCommand : server name list bean ==" + beansForSelect);
        request.setAttribute("serverNameList", beansForSelect);
        return ActionResponse.forward("/S_RIDGBTMETA", "/RidgeBotSchedulableServerTaskCommand.jsp");
    }

    /**
     * This method is used to handle the "schedulableServerTaskSummary.do" MVC action.
     *
     * @param paramHttpServletRequest  - HttpServletRequest object
     * @param paramHttpServletResponse - HttpServletResponse object
     * @return - Returns the Response object, which forwards the incoming request to "RidgeBotSchedulableServerTaskSummary.jsp" page.
     * @throws Exception
     */

    public Response schedulableServerTaskSummary(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse) throws Exception {

        return ActionResponse.forward("/S_RIDGBTMETA", "/RidgeBotSchedulableServerTaskSummary.jsp");

    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public RegisteredServerService getRegisteredServerService() {
        return registeredServerService;
    }

    public void setRegisteredServerService(RegisteredServerService registeredServerService) {
        this.registeredServerService = registeredServerService;
    }
}
