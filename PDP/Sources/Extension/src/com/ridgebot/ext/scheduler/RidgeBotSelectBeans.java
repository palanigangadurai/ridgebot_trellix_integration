package com.ridgebot.ext.scheduler;


import com.mcafee.orion.core.ui.tags.html.Option;
import com.mcafee.orion.core.ui.tags.html.SingleSelect;
import org.apache.commons.beanutils.BeanUtils;

import java.util.Collection;
import java.util.Iterator;

/**
 * This class is used to handle the select bean for RidgeBot Registered server instances.
 */
public class RidgeBotSelectBeans extends SingleSelect {
    public RidgeBotSelectBeans(Collection paramCollection, String paramString1, String paramString2) {
        for (Iterator localIterator = paramCollection.iterator(); localIterator.hasNext(); ) {
            Object localObject = localIterator.next();
            String str1 = getProperty(localObject, paramString1);
            String str2 = getProperty(localObject, paramString2);
            addOptionItem(new Option(str1, str2));
        }
    }

    private String getProperty(Object paramObject, String paramString) {
        try {
            return BeanUtils.getProperty(paramObject, paramString);
        } catch (Exception localException) {
            throw new RuntimeException("error getting value for property [" + paramString + "] : " + localException.getMessage(), localException);
        }
    }
}