<!--
~ Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
~
~ This software is the property of WSO2 LLC. and its suppliers, if any.
~ Dissemination of any information or reproduction of any material contained
~ herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
~ You may not alter or remove any copyright or other notice from copies of this content.
~
-->
<%@ page import="org.owasp.encoder.Encode" %>
<%@ page import="com.wso2.openbanking.accelerator.identity.authenticator.OBIdentifierAuthenticator" %>
<%@ page import="org.json.JSONObject" %>
<%
OBIdentifierAuthenticator sessionDetails = new OBIdentifierAuthenticator();
String clientId = Encode.forHtmlAttribute(request.getParameter("client_id"));
String requestUri = Encode.forHtmlAttribute(request.getParameter("request_uri"));
String state = Encode.forHtmlAttribute(request.getParameter("state"));

String spDetails = null;
String callbackURL = null;

String spOrgName = sessionDetails.getSPProperty(clientId, "org_name");
String spClientName = sessionDetails.getSPProperty(clientId, "client_name");
spDetails = spOrgName + "," + spClientName;
callbackURL = sessionDetails.getRedirectUri(requestUri);
request.setAttribute("spDetails",spDetails);
request.setAttribute("callbackURL",callbackURL);
request.setAttribute("state", state);
%>
