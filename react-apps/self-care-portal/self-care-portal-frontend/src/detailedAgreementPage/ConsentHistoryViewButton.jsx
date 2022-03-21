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

import React, {useEffect} from "react";
import "../css/Buttons.css";
import {Link} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {getConsentHistory} from "../store/actions";
import {specConfigurations} from "../specConfigs/specConfigurations";
import {getValueFromConsent} from "../services";

export const ConsentHistoryViewButton = ({consent}) => {

    const dispatch = useDispatch();
    const user = useSelector(state => state.currentUser.user);
    const userId = (user.email.endsWith("@carbon.super") ? (user.email) : user.email + '@carbon.super');

    useEffect(() => {
        dispatch(getConsentHistory(getValueFromConsent("consentId", consent), userId));
    }, []);

    var consentHistoryResponse = useSelector((state) => state.consentHistory.consentHistory);
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
