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
    session.setAttribute("accounts_data", request.getAttribute("accounts_data"));
    session.setAttribute("profiles_data", request.getAttribute("profiles_data"));
    session.setAttribute("sp_full_name", request.getAttribute("sp_full_name"));
    session.setAttribute("consent_expiration", request.getAttribute("consent_expiration"));
    session.setAttribute("account_masking_enabled", request.getAttribute("account_masking_enabled"));
    session.setAttribute("isConsentAmendment", request.getAttribute("isConsentAmendment"));
    session.setAttribute("isSharingDurationUpdated", request.getAttribute("isSharingDurationUpdated"));
    session.setAttribute("app", request.getAttribute("app"));
    session.setAttribute("configParamsMap", request.getAttribute("data_requested"));
    session.setAttribute("newConfigParamsMap", request.getAttribute("new_data_requested"));
    String popoverTemplate = "<div class='popover dark-bg' role='tooltip'><div class='arrow'></div><h6 class='popover-title dark-bg'></h6><div class='popover-content'></div></div>";
%>
<div class="row data-container">
    <div class="clearfix"></div>
    <form action="${pageContext.request.contextPath}/ob_cds_account_selection.do" method="post" id="cds_profile_selection"
          name="cds_profile_selection"
          class="form-horizontal">
        <div class="login-form">
            <div class="form-group ui form">
                <div class="col-md-12 ui box">
                    <h3 class="ui header"><strong>${sp_full_name}
                    </strong> requests account details on your account.
                    </h3>
                </div>
            </div>

            <c:if test="${not empty profiles_data}">
                <div class="form-group ui form select">
                    <h5 class="ui body col-md-12">
                        Please select the profile you would like to share data from:
                    </h5>
                    <div class="col-md-12" >
                        <c:forEach items="${profiles_data}" var="record">
                            <label for="${record['profileName']}">
                                <input type="radio" id="${record['profileName']}" name="optProfiles"
                                    value="${record['profileId']}" onclick="setSelectedProfile()"/>
                                ${record['profileName']}
                            </label>
                            <br/>
                        </c:forEach>
                    </div>
                </div>
            </c:if>

            <div class="form-group ui form row">
                <div class="ui body col-md-12">
                    <input type="submit" class="ui primary button btn btn-primary" id="btnNext" name="confirm profile"
                           value="Next"/>
                    <input type="hidden" id="hasApprovedAlways" name="hasApprovedAlways" value="false"/>
                    <input type="hidden" name="consent" id="consent" value="deny"/>
                    <input type="hidden" name="accountsArry[]" id="account" value=""/>
                    <input type="hidden" name="accNames" id="accountName" value=""/>
                    <input type="hidden" name="type" id="type" value="accounts"/>
                    <input type="hidden" name="selectedProfileId" id="selectedProfileId" value=""/>
                    <input type="hidden" name="sessionDataKeyConsent" value="${sessionDataKeyConsent}"/>
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

<script>
    function setSelectedProfile() {
        var selectedProfileInput = document.getElementById("selectedProfileId");
        var selectedProfile = document.querySelector('input[name="optProfiles"]:checked').value;
        selectedProfileInput.value = selectedProfile;
    }
</script>

<jsp:include page="includes/consent_bottom.jsp"/>
