<%@ taglib uri="http://www.mcafee.com/orion" prefix="orion" %>

<orion:message key="ServerTaskCommand.Message" var="serverTaskPartnerDataInfoMessage"/>

${serverTaskPartnerDataInfoMessage} ${commandSummaryMap['serverName']}
<br/> <%-- this part of code will be embedded on "summary" tab of "Server Task Builder" page--%>