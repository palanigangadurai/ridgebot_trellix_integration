package com.ridgebot.ext.model;

import com.mcafee.orion.core.auth.UserAware;
import com.mcafee.orion.core.data.DataSourceFilter;
import com.mcafee.orion.core.data.Filterable;
import com.mcafee.orion.core.data.QuickSearchable;
import com.mcafee.orion.core.query.QueryBuilder;
import com.mcafee.orion.core.query.QueryListDataSource;
import com.mcafee.orion.core.query.sexp.*;
import com.mcafee.orion.core.query.sexp.ops.SexpContains;
import com.mcafee.orion.core.query.sexp.ops.SexpOr;
import com.mcafee.orion.core.query.sexp.ops.SexpSelect;
import com.mcafee.orion.core.sexp.SexpUtil;
import com.mcafee.orion.core.util.resource.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * This class is used to create table2 object in custom tab UI for RidgeBot extension
 */
public class RidgeBotQueryListDataSource extends QueryListDataSource implements Filterable, QuickSearchable, UserAware {
    private static Logger LOGG = Logger.getLogger(RidgeBotQueryListDataSource.class);
    private String ridgebot_searchString;
    private Resource resource;
    private String tableName = "";

    public RidgeBotQueryListDataSource(String squidTableName) throws SexpException {
        super(createBuilder(squidTableName));
        this.tableName = squidTableName;
    }

    /**
     * This method provides the QueryBuilder object for the table specified in method param.
     *
     * @param tableName
     * @return QueryBuilder object
     */
    private static QueryBuilder createBuilder(String tableName) {

        QueryBuilder builder = new QueryBuilder();
        builder.setUseUID(true);
        try {
            builder.setTarget(tableName);
            builder.setTargetName(tableName);
            // set the selection to select all the available columns
            SexpSelect select = new SexpSelect();
            List<ColumnInfo> columns;
            columns = builder.getDetailsColumnInfo();
            // we will select everything from the Target
            if (columns != null)
                for (ColumnInfo targetColumnInfo : columns) {
                    //only add the column if it is selectable
                    if (targetColumnInfo.isSelectable()) {
                        String fullName = targetColumnInfo.getFullName();
                        select.addChild(new SexpProp(fullName));
                    }
                }

            builder.setSelection(select);
            return builder;
        } catch (SexpException e) {
            LOGG.debug("RodgeBot:: Error occurred while creating Query Builder=" + e);
            LOGG.error(e.getMessage(), e);
        }
        return builder;
    }

    public List<DataSourceFilter> getFilters() {
        return null;
    }

    public DataSourceFilter getFilter() {
        return null;
    }

    public void setFilter(String filterId) {
    }

    /**
     * Sets Search string and also Checks for filter condition and sets them to the queryBuilder object
     */
    private synchronized void checkFilter() {
        Sexp quickSearch = getRidgebotSearchSexp();
        getQueryBuilder().setCondition(SexpUtil.and(null, quickSearch));
    }

    /**
     * This method constructs the sexp for quick search string entered by the user
     *
     * @return Sexp object
     */
    private Sexp getRidgebotSearchSexp() {
        Sexp filter = null;
        if (!StringUtils.isEmpty(ridgebot_searchString)) {
            filter = new SexpOr();
            SexpContains expContains = null;
            String[] properties = {
                    "RidgeBotTaskInfo.TaskName",
                    "RidgeBotTaskInfo.ServerName",
                    "RidgeBotTaskInfo.TaskTargets",
                    "RidgeBotTaskInfo.TaskStatus",
                    "RidgeBotTaskInfo.TaskType"
            };
            for (String prop : properties) {
                expContains = new SexpContains();
                expContains.addChildren(new SexpProp(prop),
                        new SexpString(ridgebot_searchString));
                filter.children().add(expContains);
            }
        }
        return filter;
    }

    public String getSearchString() {
        return ridgebot_searchString;
    }

    public void setSearchString(String search) {
        ridgebot_searchString = search;
        checkFilter();
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
