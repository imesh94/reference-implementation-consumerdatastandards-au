<%--
  ~ Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
  ~
  ~ This software is the property of WSO2 Inc. and its suppliers, if any.
  ~ Dissemination of any information or reproduction of any material contained
  ~ herein is strictly forbidden, unless permitted by WSO2 in accordance with
  ~ the WSO2 Software License available at https://wso2.com/licenses/eula/3.1.
  ~ For specific language governing the permissions and limitations under this
  ~ license, please see the license as well as any agreement you’ve entered into
  ~ with WSO2 governing the purchase of this software and any associated services.
  --%>
<%@ page import="org.owasp.encoder.Encode" %>
<%@ page import="com.wso2.openbanking.accelerator.identity.authenticator.OBIdentifierAuthenticator" %>
<%@ page import="org.json.JSONObject" %>
<%
OBIdentifierAuthenticator sessionDetails = new OBIdentifierAuthenticator();
String sessionDataKey = Encode.forHtmlAttribute(request.getParameter("sessionDataKey"));
String output = sessionDetails.getSessionData(sessionDataKey);

String spDetails = null;
String callbackURL = null;

if (output != null){
	JSONObject sessionDetailsJson = new JSONObject(output);
	String spOrgName = sessionDetails.getSPProperty(sessionDetailsJson.getString("client_id"), "org_name");
	String spClientName = sessionDetails.getSPProperty(sessionDetailsJson.getString("client_id"), "client_name");
	spDetails = spOrgName + "," + spClientName;
	callbackURL = sessionDetailsJson.getString("redirect_uri");
	request.setAttribute("spDetails",spDetails);
	request.setAttribute("callbackURL",callbackURL);
}
%>
