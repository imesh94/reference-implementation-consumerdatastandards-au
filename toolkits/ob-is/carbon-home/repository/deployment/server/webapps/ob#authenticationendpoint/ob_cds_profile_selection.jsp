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
    session.setAttribute("redirectURL", request.getAttribute("redirectURL"));
    session.setAttribute("consent_expiration", request.getAttribute("consent_expiration"));
    session.setAttribute("account_masking_enabled", request.getAttribute("account_masking_enabled"));
    session.setAttribute("isConsentAmendment", request.getAttribute("isConsentAmendment"));
    session.setAttribute("isSharingDurationUpdated", request.getAttribute("isSharingDurationUpdated"));
    session.setAttribute("app", request.getAttribute("app"));
    session.setAttribute("configParamsMap", request.getAttribute("data_requested"));
    session.setAttribute("newConfigParamsMap", request.getAttribute("new_data_requested"));
    session.setAttribute("business_data_cluster", request.getAttribute("business_data_cluster"));
    session.setAttribute("new_business_data_cluster", request.getAttribute("new_business_data_cluster"));
    session.setAttribute("state", request.getAttribute("state"));
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
                                    value="${record['profileId']}" data-profile-name="${record['profileName']}"
                                onclick="setSelectedProfile()"/>
                                ${record['profileName']}
                            </label>
                            <br/>
                        </c:forEach>
                    </div>
                </div>
            </c:if>

            <div class="form-group ui form row">
                <div class="ui body col-md-12">
                    <input type="button" class="ui default column button btn btn-default" id="cancel" name="cancel"
                           onclick="showModal()" checked data-toggle="modal" data-target="#cancelModel" value="Cancel"/>
                    <input type="submit" class="ui primary column button btn" id="btnNext" name="confirm profile"
                           value="Next"/>
                    <input type="hidden" id="hasApprovedAlways" name="hasApprovedAlways" value="false"/>
                    <input type="hidden" name="consent" id="consent" value="deny"/>
                    <input type="hidden" name="accountsArry[]" id="account" value=""/>
                    <input type="hidden" name="accNames" id="accountName" value=""/>
                    <input type="hidden" name="type" id="type" value="accounts"/>
                    <input type="hidden" name="selectedProfileId" id="selectedProfileId" value=""/>
                    <input type="hidden" name="selectedProfileName" id="selectedProfileName" value=""/>
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

<!-- Cancel Modal -->
<div class="modal" id="cancelModal">
    <div class="modal-dialog">
        <div class="modal-content">

            <!-- Modal body -->
            <div class="modal-body">
                <p style="color:black"> Unless you confirm your authorisation, we won't be able to share your data with
                    "${sp_full_name}". <br>
                    <br> Are you sure you would like to cancel this process? </p>

                <div class="ui two column grid">
                    <table style="width:100%">
                        <tbody>
                        <tr>
                            <td>
                                <div class="md-col-6 column align-left buttons">
                                    <input type="button" onclick="redirect()" class="ui default column button btn btn-default"
                                           id="registerLink" role="button" value="Yes cancel">
                                </div>
                            </td>
                            <td>
                                <div class="column align-right buttons">
                                    <input type="button" onclick="closeModal()" class="ui primary column button btn" role="button"
                                           value="No continue" style="float:right;">
                                </div>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </div>
</div>

<script>
    let modal = document.getElementById("cancelModal");

    function showModal() {
        modal.style.display = "block";
    }

    function closeModal() {
        modal.style.display = "none";
    }

    function redirect() {
        let error = "User skip the consent flow";
        let state = "${state}"
        if (state) {
            top.location = "${redirectURL}#error=access_denied&error_description=" + error +
                "&state=" + state;
        } else {
            top.location = "${redirectURL}#error=access_denied&error_description=" + error;
        }
    }

    window.onclick = function (event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }

    function setSelectedProfile() {
        var selectedProfileIdInput = document.getElementById("selectedProfileId");
        var selectedProfileNameInput = document.getElementById("selectedProfileName");
        var selectedProfileId = document.querySelector('input[name="optProfiles"]:checked').value;
        var selectedProfileName = document.querySelector('input[name="optProfiles"]:checked').dataset.profileName;
        selectedProfileIdInput.value = selectedProfileId;
        selectedProfileNameInput.value = selectedProfileName;
    }
</script>

<jsp:include page="includes/consent_bottom.jsp"/>
