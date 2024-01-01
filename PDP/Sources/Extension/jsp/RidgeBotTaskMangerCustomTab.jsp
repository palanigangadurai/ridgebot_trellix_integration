<%@ taglib uri="http://www.mcafee.com/orion" prefix="orion" %>

<orion:core>
    <orion:corehead/>
    <orion:corebody>
        <orion:navigation tabId="RidgeBotServiceManager.Registered.Tab.new"/>
        <orion:content>
            <orion:message key="RegisteredServer.TableTitle" var="tableTitle"/>
            <orion:vbox>
                <orion:tabletitle id="RegisteredServerTableTitle" tableId="RidgeBotTaskInformationTable"
                  text="${tableTitle}"/>
                <orion:table2 datasourceAttr="RidgeBotTaskDataSource"
                      id="RidgeBotTaskInformationTable"
                      allowAction="true"
                      allowAdvancedFilter="false"
                      allowConfiguration="true"
                      allowFilter="true"
                      allowSelectInvert="true"
                      showCheckboxColumn="true"
                      enableDrilldown="true"
                      refresh="true"
                      registeredTypeUID="RidgeBotTaskUID"
                      allowExport="false" showTitleBar="false"/>
            </orion:vbox>
        </orion:content>
    </orion:corebody>
</orion:core>