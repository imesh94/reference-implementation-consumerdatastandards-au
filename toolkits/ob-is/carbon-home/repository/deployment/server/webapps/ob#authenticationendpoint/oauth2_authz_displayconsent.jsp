<%--
  ~ Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
  ~
  ~ This software is the property of WSO2 Inc. and its suppliers, if any.
  ~ Dissemination of any information or reproduction of any material contained
  ~ herein is strictly forbidden, unless permitted by WSO2 in accordance with
  ~ the WSO2 Commercial License available at http://wso2.com/licenses. For specific
  ~ language governing the permissions and limitations under this license,
  ~ please see the license as well as any agreement you’ve entered into with
  ~ WSO2 governing the purchase of this software and any associated services.
  --%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDateTime" %>
<jsp:include page="includes/consent_top.jsp"/>

<%
    String sessionDataKeyConsent = request.getParameter("sessionDataKeyConsent");
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
    session.setAttribute("configParamsMap", consentData);
%>
<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 data-container">
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

                    <h4 class="section-heading-5 ui subheading">Sharing Period:</h4>

                    <div class="padding" style="border:1px solid #555;">
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
