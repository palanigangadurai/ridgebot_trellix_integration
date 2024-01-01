<%@ taglib uri="http://www.mcafee.com/orion" prefix="orion" %>

<orion:core>
    <orion:corehead/>
    <orion:corebody >
        <orion:navigation tabId="RidgeBotServiceManager.Registered.Tab.new" />
        <orion:message key="RidgeBot.CreateTask.Table.Title" var="createTaskTableText"/>
        <orion:message key="RidgeBot.UI.Create" var="createText"/>
        <orion:message key="RidgeBot.UI.Back" var="backText"/>
        <orion:message key="RidgeBot.UI.Close" var="closeText"/>
        <orion:message key="RidgeBot.UI.EnterIPs" var="enterIpsText"/>
        <orion:content>
            <input type="hidden" name="disallowInput" id="disallowInput" value="${disallowInput}"/>
            <orion:vbox width="200">
                <orion:box height="auto">
                    <orion:tabletitle id="createTaskTitle" text="${createTaskTableText}"/>
                    <table id="RegisteredServer" width="100%" cellpadding="0" cellspacing="0">
                         <tr>
                              <td class="orionSummaryHeader">
                               <orion:message key="Task Name"/></td>
                              <td class="orionSummaryColumn">
                               <orion:textbox name="taskName" id="taskName" required="true" value="" onkeyup="validateState(this);" onchange="validateState(this);"/>
                               <span id="taskName_error"/></td>
                         </tr>
                         <tr>
                              <td class="orionSummaryHeader">
                               <orion:message key="Description"/>
                              </td>
                              <td class="orionSummaryColumn">
                               <orion:textbox name="description" required="true" id="description" value="" onkeyup="validateState(this);" onchange="validateState(this);"/></td>
                         </tr>
                              <orion:message key="RidgeBot Server List" var="ridgeBotMessage"/>
                         <tr>
                             <td class="orionSummaryHeader">${ridgeBotMessage}</td>
                             <td class="orionSummaryColumn"><orion:select name="serverName" id="serverId" items="${serverNameList}"
                                                                          disabled="${disallowInput}" required="true"/></td>
                         </tr>
                        <tr>
                            <td class="orionSummaryHeader">
                                <orion:message key="Test Target IP Address"/>
                            </td>
                            <td class="orionSummaryColumn">
                                <div >
                                    <orion:textarea name="ipAddress" rows="4" cols="100" id="ipAddress" required="true" value="" onkeyup="validateSpace(this); validateIPAddress(this);" placeholder="${enterIpsText}"/>
                                    <span id="createResponse"/>
                                </div>
                                <br/>
                                <label id="uploadMessage"><orion:message key="RidgeBot.UI.FileUploadMessage"/></label>
                                <orion:fileupload id="fileItem" name="fileItem" width="100"  size="50" onchange="importFileContent(this);" onclick="resetFileStatus(this);"/>
                          </td>
                         </tr>
                    </table>
                </orion:box>
                <orion:footerbar>
                    <orion:button id="back" text="${backText}" onclick="OrionNavigation.selectTab('ThirdParty','RidgeBotServiceManager.Registered.Tab.new')"/>
                    <orion:button name="create" id="create" text="${createText}" onclick="createNewTask(this)"/>
                    <orion:button id="close" text="${closeText}" onclick="OrionNavigation.navigate('/S_RIDGBTMETA/showPartnerData.do');"/>
                </orion:footerbar>
            </orion:vbox>
    </orion:content>
    </orion:corebody>
</orion:core>

<script type="text/javascript">
 var isValidIPValue = "no";
 function validateSpace(ele) {
    ele.value = ele.value.replace(/\s/g, "");
    return true;
 }

 function createNewTask(element)
 {
    var tasName = document.getElementById('taskName').value;
    var description = document.getElementById('description').value;
    var field = document.getElementById('serverId');
    var serverId = field.value;
    var ipAddress = document.getElementById('ipAddress').value;
    var serverName = field[field.selectedIndex].innerHTML;
    OrionCore.showPleaseWait(true);
    OrionCore.doAsyncAction("/S_RIDGBTMETA/createNewTaskAction.do", ["taskName", tasName,"description",description,"serverId",serverId,"serverName",serverName,"ipAddress",ipAddress], _showDetails, null);
 }

 function importFileContent(element){
     OrionCore.showPleaseWait(true);
     OrionForm.setEncoding(OrionForm.MULTIPART);
     var fileName = $("fileItem").value;
     var fileNameArray = fileName.split(".");
     if(fileNameArray[1] == "txt")
     {
        OrionCore.doAsyncFormAction("/S_RIDGBTMETA/importFileContent.do", [], _showImportDetails, null);
     }
     else
     {
        _showImportDetails("File import failed : invalid file format");
     }
}

 function resetFileStatus(element)
 {
    $("fileItem").value = "";
 }

 function _showImportDetails(data)
 {
    $("createResponse").className = "";
    $("createResponse").innerHTML = "";
    if(null != data)
        {
            if(data.includes("failed"))
            {
                $("createResponse").className = "errorText";
                $("createResponse").innerHTML = data;
                $("ipAddress").value = "";
            }
            else
            {
                $("ipAddress").value = data;
                validateIPAddress(data);
                $("createResponse").className = "";
                $("createResponse").innerHTML = "File Upload Success";
            }
         }
         validateState();
         OrionCore.showPleaseWait(false);
 }

 function _showDetails(data) {
    if(null != data)
    {
        if(data.includes("Task_Id"))
        {
            const responseArray = data.split(":");
            $("createResponse").className = "";
            $("createResponse").innerHTML = responseArray[0];
        }
        else
        {
            $("createResponse").className = "errorText";
            $("createResponse").innerHTML = data;
        }
     }
     validateState();
     OrionCore.showPleaseWait(false);
 }

 function validateIPAddress(ipaddres) {
    //if (/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(ipaddress)) {
    var createButton = $("create");
    var ipAddressVal =  document.getElementById('ipAddress').value;
    var ipArray = ipAddressVal.split(",");
    let arrayLength = ipArray.length;
    let isValidIp = false;
    if( null != ipArray && arrayLength<=0 )
    {
        isValidIp = OrionValidate.isValidIPv4String(ipAddressVal);
    }
    else
    {
        for (let index = 0; index < ipArray.length; index++) {
            isValidIp = OrionValidate.isValidIPv4String(ipArray[index]);
        }
    }
    if(isValidIp)
    {
        isValidIPValue = "yes";
        validateState();
        return true;
    }
    else
    {
        isValidIPValue = "no";
        validateState();
        return false;
    }
}

function validateState(data)
{
    var tasName = document.getElementById('taskName').value;
    var description = document.getElementById('description').value;
    var ipAddressVal =  document.getElementById('ipAddress').value;
    var createButton = $("create");
    if(null != tasName && tasName != "" && null != ipAddressVal && ipAddressVal != "" && null != description && description != "" && isValidIPValue === "yes")
    {
        OrionCore.setEnabled(createButton, true);
    }
    else
    {
        OrionCore.setEnabled(createButton, false);
    }
    OrionForm.revalidate();
}
 function initialize() {
    OrionForm.setStateChangeHandler(onFormStateChange);
    OrionForm.setValidateHandler(onFormStateChange);
    OrionForm.revalidate();
 }

function onFormStateChange(isDirty, isValid) {
    var createButton = $("create");
    if (isDirty && isValid && isValidIPValue === "yes")
        OrionCore.setEnabled(createButton, true);
    else
        OrionCore.setEnabled(createButton, false);
}
OrionCore.addLoadHandler(initialize);
</script>