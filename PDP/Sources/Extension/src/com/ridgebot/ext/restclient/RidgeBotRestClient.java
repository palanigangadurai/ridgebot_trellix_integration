package com.ridgebot.ext.restclient;
/**
 * Created by Admin
 */

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mcafee.epo.core.services.EPOProxyService;
import com.mcafee.orion.core.auth.UserLoader;
import com.mcafee.orion.core.db.base.Database;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Connection;

import static org.springframework.http.HttpMethod.GET;


public class RidgeBotRestClient {
    private static final Logger LOGG = Logger.getLogger(RidgeBotRestClient.class);
    private String path;
    private String apiKey;
    private String restParams;
    private String restURL;
    private EPOProxyService epoProxyService;
    private UserLoader userLoader;
    private Database database;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getRestParams() {
        return restParams;
    }

    public void setRestParams(String restParams) {
        this.restParams = restParams;
    }

    public String getRestURL() {
        return restURL;
    }

    public void setRestURL(String restURL) {
        this.restURL = restURL;
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders() {{
            set("Authorization", apiKey);
            set("accept", "application/json");
        }};
        return headers;
    }

    public int testConnection() throws Exception {

        LOGG.debug("RidgeBot: RSApiKeyRestClient testConnection is called");
        ResponseEntity<String> response;
        Connection connection = getDatabase().getConnection(getUserLoader().getDefaultTenantSystemUser());
        RestTemplate restTemplate = RidgeBotServerRestClientUtil.getRegisteredServerRestTemplate(epoProxyService, connection);
        String url = getRestURL();
        HttpHeaders headers = createHttpHeaders();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        int statusCode = -1;
        try {
            LOGG.debug("RidgeBot: RestAPIClient , before rest execute  :: ");
            response = restTemplate.exchange(builder.buildAndExpand().toUri(), GET, new HttpEntity<Object>(headers), String.class);
            LOGG.debug("RidgeBot: RestAPIClient , after rest execute  :: " + response);
            LOGG.debug("RidgeBot: RestAPIClient , Before send response  :: " + response);
            String responseBody = response.getBody();
            JsonParser jparse = new JsonParser();
            JsonObject jObj = jparse.parse(responseBody).getAsJsonObject();
            String message = jObj.get("msg").getAsString();
            LOGG.debug("RidgeBot : RestAPIClient : testConnection , message ==" + message);
            if ("success".equals(message)) {
                statusCode = response.getStatusCode().value();
            }
        } catch (HttpClientErrorException h) {
            LOGG.debug("RidgeBot: RestAPIClient Error occurred while testing the connection" + h);
            throw new HttpClientErrorException(h.getStatusCode());
        } catch (ResourceAccessException e) {
            LOGG.debug("RidgeBot: RestAPIClient Error occurred while testing the connection" + e);
            throw new ResourceAccessException(e.getMessage());
        } catch (Exception ex) {
            throw new ResourceAccessException(ex.getMessage());
        }

        return statusCode;
    }


    public String getInfoFromAPI() throws Exception {
        String responseString = null;
        String path;
        if (getRestParams() != null || !(getRestParams().equals(""))) {
            path = "?" + getRestParams() + "&output=json";
        } else {
            path = "&output=json";
        }
        responseString = sendGetRequest(path);
        return responseString;
    }


    public String postInfoFromAPI() throws Exception {
        String responseString;
        String path;
        if (getRestParams() != null || !(getRestParams().equals(""))) {
            path = getRestParams();
        } else {
            path = "";
        }

        responseString = sendPostRequest(path);
        return responseString;
    }

    public String sendGetRequest(String path) throws Exception {
        HttpHeaders headers = createHttpHeaders();
        String responseString = null;
        RestTemplate restTemplate = RidgeBotServerRestClientUtil.getRegisteredServerRestTemplate(epoProxyService, getDatabase().getConnection(getUserLoader().getDefaultTenantSystemUser()));
        String url = getRestURL() + path;
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);

        //TODO : Handle Null condition for response
        ResponseEntity<String> response = restTemplate.exchange(builder.buildAndExpand().toUri(), GET, new HttpEntity<Object>(headers), String.class);
        if (null != response) {
            responseString = (response.getBody()).toString();
        } else {
            if (response.getStatusCode().equals("200")) {
                throw new Exception(" RidgeBot Registered Server: Task Failed. Check if the REST API URL or REST params are proper");
            } else {
                LOGG.debug("RidgeBot Registered Server : Invalid response received : " + response.getStatusCode() + " . Response received : " + response);
                throw new Exception("RidgeBot Registered Server: Task Failed. Invalid response received:" + response.getStatusCode());
            }
        }
        return responseString;
    }

    public String sendPostRequest(Object path) throws Exception {
        RestTemplate restTemplate = RidgeBotServerRestClientUtil.getRegisteredServerRestTemplate(epoProxyService, getDatabase().getConnection(getUserLoader().getDefaultTenantSystemUser()));
        HttpHeaders headers = createHttpHeaders();
        String responseCode = null;
        HttpEntity<Object> request = new HttpEntity<Object>(path, headers);
        String url = getRestURL();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        //TODO : Handle Null condition for response
        ResponseEntity<String> response = restTemplate.postForEntity(builder.buildAndExpand().toUri(), request, String.class);
        if (null != response) {
            responseCode = String.valueOf(response.getBody());
        } else {
            LOGG.debug("RidgeBot Registered Server: Invalid response received : " + response.getStatusCode() + " . Response received : " + response);
            throw new Exception("RidgeBot Registered Server: Task Failed. Invalid response received:" + response.getStatusCode());
        }

        return responseCode;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }


    public UserLoader getUserLoader() {
        return userLoader;
    }

    public void setUserLoader(UserLoader userLoader) {
        this.userLoader = userLoader;
    }

    public EPOProxyService getEpoProxyService() {
        return epoProxyService;
    }

    public void setEpoProxyService(EPOProxyService epoProxyService) {
        this.epoProxyService = epoProxyService;
    }
}

