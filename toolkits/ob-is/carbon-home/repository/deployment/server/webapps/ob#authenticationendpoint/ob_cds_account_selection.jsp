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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="includes/consent_top.jsp"/>
<%
    String preSelectedProfileId = (String) request.getAttribute("preSelectedProfileId");
    String selectedProfileId = (preSelectedProfileId == null || "".equals(preSelectedProfileId)) ?
            request.getParameter("selectedProfileId") : preSelectedProfileId;
    if (session.getAttribute("profiles_data") == null) {
        session.setAttribute("profiles_data", request.getAttribute("profiles_data"));
    }
    if (session.getAttribute("configParamsMap") == null) {
        session.setAttribute("configParamsMap", request.getAttribute("data_requested"));
    }
    if (session.getAttribute("newConfigParamsMap") == null) {
        session.setAttribute("newConfigParamsMap", request.getAttribute("new_data_requested"));
    }
    if (session.getAttribute("business_data_cluster") == null) {
        session.setAttribute("business_data_cluster", request.getAttribute("business_data_cluster"));
    }
    if (session.getAttribute("new_business_data_cluster") == null) {
        session.setAttribute("new_business_data_cluster", request.getAttribute("new_business_data_cluster"));
    }

    String popoverTemplate = "<div class='popover dark-bg' role='tooltip'><div class='arrow'></div><h6 class='popover-title dark-bg'></h6><div class='popover-content'></div></div>";
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
                    </strong> requests account details on your account.
                    </h3>
                </div>
            </div>

            <c:if test="${not empty accounts_data}">
                <div class="form-group ui form select">
                    <h5 class="ui body col-md-12">
                        Select the accounts you wish to authorise:
                    </h5>
                    <div class="col-md-12" >
                        <%--Get account ids for the selected profile--%>
                        <c:set var="selectedProfileId" scope="session" value="<%=selectedProfileId%>"/>
                        <c:forEach items="${profiles_data}" var="profile">
                            <c:if test="${profile['profileId'] eq selectedProfileId}">
                                <c:set var="profileAccountIds" value="${profile['accountIds']}" />
                                <c:set var="selectedProfileName" value="${profile['profileName']}" />
                            </c:if>
                        </c:forEach>
                        <%--Display filtered accounts--%>
                        <c:forEach items="${accounts_data}" var="record">
                            <c:if test="${fn:contains(profileAccountIds, record['accountId'])}">
                                <label for="${record['displayName']}">
                                    <input type="checkbox" id="${record['displayName']}" name="chkAccounts"
                                        value="${record['accountId']}" onclick="updateAcc()"
                                        ${record['isPreSelectedAccount'] ? 'checked' : ''}
                                        ${record['is_joint_account'] ? record['is_selectable'] ? "" : "disabled='disabled'" : ""}
                                        ${record['is_secondary_account'] ? record['is_selectable'] ? "" : "disabled='disabled'" : ""}
                                    />
                                    ${record['displayName']}
                                </label>

                                <span id="joint-accounts-info">
                                    <c:if test="${record['is_joint_account'] eq true}">
                                        <c:if test="${record['is_selectable'] ne true}">
                                            <%
                                                String disabledPopoverContent = "<p style='text-align: left'> There are a range of reasons why certain accounts may not available to share."
                                                    + "Please call the bank for more details.<br/><br/> For joint accounts, all account holders must elect to make the account available for sharing."
                                                    + "This can be done via the Data Sharing dashboard in Internet Banking or the app. </p>";
                                            %>
                                            <a tabindex="0" role="button" data-html="true" data-placement="auto top" data-toggle="popover" data-template="<%=popoverTemplate%>"
                                                data-trigger="focus" title="Why can't I share these?" data-content="<%=disabledPopoverContent%>">&#9432;</a>
                                        </c:if>
                                        <c:if test="${record['is_selectable'] eq true}">
                                            <%
                                                String selectablePopoverContent = "<span style='text-align: left'> other account holder(s) can share this joint account data at any time, "
                                                    + "without each other&lsquo;s permission. <br/><br/> You can change sharing preferences for this account by going to &lsquo;Settings &gt;"
                                                    + "Data sharing &gt; Account permissions&rsquo;</span>";
                                            %>
                                            <a tabindex="0" role="button" data-html="true" data-placement="auto top" data-toggle="popover" data-template="<%=popoverTemplate%>"
                                                data-trigger="focus" title="&check; Pre-approval enabled" data-content="${record['linked_members_count']}<%=selectablePopoverContent%>">&#9432;</a>
                                        </c:if>
                                    </c:if>
                                </span>

                                <span id="secondary-accounts-info">
                                    <c:if test="${record['is_secondary_account'] eq true}">
                                        <c:if test="${record['is_selectable'] ne true}">
                                            <%
                                                String disabledPopoverContent = "<p style='text-align: left'> The account holder(s) must give you secondary user data sharing rights before you can share data from this account."
                                                        + "<br/><br/>"
                                                        + "Please call the bank for more details. </p>";
                                            %>
                                            <a tabindex="0" role="button" data-html="true" data-placement="auto top" data-toggle="popover" data-template="<%=popoverTemplate%>"
                                               data-trigger="focus" title="Why can't I share these?" data-content="<%=disabledPopoverContent%>">&#9432;</a>
                                        </c:if>
                                    </c:if>
                                </span>

                                <div class="accountIdClass" id="${record['accountId']}">
                                    <small>${record['accountId']}</small>
                                </div><br/>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </c:if>

            <div class="form-group ui form row">
                <div class="ui body col-md-12">
                    <input type="button" class="ui default column button btn btn-default" id="cancel" name="cancel"
                           onclick="showModal()" checked data-toggle="modal" data-target="#cancelModel" value="Cancel"/>
                    <input type="button" class="ui primary button column btn" id="approve" name="confirm account"
                           onclick="approvedAcc(); return false;" value="Approve"/>
                    <input type="hidden" id="hasApprovedAlways" name="hasApprovedAlways" value="false"/>
                    <input type="hidden" name="sessionDataKeyConsent" value="${sessionDataKeyConsent}"/>
                    <input type="hidden" name="consent" id="consent" value="deny"/>
                    <input type="hidden" name="app" id="app" value="${app}"/>
                    <input type="hidden" name="spFullName" id="app" value="${sp_full_name}"/>
                    <input type="hidden" name="accountsArry[]" id="account" value=""/>
                    <input type="hidden" name="accNames" id="accountName" value=""/>
                    <input type="hidden" name="type" id="type" value="accounts"/>
                    <input type="hidden" name="consent-expiry-date" id="consentExp" value="${consent_expiration}"/>
                    <input type="hidden" name="accountMaskingEnabled" id="accountMaskingEnabled" value="${account_masking_enabled}"/>
                    <input type="hidden" name="isConsentAmendment" id="isConsentAmendment" value="${isConsentAmendment}"/>
                    <input type="hidden" name="isSharingDurationUpdated" id="isSharingDurationUpdated" value="${isSharingDurationUpdated}"/>
                    <input type="hidden" name="selectedProfileId" id="selectedProfileId" value="<%=selectedProfileId%>"/>
                    <input type="hidden" name="selectedProfileName" id="selectedProfileName" value="${selectedProfileName}"/>
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

    $(document).ready(function(){
        updateAcc();
        var accountMaskingEnabled="${account_masking_enabled}";

        function maskAccountId(accountId) {
            var start = accountId.substring(0,4);
            var end = accountId.slice(accountId.length - 3); 
            var mask = "*".repeat(accountId.length - 7); // 4+3
            var maskedAccId = start + mask + end; 
            return maskedAccId;
        }

        if (accountMaskingEnabled == "true") {
            var accountElements = document.getElementsByClassName("accountIdClass");
            for (var i = 0; i < accountElements.length; i++) {
                var elementId = accountElements.item(i).id;
                document.getElementById(elementId).textContent=maskAccountId(elementId);
            }
        }

        $('[data-toggle="popover"]').popover();
    });
</script>

<jsp:include page="includes/consent_bottom.jsp"/>
