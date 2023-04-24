<!--
~ Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
~
~ This software is the property of WSO2 LLC. and its suppliers, if any.
~ Dissemination of any information or reproduction of any material contained
~ herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
~ You may not alter or remove any copyright or other notice from copies of this content.
~
-->

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.OffsetDateTime" %>
<%@ page import="java.time.ZoneOffset" %>
<jsp:include page="includes/consent_top.jsp"/>

<%
    String sessionDataKeyConsent = request.getParameter("sessionDataKeyConsent");
    String isConsentAmendment = request.getParameter("isConsentAmendment");
    String isSharingDurationUpdated = request.getParameter("isSharingDurationUpdated");
    String accounts = request.getParameter("accountsArry[]");
    String accounNames = request.getParameter("accNames");
    String appName = request.getParameter("app");
    String spFullName = request.getParameter("spFullName");
    String consentId = request.getParameter("id");
    String userName = request.getParameter("user");
    String[] accountList = accounNames.split(":");
    String[] accountIdList = accounts.split(":");
    String consentExpiryDateTime = request.getParameter("consent-expiry-date");
    String consentExpiryDate = consentExpiryDateTime.split("T")[0];
    String accountMaskingEnabled = request.getParameter("accountMaskingEnabled");
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDateTime now = LocalDateTime.now();
    String currentDate = dtf.format(now);
    Map<String, List<String>> consentData = (Map<String, List<String>>) session.getAttribute("configParamsMap");
    Map<String, List<String>> newConsentData = (Map<String, List<String>>) session.getAttribute("newConfigParamsMap");
    session.setAttribute("configParamsMap", consentData);
    session.setAttribute("newConfigParamsMap", newConsentData);
    session.setAttribute("isConsentAmendment", isConsentAmendment);
    session.setAttribute("isSharingDurationUpdated", isSharingDurationUpdated);

    boolean isSharedWithinDay = true;
    if (!"Single use consent".equals(consentExpiryDateTime)) {
        OffsetDateTime expDate = OffsetDateTime.parse(consentExpiryDateTime);
        OffsetDateTime curDate = OffsetDateTime.now(ZoneOffset.UTC);
        if ((expDate.toEpochSecond() - curDate.toEpochSecond())/(3600*24.0) > 1) {
            isSharedWithinDay = false;
        }
    }
    session.setAttribute("isSharedWithinDay", isSharedWithinDay);
%>
<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
    <div class="clearfix"></div>
    <form action="${pageContext.request.contextPath}/oauth2_authz_confirm.do" method="post" id="oauth2_authz_confirm"
          name="oauth2_authz_confirm" class="form-horizontal">
        <div class="login-form">
            <div class="form-group ui form">
                <div class="col-md-12 ui box">
                    <h3 class="ui header"><strong><%=spFullName%>
                    </strong> requests account details on your account.</h3>

                    <div class="padding-top">
                        <h4 class="section-heading-5 ui subheading">
                            Accounts selected:
                        </h4>
                        <div class="padding-left">
                            <ul class="scopes-list padding">
                            <%
                                for (int i = 0; i < accountList.length; i++) {
                                %>
                                    <li>
                                        <strong><% out.println(accountList[i]); %></strong><br>
                                        <span class ="accountIdClass" id="<% out.println(accountIdList[i]);%>">
                                            <small><% out.println(accountIdList[i]);%></small>
                                        </span>
                                    </li><br>
                                <%
                                }
                            %>
                            </ul>
                        </div>
                    </div>

                    <h4 class="section-heading-5 ui subheading">Data requested:</h4>

                    <!--Display requested data-->
                    <c:forEach items="<%=consentData%>" var="record">
                        <div class="padding" style="border:1px solid #555;">
                            <button type="button" class="collapsible">${record.key}</button>
                            <div class="content">
                                <ul class="scopes-list padding">
                                    <c:forEach items="${record.value}" var="record_data">
                                        <li>${record_data}</li>
                                    </c:forEach>
                                    <c:if test="${(record.key eq 'Account name, type, and balance') ||
                                    (record.key eq 'Account balance and details') || (record.key eq 'Transaction details')
                                    || (record.key eq 'Direct debits and scheduled payments')}">
                                        <br>
                                        <p> This may include historical data that dates back to 1 January 2017. </p>
                                    </c:if>
                                </ul>
                            </div>
                        </div>
                        <br>
                    </c:forEach>
                    <!--Display newly added requested data (Upon consent Amendment)-->
                    <c:if test="${not empty newConfigParamsMap}">
                        <c:forEach items="<%=newConsentData%>" var="record">
                            <div class="padding" style="border:1px solid #555;">
                                <button type="button" class="collapsible">${record.key}
                                    <span style="border: 1px solid #1b2c8f;color:#1b2c8f;font-weight:bold;background-color:#f4f5fd">New</span>
                                </button>
                                <div class="content">
                                    <ul class="scopes-list padding">
                                        <c:forEach items="${record.value}" var="record_data">
                                            <li>${record_data}</li>
                                        </c:forEach>
                                    </ul>
                                    <c:if test="${(record.key eq 'Account name, type, and balance') ||
                                    (record.key eq 'Account balance and details') || (record.key eq 'Transaction details')
                                    || (record.key eq 'Direct debits and scheduled payments')}">
                                        <br>
                                        <p> This may include historical data that dates back to 1 January 2017. </p>
                                    </c:if>
                                </div>
                            </div>
                            <br>
                        </c:forEach>
                    </c:if>

                    <h4 class="section-heading-5 ui subheading">Sharing Period:</h4>

                    <div class="padding" style="border:1px solid #555;">
                        <c:choose>
                            <c:when test="${!isSharedWithinDay}">
                                <div class="padding-top ui subheading">
                                    Your data will be shared for the given sharing period :
                                    <button type="button" class="collapsible" id="consent-expiry-date"> <%=currentDate%> - <%=consentExpiryDate%>
                                        <c:if test="${isConsentAmendment && isSharingDurationUpdated}">
                                            <span style="border: 1px solid #1b2c8f;color:#1b2c8f;font-weight:bold;background-color:#f4f5fd">New</span>
                                        </c:if>
                                    </button>
                                    <div class="content">
                                        <!-- <div class="padding-top ui subheading">How Often your data will be shared : -->
                                        <h5 class="section-heading-5 padding-left ui subheading">
                                            <span id="consentExpiryTime"></span>
                                        </h5>
                                        <!-- </div> -->
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <!-- <div class="padding-top ui subheading">How Often your data will be shared : -->
                                <h5 class="section-heading-5 padding-left ui subheading">
                                    <span id="consentExpiryTime"></span>
                                    <c:if test="${isConsentAmendment && isSharingDurationUpdated}">
                                        <span style="border: 1px solid #1b2c8f;color:#1b2c8f;font-weight:bold;background-color:#f4f5fd">New</span>
                                    </c:if>
                                </h5>
                                <!-- </div> -->
                            </c:otherwise>
                        </c:choose>
                    </div>

                     <div class="padding-top ui subheading">Where to manage this arrangement :
                        <h5 class="section-heading-5 padding-left ui subheading">
                            <span> You can review and manage this arrangement on the Data Sharing dashboard by going to Settings>Data Sharing on the <%=spFullName%> website or app.</span>
                        </h5>
                    </div>
                    <div class="padding-top ui subheading">If you want to stop sharing this data :
                        <h5 class="section-heading-5 padding-left ui subheading">
                            <span> You can request us to stop sharing your data on your Data Sharing dashboard or by writing to <%=spFullName%> email.</span>
                        </h5>
                    </div>
                    <div class="ui">
                        <hr>
                        If you want to stop sharing data, you can request us to stop sharing data on your data sharing
                        dashboard.
                        </br>
                        Do you confirm that we can share your data with <%=spFullName%>?
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="col-md-12">
                    <div class="ui box">
                       <input class="ui default column button btn btn-default" type="reset" value="Deny" onclick="deny()"
                           data-toggle="modal" data-target="#cancelModel" />
                        <input type="button" class="ui default column button btn btn-default" id="back" name="back"
                               onclick="history.back();"
                               value="Back"/>
                        <input type="button" class="ui primary column button btn btn-primary" id="approve" name="approve"
                               onclick="javascript: approvedAU(); return false;"
                               value="Authorise"/>
                        <input type="hidden" id="hasApprovedAlways" name="hasApprovedAlways" value="false"/>
                        <input type="hidden" name="sessionDataKeyConsent" value="<%=sessionDataKeyConsent%>"/>
                        <input type="hidden" name="consent" id="consent" value="deny"/>
                        <input type="hidden" name="app" id="app" value="<%=appName%>"/>
                        <input type="hidden" name="type" id="type" value="accounts"/>
                        <input type="hidden" name="accounts[]" id="account" value="<%=accounts%>">
                        <input type="hidden" name="spFullName" id="spFullName" value="<%=spFullName%>"/>
                        <input type="hidden" name="user" id="user" value="<%=userName%>"/>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                    <div class="well policy-info-message" role="alert margin-top-5x">
                        <div>
                            ${privacyDescription}
                            <a href="privacy_policy.do" target="policy-pane">
                                ${privacyGeneral}
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<script>
    $(document).ready(function(){
        var accountMaskinEnabled="<%=accountMaskingEnabled%>";

        function maskAccountId(accountId) {
            var start = accountId.substring(0,4);
            var end = accountId.slice(accountId.length - 4);
            var mask = "*".repeat(accountId.length - 8);
            var maskedAccId = start + mask + end;
            return maskedAccId;
        }

        if (accountMaskinEnabled == "true") {
            var accountElements = document.getElementsByClassName("accountIdClass");
            for (var i = 0; i < accountElements.length; i++) {
                var elementId = accountElements.item(i).id;
                document.getElementById(elementId).textContent=maskAccountId(elementId);
            }
        }

        var consentExpiryDate = "<%=consentExpiryDateTime%>";
        var output = "";
        var finalOutput = "";

        if ("Single use consent" == consentExpiryDate) {
            output = "Your data will be shared once.";
        } else {
            var consentExpiryTime = consentExpiryDate.split("T")[0] + " " + (consentExpiryDate.split("T")[1]).split("\\.")[0];
            if (!navigator.userAgent.match(/(chrome|firefox)\/?\s*(\d+)/i)) {
                consentExpiryTime = consentExpiryDate.split(".")[0] + "Z";
            }
            var datetime = new Date(consentExpiryTime);
            var date = new Date();
            var now = new Date(date.getTime());
            var diff = datetime.getTime() - now.getTime();

            var seconds = Number(diff/1000)
            var months = Math.floor(seconds / (3600*24*30));
            var days = Math.ceil((seconds / (3600*24)) % 30);
            var hours = Math.floor(seconds % (3600*24) / 3600);
            var mins = Math.ceil(seconds % 3600 / 60);

            var monthsDisplay = months > 0 ? months + (months == 1 ? " month " : " months ") : "";
            var daysDisplay = days > 0 ? days + (days == 1 ? " day " : " days ") : "";
            var hoursDisplay = hours > 0 ? hours + (hours == 1 ? " hour " : " hours ") : "";
            var minsDisplay = mins > 0 ? mins + (mins == 1 ? " minute " : " minutes ") : "";

            var value;
            if (seconds < 86400) {
                if (hoursDisplay == "") {
                    finalOutput = "Your data will be accessible for the next 1 hour";
                } else {
                    if (minsDisplay == "60 minutes ") {
                        var hour = hoursDisplay.substring(0,2);
                        if (hour > 0) {
                            value = (parseInt(hour) + 1) + " hours";
                        } else {
                            value = "1 hour ";
                        }
                    } else {
                        value = hoursDisplay;
                    }
                }
            } else {
                if (daysDisplay == "30 days ") {
                    var month = monthsDisplay.substring(0,2);
                    if (month > 0) {
                        value = (parseInt(month) + 1) + " months";
                    } else {
                        value = "1 month ";
                    }
                } else {
                    var month = monthsDisplay.substring(0,2);
                    if (month >= 12){
                        value = "1 year";
                    } else if (month == 0){
                        value = daysDisplay;
                    } else {
                        value = monthsDisplay + "and " + daysDisplay;
                    }
                }
            }

            if (finalOutput == "") {
                output = "Your data will be shared on an on-going basis for " +  value;
            } else {
                output = finalOutput;
            }

        }
        document.getElementById("consentExpiryTime").textContent= output;
    });

    var coll = document.getElementsByClassName("collapsible");
    var i;

    for (i = 0; i < coll.length; i++) {
      coll[i].addEventListener("click", function() {
        this.classList.toggle("active");
        var content = this.nextElementSibling;
        if (content.style.maxHeight){
          content.style.maxHeight = null;
        } else {
          content.style.maxHeight = content.scrollHeight + "px";
        }
      });
    }
</script>

<jsp:include page="includes/consent_bottom.jsp"/>
