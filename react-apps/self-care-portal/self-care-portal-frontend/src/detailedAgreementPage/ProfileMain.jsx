/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

import {Container} from "react-bootstrap";
import {Link} from "react-router-dom";
import {withdrawLang} from "../specConfigs";
import ADRLogo from "../images/ADRLogo.png";
import moment from "moment";
import {specConfigurations} from "../specConfigs/specConfigurations";
import {generatePDF, getExpireTimeFromConsent} from "../services/utils";


export const ProfileMain = ({consent, infoLabel, appicationName, logoURL}) => {

    const consentConsentId = consent.consentId;
    const currentDate = moment().format("YYYY-MM-DDTHH:mm:ss[Z]");

    if (logoURL === undefined || logoURL === '') {
        logoURL = ADRLogo
    }

    function isNotExpired() {
        try {
            let expireTimeFromConsent = getExpireTimeFromConsent(consent, "YYYY-MM-DDTHH:mm:ss[Z]");
            if (!expireTimeFromConsent) {
                return true;
            }
            return moment(currentDate)
                .isBefore(expireTimeFromConsent);
        } catch (e) {
            return true;
        }
    }

    const consentStatusLabel = (consent.currentStatus.toLowerCase() ===
        specConfigurations.status.authorised.toLowerCase() && !isNotExpired())
        ? specConfigurations.status.expired : infoLabel.label;
    return (
        <Container className="profileMain">
            <img id="profileLogo" src={logoURL} width="50" height="50" alt="new"/>
            <h4 className="mt-3">{appicationName}</h4>
            <>
                <div className="confirmLink">
                    <a id="confirmationReportLink" href="javascript:void(0);"
                       onClick={() => generatePDF(consent, appicationName, consentStatusLabel)}>
                        {infoLabel.profile.confirmation}
                    </a>
                </div>
                {consent.currentStatus.toLowerCase() ===
                specConfigurations.status.authorised.toLowerCase() && isNotExpired() ? (
                    <div className="actionButtons">
                        <div className="actionBtnDiv">
                            <Link
                                to={`/consentmgr/${consentConsentId}/withdrawal-step-1`}
                                className="withdrawBtn"
                            >
                                {withdrawLang.detailedConsentPageStopSharingBtn}
                            </Link>
                        </div>
                    </div>
                ) : (
                    <div className="actionButtons">
                    </div>
                )
                }
            </>
        </Container>
    );
};
