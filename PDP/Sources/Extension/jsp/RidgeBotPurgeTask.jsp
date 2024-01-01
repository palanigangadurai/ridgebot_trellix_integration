<%@ taglib uri="http://www.mcafee.com/orion" prefix="orion" %>

<orion:core>
    <orion:corehead/>
    <orion:corebody >
        <orion:navigation tabId="RidgeBotServiceManager.Registered.Tab.new" />
        <orion:message key="RidgeBot.PurgeTask.Table.Title" var="purgeTaskTableText"/>
        <orion:message key="RidgeBot.UI.Purge" var="purgeText"/>
        <orion:message key="RidgeBot.UI.Back" var="backText"/>
        <orion:message key="RidgeBot.UI.Close" var="closeText"/>
        <orion:content>
            <input type="hidden" name="disallowInput" id="disallowInput" value="${disallowInput}"/>
            <orion:vbox width="200">
                <orion:box height="auto">
                    <orion:tabletitle id="purgeTaskTitle" text="${purgeTaskTableText}"/>
                    <table id="RegisteredServer" width="100%" cellpadding="0" cellspacing="0">
                         <orion:message key="RidgeBot Server List" var="ridgeBotMessage"/>
                         <tr>
                             <td class="orionSummaryHeader">${ridgeBotMessage}</td>
                             <td class="orionSummaryColumn"><orion:select name="serverName" id="serverId" items="${serverNameList}"
                                                                          disabled="${disallowInput}" required="true"/>
                             <span id="purgeResponse"/>
                             </td>
                         </tr>
                    </table>
                </orion:box>
                <orion:footerbar>
                    <orion:button id="back" text="${backText}" onclick="OrionNavigation.selectTab('ThirdParty','RidgeBotServiceManager.Registered.Tab.new' )"/>
                    <orion:button name="purge" id="purge" text="${purgeText}" onclick="purgeTask(this)"/>
                    <orion:button id="Close" text="${closeText}" onclick="OrionNavigation.navigate('/S_RIDGBTMETA/showPartnerData.do');"/>
                </orion:footerbar>
            </orion:vbox>
    </orion:content>
    </orion:corebody>
</orion:core>

<script type="text/javascript">

 function validateSpace(ele) {
    ele.value = ele.value.replace(/\s/g, "");
    return true;
 }

 function purgeTask(element)
 {
    var field = document.getElementById('serverId');
    var serverId = field.value;
    var serverName = field[field.selectedIndex].innerHTML;
    OrionCore.showPleaseWait(true);
    OrionCore.doAsyncAction("/S_RIDGBTMETA/purgeTaskAction.do", ["serverId",serverId,"serverName",serverName], _showDetails, null);
 }

 function _showDetails(data)
 {
     if(null != data)
     {
         if(data.includes("Success") || data.includes("success"))
         {
             $("purgeResponse").className = "";
             $("purgeResponse").innerHTML = data;
         }
         else
         {
             $("purgeResponse").className = "errorText";
             $("purgeResponse").innerHTML = data;
         }
      }
      OrionForm.revalidate();
      OrionCore.showPleaseWait(false);
  }

function initialize() {
    OrionForm.setStateChangeHandler(onFormStateChange);
    OrionForm.setValidateHandler(onFormStateChange);
    OrionForm.revalidate();
 }

function onFormStateChange(isDirty, isValid) {
    var purgeButton = $("purge");
    if($("disallowInput").value)
        OrionCore.setEnabled(purgeButton, true);
    else
        OrionCore.setEnabled(purgeButton, false);
}
OrionCore.addLoadHandler(initialize);
</script>