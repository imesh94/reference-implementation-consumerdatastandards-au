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
import CDRLogo from "../images/CDRLogo.png";

export const Accreditation = ({infoLabel, accreditationNumber, applicationName}) => {
    return (
        <>
            <h6>{infoLabel.accreditation.accreditationLabel}</h6>
            <p>
                {applicationName} {infoLabel.accreditation.accreditWebsite} [
                <a href={infoLabel.accreditation.accreditWebsiteLink} target="_blank" rel="noreferrer">
                    {/* add website link */}
                    {infoLabel.accreditation.accreditWebsiteLinkText}
                </a>
                ]
            </p>
            <div className="accredBox">
                <img id="cdrLogo" src={CDRLogo} alt="CDR"/>
                <div className="accredInfo">
                    <h6>{applicationName}</h6>
                    {/* add accredited Data Recipient's number */}
                    <p>{infoLabel.accreditation.accreditDR} {accreditationNumber}</p>
                </div>
            </div>
        </>
    );
};
