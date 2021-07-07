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
<%@ page import="org.wso2.carbon.identity.mgt.endpoint.util.IdentityManagementEndpointUtil" %>
<%@ page import="org.wso2.carbon.identity.application.authentication.endpoint.util.Constants" %>
<%@ page import="org.wso2.carbon.identity.authenticator.smsotp.SMSOTPConstants" %>
<%@ page import="org.wso2.carbon.identity.authenticator.smsotp.SMSOTPUtils" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.Map" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes/localize.jsp" %>

<%
    request.getSession().invalidate();
    String queryString = request.getQueryString();
    Map<String, String> idpAuthenticatorMapping = null;
    if (request.getAttribute(Constants.IDP_AUTHENTICATOR_MAP) != null) {
        idpAuthenticatorMapping = (Map<String, String>) request.getAttribute(Constants.IDP_AUTHENTICATOR_MAP);
    }

    String errorMessage = IdentityManagementEndpointUtil.i18n(recoveryResourceBundle,"error.retry");
    String authenticationFailed = "false";

    //Get SMSOTP properties from application-authentication.xml
    int tokenTimeoutSeconds = 0;
    Map<String, String> SMSOTPParamMap =  SMSOTPUtils.getSMSParameters();
    if (SMSOTPParamMap.containsKey(SMSOTPConstants.TOKEN_EXPIRY_TIME)) {
        tokenTimeoutSeconds = Integer.valueOf(SMSOTPParamMap.get(SMSOTPConstants.TOKEN_EXPIRY_TIME));
    }

    if (Boolean.parseBoolean(request.getParameter(Constants.AUTH_FAILURE))) {
        authenticationFailed = "true";
        tokenTimeoutSeconds = 0;

        if (request.getParameter(Constants.AUTH_FAILURE_MSG) != null) {
            errorMessage = request.getParameter(Constants.AUTH_FAILURE_MSG);

            if (errorMessage.equalsIgnoreCase("authentication.fail.message")) {
                errorMessage = IdentityManagementEndpointUtil.i18n(recoveryResourceBundle,"error.retry");
            }
            if (errorMessage.equalsIgnoreCase(SMSOTPConstants.TOKEN_EXPIRED_VALUE)) {
                errorMessage = IdentityManagementEndpointUtil.i18n(recoveryResourceBundle,"error.code.expired.resend");
            }
        }
    }
%>

<html>
    <head>
        <!-- header -->
        <%
            File headerFile = new File(getServletContext().getRealPath("extensions/header.jsp"));
            if (headerFile.exists()) {
        %>
        <jsp:include page="extensions/header.jsp"/>
        <% } else { %>
        <jsp:include page="includes/header.jsp"/>
        <% } %>
        <!--[if lt IE 9]>
        <script src="js/html5shiv.min.js"></script>
        <script src="js/respond.min.js"></script>
        <![endif]-->
    </head>

    <body class="login-portal layout sms-otp-portal-layout">
        <main class="center-segment">
            <div class="ui container medium center aligned middle aligned">
                <!-- product-title -->
                <%
                    File productTitleFile = new File(getServletContext().getRealPath("extensions/product-title.jsp"));
                    if (productTitleFile.exists()) {
                %>
                <jsp:include page="extensions/product-title.jsp"/>
                <% } else { %>
                <jsp:include page="includes/product-title.jsp"/>
                <% } %>

                <div class="ui segment">
                    <!-- page content -->
                    <h2><%=IdentityManagementEndpointUtil.i18n(recoveryResourceBundle, "auth.with.smsotp")%></h2>
                    <div class="ui divider hidden"></div>
                    <%
                        if ("true".equals(authenticationFailed)) {
                    %>
                            <div class="ui negative message" id="failed-msg">
                                <%=Encode.forHtmlContent(errorMessage)%>
                            </div>
                            <div class="ui divider hidden"></div>
                    <% } %>
                    <div class="error-msg"></div>
                    <div class="segment-form">
                        <form class="ui large form" id="pin_form" name="pin_form" action="../../commonauth"  method="POST">
                            <%
                                String loginFailed = request.getParameter("authFailure");
                                if (loginFailed != null && "true".equals(loginFailed)) {
                                    String authFailureMsg = request.getParameter("authFailureMsg");
                                    if (authFailureMsg != null && "login.fail.message".equals(authFailureMsg)) {
                            %>
                                <div class="ui visible negative message">
                                    <%=IdentityManagementEndpointUtil.i18n(recoveryResourceBundle, "error.retry")%>
                                </div>
                                <div class="ui divider hidden"></div>
                            <% } }  %>
                            <!-- Token Pin -->
                            <% if (request.getParameter("screenvalue") != null) { %>
                            <div class="field">
                                <label for="password">
                                    <%=IdentityManagementEndpointUtil.i18n(recoveryResourceBundle, "enter.code.sent")%><%=Encode.forHtmlContent(request.getParameter("screenvalue"))%>
                                </label>
                                <input type="password" id='OTPcode' name="OTPcode"
                                        size='30'/>
                            <% } else { %>
                            <div class="field">
                                <label for="password"><%=IdentityManagementEndpointUtil.i18n(recoveryResourceBundle, "enter.code.sent")%></label>
                                <input type="password" id='OTPcode' name="OTPcode"
                                size='30'/>
                            <% } %>
                            </div>
                            <%if (tokenTimeoutSeconds > 0) {%>
                                <div class="otp-timer" id=otpTimeout></div>
                            <%}%>
                            <input type="hidden" name="sessionDataKey"
                            value='<%=Encode.forHtmlAttribute(request.getParameter("sessionDataKey"))%>'/><br/>
                            <div class="align-right buttons">
                                <%
                                    if ("true".equals(authenticationFailed)) {
                                        String reSendCode = request.getParameter("resendCode");
                                        if ("true".equals(reSendCode)) {
                                %>
                                    <div id="resendCodeLinkDiv" class="ui button link-button">
                                        <a id="resend"><%=IdentityManagementEndpointUtil.i18n(recoveryResourceBundle, "resend.code")%></a>
                                    </div>
                                <% } } %>
                                <input
                                    type="button" name="authenticate" id="authenticate"
                                    value="<%=IdentityManagementEndpointUtil.i18n(recoveryResourceBundle, "authenticate")%>" class="ui primary button"/>
                            </div>
                            <input type='hidden' name='resendCode' id='resendCode' value='false'/>
                            <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 form-group">
                               <hr class="separator">
                               <%
				  File disclaimerFile = new File(getServletContext().getRealPath("extensions/toolkit-disclaimer.jsp"));
				  if (disclaimerFile.exists()) {
			       %>
			            <jsp:include page="extensions/toolkit-disclaimer.jsp"/>
			      <% } %>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>

        <!-- product-footer -->
        <%
            File productFooterFile = new File(getServletContext().getRealPath("extensions/product-footer.jsp"));
            if (productFooterFile.exists()) {
        %>
        <jsp:include page="extensions/product-footer.jsp"/>
        <% } else { %>
        <jsp:include page="includes/product-footer.jsp"/>
        <% } %>

        <!-- footer -->
        <%
            File footerFile = new File(getServletContext().getRealPath("extensions/footer.jsp"));
            if (footerFile.exists()) {
        %>
        <jsp:include page="extensions/footer.jsp"/>
        <% } else { %>
        <jsp:include page="includes/footer.jsp"/>
        <% } %>

        <script type="text/javascript">
        $(document).ready(function() {
            $('#authenticate').click(function() {
                if ($('#pin_form').data("submitted") === true) {
                    console.warn("Prevented a possible double submit event");
                } else {
                    var OTPcode = document.getElementById("OTPcode").value;
                    if (OTPcode == "") {
                        document.getElementById('alertDiv').innerHTML
                            = '<div id="error-msg" class="ui negative message"><%=IdentityManagementEndpointUtil.i18n(recoveryResourceBundle, "please.enter.code")%></div><div class="ui divider hidden"></div>';
                    } else {
                        $('#pin_form').data("submitted", true);
                        $('#pin_form').submit();
                    }
                }
            });
        });
        $(document).ready(function() {
            $('#resendCodeLinkDiv').click(function() {
                document.getElementById("resendCode").value = "true";
                $('#pin_form').submit();
            });
        });
        $(document).ready(function() {
            var timeoutHandle;
            function countdown(totalSeconds) {
              var minutes = Math.floor(totalSeconds/60);
              var seconds = totalSeconds % 60;
              function tick() {
                var counter = document.getElementById("otpTimeout");
                if (minutes == 0 && seconds == 0) {
                    counter.innerHTML = "The OTP is expired";
		    counter.style.color="#ff5e5e";
                } else {
                  counter.innerHTML = "The OTP will expire in " +
                    minutes.toString() + ":" + (seconds < 10 ? "0" : "") + String(seconds);
                    if (minutes == 0 && seconds <= 10 ) {
			counter.style.color="orange";
	            }
                }
                seconds--;
                if (seconds >= 0) {
                  timeoutHandle = setTimeout(tick, 1000);
                } else {
                  if (minutes >= 1) {
                    setTimeout(function () {
                      countdown((minutes - 1) * 60 + 59);
                    }, 1000);
                  }
                }
              }
              tick();
            }
            countdown(<%=tokenTimeoutSeconds%>);
        });
        </script>
    </body>
</html>
