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

<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="includes/consent_top.jsp"/>
<%
    session.setAttribute("configParamsMap", request.getAttribute("data_requested"));
%>
<div class="row data-container">
    <div class="clearfix"></div>
    <form action="${pageContext.request.contextPath}/oauth2_authz_consent.do" method="post" id="oauth2_authz_consent"
          name="oauth2_authz_consent"
          class="form-horizontal">
        <div class="login-form">
            <div class="form-group ui form">
                <div class="col-md-12 ui box">
                    <h3 class="ui header"><strong>${sp_full_name}
                    </strong> requests account details on your account - ob_default.jsp.
                    </h3>
                </div>
            </div>

            <c:if test="${not empty account_data}">
                <div class="form-group ui form select">
                    <h5 class="ui body col-md-12">
                        Select the accounts you wish to authorise:
                    </h5>
                    <div class="col-md-12" >
                        <c:forEach items="${account_data}" var="record">
                            <label for="${record['display_name']}">
                                <input type="checkbox" id="${record['display_name']}" name="chkAccounts"
                                       value="${record['account_id']}" onclick="updateAcc()"
                                />
                                    ${record['display_name']} </br> <small>${record['account_id']}</small>
                            </label>
                            <br>
                        </c:forEach>
                    </div>
                </div>
            </c:if>

            <div class="form-group ui form row">
                <div class="ui body col-md-12">
                    <input type="button" class="ui primary button btn btn-primary" id="approve" name="confirm account"
                           onclick="approvedAcc(); return false;" value="Approve"/>
                    <input type="hidden" id="hasApprovedAlways" name="hasApprovedAlways" value="false"/>
                    <input type="hidden" name="sessionDataKeyConsent" value="${sessionDataKeyConsent}"/>
                    <input type="hidden" name="consent" id="consent" value="deny"/>
                    <input type="hidden" name="app" id="app" value="${app}"/>
                    <input type="hidden" name="accountsArry[]" id="account" value=""/>
                    <input type="hidden" name="accNames" id="accountName" value=""/>
                    <input type="hidden" name="type" id="type" value="accounts"/>
                    <input type="hidden" name="consent-expiry-date" id="consentExp" value="${data_requested}"/>
                </div>
            </div>

            <div class="form-group ui form row">
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
<jsp:include page="includes/consent_bottom.jsp"/>
