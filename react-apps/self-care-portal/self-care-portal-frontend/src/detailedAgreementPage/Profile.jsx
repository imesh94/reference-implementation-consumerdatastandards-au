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

import React, {useEffect, useState} from "react";
import {Accreditation, ProfileMain, StatusLabel} from "../detailedAgreementPage";
import {AccreditationCDS} from "../specConfigs/CDS/componants/detailedAggrementPage/AccreditationCDS";
import {AccreditationUK} from "../specConfigs/UK/componants/detailedAggrementPage/AccreditationUK";
import {AccreditationBG} from "../specConfigs/BG/componants/detailedAggrementPage/AccreditationBG";

import "../css/Profile.css";
import "../css/Buttons.css";
import {CONFIG} from "../config";
import {getExpireTimeFromConsent} from "../services/utils";
import {AccreditationDefault} from "../specConfigs/Default/componants/detailedAggrementPage/AccreditationDefault";

export const Profile = ({consent, infoLabel, appicationName, logoURL}) => {


    const [expireTime, setExpireTime] = useState(() => {
        return getExpireTimeFromConsent(consent, "YYYY-MM-DDTHH:mm:ss[Z]");
    });

    useEffect(() => {
        setExpireTime(getExpireTimeFromConsent(consent, "YYYY-MM-DDTHH:mm:ss[Z]"));
    }, [consent]);


    return (
        <>
            <div className="profileBody">
                <StatusLabel
                    infoLabel={infoLabel}
                    expireDate={expireTime}
                />
                <ProfileMain consent={consent} infoLabel={infoLabel} appicationName={appicationName}
                             logoURL={logoURL}/>
                <hr className="horizontalLine"/>
                <div className="infoBox">
                    {
                        CONFIG.SPEC == 'Default' ? (
                            <AccreditationDefault infoLabel={infoLabel} accreditationNumber={appicationName}
                                                  applicationName={appicationName}/>
                        ) : CONFIG.SPEC == 'CDS' ? (
                            <AccreditationCDS infoLabel={infoLabel} accreditationNumber={appicationName}
                                              applicationName={appicationName}/>
                        ) : CONFIG.SPEC == 'UK' ? (
                            <AccreditationUK infoLabel={infoLabel} accreditationNumber={appicationName}
                                             applicationName={appicationName}/>
                        ) : CONFIG.SPEC == 'BG' ? (
                            <AccreditationBG infoLabel={infoLabel} applicationName={appicationName}/>
                        ) : (
                            <Accreditation infoLabel={infoLabel} accreditationNumber={appicationName}
                                           applicationName={appicationName}/>
                        )
                    }
                </div>
                <div className="infoBox">
                    <h6>Other important information</h6>
                    <p>
                        There may be additional important information not shown here. Please
                        check this sharing arrangement of {appicationName}’s
                        website/app.
                    </p>
                </div>
            </div>
        </>
    );
};
