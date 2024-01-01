<%@ taglib uri="http://www.mcafee.com/orion" prefix="orion" %>

<input type="hidden" name="disallowInput" id="disallowInput" value="${disallowInput}"/>

<table id="ServerList" width="100%">
    <orion:message key="RidgeBot.Select.Serve" var="ridgeBotMessage"/>
    <tr>
        <td class="orionSummaryHeader">${ridgeBotMessage}</td>
        <td class="orionSummaryColumn"><orion:select name="serverName" id="serverId" items="${serverNameList}"
                                                     disabled="${disallowInput}"/></td>
    </tr>
</table>

<script type="text/javascript">

    <%--
    *This method will we be called by EPO server when the edit request comes from "Actions" tab of "Server Builder" page to set the request param value in to "data" array object .
    *
    * @param container is the DOM object for this HTML page
    * @param data is the JavaScript Array in the format:data["key"] = value, which can be used to set the input elements upon loading the snippet
    --%>
    var disallow = document.getElementById("disallowInput");
    _setData = function(container, data)
    {
        if (disallow.value == "true")
        {
            OrionWizard.setNextButtonEnabled(false);
        }
        else
        {
            OrionWizard.setNextButtonEnabled(true);
            var field = OrionCore.getChildNodeById(container, "serverId");

            if (data["serverId"] == null)
            {
                field.value = "";
            }
            else
            {
                field.value = data["serverId"];
            }
        }

    };
    <%--
     *This method will we be called by EPO server when the first request(i,e creating new task) comes from "Actions" tab of "Server Builder" page to get the values from container object and set those value in to array object.
     *
     * @param container is the DOM object for the HTML page come as part of the request
     --%>
    _getData = function(container)
    {
        if (disallow.value == "true")
        {
            OrionWizard.setNextButtonEnabled(false);

        }
        else {
            OrionWizard.setNextButtonEnabled(false);
            var data = new Array();
            var field = OrionCore.getChildNodeById(container, "serverId");
            data["serverId"] = field.value;
            data["serverName"] = field[field.selectedIndex].innerHTML;
        }

        return data;    //data is the JavaScript Array in the format:data["key"] = value, this can contain the values of parameters comes as part of the request.
    };
    <%--
    * This method determine whether or not the data entered is valid
    *@return true enables the "Next" button, return false will disable the "Next" button
    --%>
    _allowSubmit = function()
    {
        if (disallow.value == "true")
        {
            OrionWizard.setNextButtonEnabled(false);
            return false;
        }
        else
        {
            OrionWizard.setNextButtonEnabled(true);
            return true; //This method should return true to enable the "Next" button on "Actions" page
        }
    };
</script>
