<%@ taglib uri="http://www.mcafee.com/orion" prefix="orion" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<orion:message key="RegisteredServer.TestConnection" var="registeredServerTestConnection"/>

<script>
 function validateSpace(ele) {
  ele.value = ele.value.replace(/\s/g, "");
  enableOrDisableTestButton();
  return true;
 }

 function validateRestUrl() {
  var restUrlTextbox = $("restURL");
  restUrlTextbox.value = restUrlTextbox.value.replace(/\s/g,"");
  var restUrl = restUrlTextbox.value;
  var testConnectionButton = $("testconnection");
  $("testResults").className = "";
  $("testResults").innerHTML = "";
  if(restUrl.match("https://.*") || restUrl.match("http://.*"))
  {
    if(restUrl.endsWith("/api/v4") )
    {
        $("restURL_error_1").innerHTML = "";
        $("restURL_error_1").className = "";
        $("restURL_error_1").style.display = "none";
        if($("apiKey").value != "")
        {
            OrionCore.setEnabled(testConnectionButton, true);
            return true;
        }
        else{
            OrionCore.setEnabled(testConnectionButton, false);
            return false;
        }
    }
    else
    {
        $("restURL_error_1").innerHTML = "Invalid URL";
        $("restURL_error_1").className = "errorText";
        $("restURL_error_1").style.display = "inline";
        OrionCore.setEnabled(testConnectionButton, false);
        return false;
    }
  }
  else
  {
    if (restUrl !="")
    {
        $("restURL_error_1").innerHTML = "Invalid URL";
        $("restURL_error_1").className = "errorText";
        $("restURL_error_1").style.display = "inline";
        OrionCore.setEnabled(testConnectionButton, false);
    }
   return false;
  }
 }
</script>


<table id="RegisteredServer" width="100%" cellpadding="0" cellspacing="0">
 <tr>
  <td class="orionSummaryHeader">
   <orion:message key="RegisteredServer.Rest.Url"/></td>

  <td class="orionSummaryColumn">
   <orion:textbox name="restURL" id="restURL" required="true" value="${restURL}" onchange="validateRestUrl();"/>
   <label id="restURLLabel"><orion:message key="restURLLabel.Text"/></label>
   <span id="restURL_error_1"/></td>
 </tr>

 <tr>
  <td class="orionSummaryHeader">
   <orion:message key="RegisteredServer.apiKey"/></td>
  <td class="orionSummaryColumn">
   <orion:textarea name="apiKey" rows="4" cols="100" id="apiKey" required="true" value="${apiKey}" onkeyup="validateSpace(this);"/></td>
 </tr>

 <tr>
  <td class="orionSummaryHeader">
   <orion:message key="RegisteredServer.TestConnection"/>
  </td>

  <td class="orionSummaryColumn">
   <orion:button name="testconnection" id="testconnection" size="15" text="${registeredServerTestConnection}"
                 onclick="testConnection();"/>
   <span id="testResults"/></td>
 </tr>

</table>
<script type="text/javascript">

 function testConnection() {
  OrionCore.showPleaseWait(true);
  OrionCore.doAsyncFormAction("/S_RIDGBTMETA/testConnection.do", [], _testDBComplete);
 }
 function _testDBComplete(responseData) {
  var data;
  var note;
  var testConnectionButton = $("testconnection");
  if (responseData.indexOf(",") != -1) {
   data = responseData.split(",")[0];
   note = responseData.split(",")[1];
  } else {
   data = responseData;
  }

  if (data == "Test Connection is Successful") {
   $("testResults").className = "";
   $("testResults").innerHTML = data;
   OrionCore.setEnabled(testConnectionButton, true);
  } else if (data == "Successful") {
   $("testResults").className = "";
   $("testResults").innerHTML = data;
   OrionCore.setEnabled(testConnectionButton, true);
  } else {
   $("testResults").className = "errorText";
   $("testResults").innerHTML = data;
   OrionCore.setEnabled(testConnectionButton, false);
  }
  OrionCore.showPleaseWait(false);
 }
 function enableOrDisableTestButton()
 {
    var status = validateRestUrl();
    var testConnectionButton = $("testconnection");
    if(status && $("apiKey").value != "")
    {
        OrionCore.setEnabled(testConnectionButton, true);
    }
    else{
        OrionCore.setEnabled(testConnectionButton, false);
    }
 }
 function initialize() {
  OrionForm.setStateChangeHandler(onFormStateChange);
  OrionForm.setValidateHandler(onFormStateChange);
  OrionForm.revalidate();
  $("testResults").className = "";
  $("testResults").innerHTML = "";
  if ($("restURL").value != "" && $("apiKey").value != "") {
    enableOrDisableTestButton();
  }
  else
  {
    enableOrDisableTestButton();
  }
 }
 function onFormStateChange(isDirty, isValid) {
  var areConnectionFieldsValid = true;
  var testConnectionButton = $("testconnection");
  $("testResults").className = "";
  $("testResults").innerHTML = "";
  if (isValid)
   OrionCore.setEnabled(testConnectionButton, true);
  else
   OrionCore.setEnabled(testConnectionButton, false);
 }
 OrionCore.addLoadHandler(initialize);
</script>