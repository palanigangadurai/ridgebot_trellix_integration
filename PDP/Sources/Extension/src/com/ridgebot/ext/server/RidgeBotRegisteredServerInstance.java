package com.ridgebot.ext.server;

/**
 * Created by admin
 */

import com.mcafee.orion.core.cert.Obfuscator;
import com.mcafee.orion.core.db.base.Database;
import com.mcafee.orion.core.util.resource.LocaleAware;
import com.mcafee.orion.core.util.resource.Resource;
import com.mcafee.orion.rs.shared.Categorized;
import com.mcafee.orion.rs.shared.ServerType;

import java.util.Locale;
import java.util.Map;

public class RidgeBotRegisteredServerInstance implements ServerType, Categorized, LocaleAware {
    private String name = null;
    private String displayName = null;
    private String displayUri = null;
    private String updateUri = null;
    private String context = null;
    private String categoryName = null;
    private Resource resource = null;
    private Obfuscator obfuscator = null;
    private Locale locale = null;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName(String s) {
        return getResource().getString(displayName, locale);
    }

    public String getDisplayName() {
        return getResource().getString(displayName, locale);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * This method is used to get the Database object for remotely registerd database using registered server (Partner Registered Server).
     *
     * @param registeredServerInfoMap - Which contains the information about the Registerd Server information i.e.REST URL, params etc..
     * @return - Return the Database object Registerd server(Partner Registered Server).
     * @throws Exception
     */

    //TODO : no need to implement this method.
    public Database getDatabase(Map<String, String> registeredServerInfoMap) throws Exception {
        return null;
    }

    public String getDisplayUri() {

        return displayUri;
    }

    public void setDisplayUri(String displayUri) {
        this.displayUri = displayUri;
    }

    public String getUpdateUri() {
        return updateUri;
    }

    public void setUpdateUri(String updateUri) {
        this.updateUri = updateUri;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Obfuscator getObfuscator() {
        return obfuscator;
    }

    public void setObfuscator(Obfuscator obfuscator) {
        this.obfuscator = obfuscator;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
