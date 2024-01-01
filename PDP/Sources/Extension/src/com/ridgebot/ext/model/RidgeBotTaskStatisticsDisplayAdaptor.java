package com.ridgebot.ext.model;

import com.mcafee.orion.core.ui.DisplayAdapterImpl;
import com.mcafee.orion.core.ui.DisplayContext;
import com.mcafee.orion.core.util.resource.Resource;

/**
 * This class is used to create display adapter for RidgeBot task information that are displayed in
 * table2 object on the RidgeBot custom tab UI.
 */
public class RidgeBotTaskStatisticsDisplayAdaptor extends DisplayAdapterImpl {

    private static final String[] PROPERTIES =
            {"securitySafetyIndex", "vulNumber", "vulInfo", "vulTypeNumber", "vulHigh", "vulMedium", "vulLow", "securityHttpsRatio", "securityVulTransform", "securityAttackSurfaceDensity", "securityRiskTransform", "securityAliveRatio", "securityVulTypeCoverageRatio", "taskExploitCount", "taskDigCount", "taskScanCount", "riskCredentialsNum", "riskPenetrationNum", "riskInfiltratedAssetsNum", "riskExploitNum", "riskCodeNum", "riskNumber", "riskShellNum", "riskDatabaseNum", "chainSensitiveAttackAverage", "chainVulAttackAverage", "attackIpAliveNum", "attackIpAsstesNum", "attackIpUnaliveNum", "attackSiteAsstesNum", "attackSiteAliveNum", "attackSiteUnaliveNum", "attackAsstesNum", "attackSurfaceNum"};

    public RidgeBotTaskStatisticsDisplayAdaptor(String name, Resource resource) {
        super(name, resource);
    }

    public String[] getPropertyNames() {
        return PROPERTIES;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String formatPropertyValue(String propName, Object propValue, DisplayContext displayContext) {

        return super.formatPropertyValue(propName, propValue, displayContext);
    }
}