package com.ridgebot.ext.server;

/**
 * Created by admin
 */


import com.mcafee.orion.core.auth.OrionUser;
import com.mcafee.orion.core.auth.UserAware;
import com.mcafee.orion.core.cert.Obfuscator;
import com.mcafee.orion.core.servlet.ActionResponse;
import com.mcafee.orion.core.servlet.Response;
import com.mcafee.orion.core.servlet.util.UserUtil;
import com.mcafee.orion.core.ui.WizardAction;
import com.mcafee.orion.core.ui.WizardInfo;
import com.mcafee.orion.core.util.ioc.ContextInjectible;
import com.mcafee.orion.core.util.resource.LocaleAware;
import com.mcafee.orion.core.util.resource.Resource;
import com.mcafee.orion.rs.servers.RegisteredServer;
import com.mcafee.orion.rs.shared.ServerRegistry;
import com.ridgebot.ext.bean.RidgeBotRegisteredServerTypeItem;
import com.ridgebot.ext.restclient.RidgeBotRestClient;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This class will manage all the actions for RidgeBot Registered server UI.
 */

public class RidgeBotRegisteredServerAction extends WizardAction implements ContextInjectible, LocaleAware, UserAware {
    private static final Logger LOGG = Logger.getLogger(RidgeBotRegisteredServerAction.class);
    private static String fakePassword = "____fake___password__";
    private RegisteredServer registeredServer = null;
    private Resource resource = null;
    private Locale locale = null;
    private WizardInfo myWizardInfo;
    private ServerRegistry serverRegistry;
    private Obfuscator obfuscator = null;
    private RidgeBotRestClient restClientUtil = null;
    private String testConnSubPath = "/test/connect";

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Obfuscator getObfuscator() {
        return obfuscator;
    }

    public void setObfuscator(Obfuscator obfuscator) {
        this.obfuscator = obfuscator;
    }

    private RegisteredServer getRegisteredServer(HttpServletRequest request) {
        OrionUser user = UserUtil.getOrionUser(request);
        return (RegisteredServer) user.getAttribute("registeredServerWizardState");
    }

    /**
     * This method id used to handle the "testDBConnection.do" MVC action. This method id used to test whether the given configuration parameters of Registered Server is valid or not.
     *
     * @param paramHttpServletRequest  - HttpServletRequest object
     * @param paramHttpServletResponse - HttpServletResponse object.
     * @return - Returns the success message if the given parameters of Registered Server is correct otherwise the failure message will be returned to the user.
     * @throws Exception
     */
    public Response testConnection(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
            throws Exception {
        try {
            String restMethod = "GET";
            String apiKey = StringEscapeUtils.escapeHtml(getRequestParams(paramHttpServletRequest).get("apiKey"));
            String restURL = StringEscapeUtils.escapeHtml(getRequestParams(paramHttpServletRequest).get("restURL"));
            getRequestParams(paramHttpServletRequest);
            RidgeBotRestClient rsApiKeyRestClient = getRestClientUtil();
            rsApiKeyRestClient.setApiKey(apiKey);
            rsApiKeyRestClient.setRestURL(restURL + testConnSubPath);
            int respCode = rsApiKeyRestClient.testConnection();
            LOGG.debug("RideBot: Test Connection Response code  = " + respCode);
            if (respCode == 200) {
                paramHttpServletResponse.getWriter().write(StringEscapeUtils.escapeHtml(this.resource.getString("RegisteredServer.auth.success", locale)));

            } else if (respCode == 401) {
                paramHttpServletResponse.getWriter().write(StringEscapeUtils.escapeHtml(this.resource.formatString("RegisteredServer.auth.failed", locale)));

            } else {
                paramHttpServletResponse.getWriter().write(StringEscapeUtils.escapeHtml(this.resource.formatString("RegisteredServer.connect.failed", locale)));
            }

        } catch (Exception localException) {
            LOGG.debug("RidgeBot : Error occurred while testing the connection" + localException);
            paramHttpServletResponse.getWriter().write(StringEscapeUtils.escapeHtml(this.resource.formatString("RegisteredServer.connect.failed", locale, new Object[]{localException.getMessage()})));
        }
        paramHttpServletResponse.getWriter().flush();
        return ActionResponse.none();
    }

    public void setRegisteredServer(RegisteredServer registeredServer) {
        this.registeredServer = registeredServer;
    }


    /**
     * This method is used to handle "loadServer.do" MVC action.
     * This method will get the already stored information of Registered Server from ePO DB to GUI while editing.
     *
     * @param request  - HttpServletRequest
     * @param response - HttpServletResponse
     * @return - Returns the Response object, which forwards the incoming request to "RegisteredServerBasicAuthConfig.jsp" page
     * to display the Registered Server information.
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public Response loadServer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //retrieves the "registeredServerWizardState" from the request
        RegisteredServer server = getRegisteredServer(request);
        Map<String, String> params = null;

        //Check the status of the server wizard
        if (server == null) {
            throw new ServletException(resource.getString("RegisteredServer.error.text", locale));
        } else if (server != null) {
            //get the parameters from the requested URI
            params = server.getURI().getParams();
        }
        request.setAttribute("restURL", StringEscapeUtils.escapeHtml(params.get("restURL")));
        request.setAttribute("apiKey", StringEscapeUtils.escapeHtml(params.get("apiKey")));
        return ActionResponse.forward("/S_RIDGBTMETA", "/RidgeBotServerConfig.jsp");
    }

    /**
     * This method is used to handle the "saveServer.do" MVC action, this method is used to save the configuration information of Registered server into ePO database.
     *
     * @param request  - HttpServletRequest object
     * @param response - HttpServletResponse object
     * @return - Returns Response object, which forwards the incoming request to next display page of Registered server configuration wizard.
     * @throws Exception
     */
    public Response saveServer(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RegisteredServer localRegisteredServer = getRegisteredServer(request);
        Map<String, String> params = null;
        if (localRegisteredServer == null) {
            throw new Exception(resource.getString("RegisteredServer.error.text", locale));
        } else {
            params = getRequestParams(request);
        }
        localRegisteredServer.getURI().setParams(params);
        return nextDisplayPage(request);
    }

    /**
     * This method returns the configuration information of Registered server as a Map object
     *
     * @param paramHttpServletRequest - HttpServeltRequest object.
     * @return - Return the Map object, which contains the Registered Server configuration information.
     * @throws Exception
     */
    public Map<String, String> getRequestParams(HttpServletRequest paramHttpServletRequest)
            throws Exception {
        RegisteredServer localServer = getRegisteredServer(paramHttpServletRequest);
        Map <String,String>localHashMap = new HashMap<String,String>();
        String apiKey = StringEscapeUtils.escapeHtml(paramHttpServletRequest.getParameter("apiKey"));
        String restURL = StringEscapeUtils.escapeHtml(paramHttpServletRequest.getParameter("restURL"));
        localHashMap.put("restURL", restURL);
        localHashMap.put("apiKey", apiKey);
        return localHashMap;
    }

    public ServerRegistry getServerRegistry() {
        return serverRegistry;
    }

    public void setServerRegistry(ServerRegistry serverRegistry) {
        this.serverRegistry = serverRegistry;
    }

    @Override
    public WizardInfo getWizardInfo() {
        return getMyWizardInfo();
    }

    public WizardInfo getMyWizardInfo() {
        return myWizardInfo;
    }

    public void setMyWizardInfo(WizardInfo myWizardInfo) {
        this.myWizardInfo = myWizardInfo;  //injecting this object(rz.wizard) from beans.xml file
    }

    private ArrayList getRestMethodOptionList() {
        ArrayList<RidgeBotRegisteredServerTypeItem> list = new ArrayList();
        RidgeBotRegisteredServerTypeItem registeredServerTypeItem1 = new RidgeBotRegisteredServerTypeItem();
        registeredServerTypeItem1.setName("GET");
        registeredServerTypeItem1.setId("GET");
        RidgeBotRegisteredServerTypeItem registeredServerTypeItem2 = new RidgeBotRegisteredServerTypeItem();
        registeredServerTypeItem2.setName("POST");
        registeredServerTypeItem2.setId("POST");
        list.add(registeredServerTypeItem1);
        list.add(registeredServerTypeItem2);
        return list;
    }

    public RidgeBotRestClient getRestClientUtil() {
        return restClientUtil;
    }

    public void setRestClientUtil(RidgeBotRestClient restClientUtil) {
        this.restClientUtil = restClientUtil;
    }


}
