/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

import React, {useContext, useEffect} from "react";
import "../css/Buttons.css";
import {Link} from "react-router-dom";
import {specConfigurations} from "../specConfigs/specConfigurations";
import {getValueFromConsent} from "../services";
import { ConsentHistoryContext } from "../context/ConsentHistoryContext";
import { UserContext } from "../context/UserContext";

export const ConsentHistoryViewButton = ({consent}) => {
    const {contextConsentHistory,getContextConsentHistory} = useContext(ConsentHistoryContext)
    const {currentContextUser} = useContext(UserContext)

    const user = currentContextUser.user;
    const userId = (user.email.endsWith("@carbon.super") ? (user.email) : user.email + '@carbon.super');

    useEffect(() => {
        getContextConsentHistory(getValueFromConsent("consentId",consent),userId);
    }, []);

    var consentHistoryResponse = contextConsentHistory.consentHistory;
    const consentHistory = consentHistoryResponse.consentAmendmentHistory;

    return (
        <>
        {(
            (typeof(consentHistory) !== 'undefined' && consentHistory != null) ? (
                <div>
                    <h5>{specConfigurations.consentHistory.consentHistoryLabel}</h5>
                    <div className="consentHistoryActionBtnDiv">
                        <Link
                            to={`/consentmgr/consent-history/${getValueFromConsent("consentId", consent)}`}
                            className="comButton">
                            {specConfigurations.consentHistory.consentHistoryView}
                        </Link>
                    </div>
                </div>
            ):(
                <div/>
            )
        )}
        </>
    );
};
