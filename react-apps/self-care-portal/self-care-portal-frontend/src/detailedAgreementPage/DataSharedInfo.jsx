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
import {DataSharedInfoCDS} from "../specConfigs/CDS/componants/detailedAggrementPage/DataSharedInfoCDS";
import {DataSharedInfoUK} from "../specConfigs/UK/componants/detailedAggrementPage/DataSharedInfoUK";
import {DataSharedInfoBG} from "../specConfigs/BG/componants/detailedAggrementPage/DataSharedInfoBG";
import {DataSharedInfoDefault} from "../specConfigs/Default/componants/detailedAggrementPage/DataSharedInfoDefault";

export const DataSharedInfo = ({consent, infoLabels}) => {

    return (
        <>
            {
                CONFIG.SPEC === 'Default' ? (
                    <DataSharedInfoDefault consent={consent} infoLabels={infoLabels}/>
                ) : CONFIG.SPEC === 'CDS' ? (
                    <DataSharedInfoCDS consent={consent} infoLabels={infoLabels}/>
                ) : CONFIG.SPEC === 'UK' ? (
                    <DataSharedInfoUK consent={consent} infoLabels={infoLabels}/>
                ) : CONFIG.SPEC === 'BG' ? (
                    <DataSharedInfoBG consent={consent} infoLabels={infoLabels}/>
                ) : (
                    <div className="dataSharedBody"/>
                )
            }
        </>
    );
};
