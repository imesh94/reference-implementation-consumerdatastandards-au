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

import React from "react";
import {CONFIG} from "../config";
import {AccountsInfoCDS} from "../specConfigs/CDS/componants/detailedAggrementPage/AccountsInfoCDS";
import {AccountsInfoUK} from "../specConfigs/UK/componants/detailedAggrementPage/AccountsInfoUK";
import {AccountsInfoBG} from "../specConfigs/BG/componants/detailedAggrementPage/AccountsInfoBG";
import {AccountsInfoDefault} from "../specConfigs/Default/componants/detailedAggrementPage/AccountsInfoDefault";


export const AccountsInfo = ({consent}) => {

    return (
        <>
            {
                CONFIG.SPEC === 'Default' ? (
                    <AccountsInfoDefault consent={consent}/>
                ) : CONFIG.SPEC === 'CDS' ? (
                    <AccountsInfoCDS consent={consent}/>
                ) : CONFIG.SPEC === 'UK' ? (
                    <AccountsInfoUK consent={consent}/>
                ) : CONFIG.SPEC === 'BG' ? (
                    <AccountsInfoBG consent={consent}/>
                ) : (
                    <div className="accountsInfoBody"/>
                )
            }
        </>
    );
};
