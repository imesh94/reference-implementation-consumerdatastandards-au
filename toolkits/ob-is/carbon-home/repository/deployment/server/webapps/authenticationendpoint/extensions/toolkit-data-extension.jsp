<%--
 ~ Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
 ~
 ~ WSO2 LLC. licenses this file to you under the Apache License,
 ~ Version 2.0 (the "License"); you may not use this file except
 ~ in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied. See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 --%>
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
