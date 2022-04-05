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

import moment from "moment";
import React from "react";
import {PreviousConsentDataItem} from "./PreviousConsentDataItem";

export const ConsentHistoryTableBody = ({ consentHistory, consentHistoryLang}) => {

  let id = 0;
  function getReason(amendedReason){
    let reason;
    Object.keys(consentHistoryLang.consentAmendmentReasonLabels).forEach(key => {
            if (key == amendedReason) {
              reason = consentHistoryLang.consentAmendmentReasonLabels[key];
            }
        }
    );
    return reason;
  }

  return (
      <tbody key={id = id + 1}>
      {(
          consentHistory.length === 0 ? (
                  <tr id="noConsentsLbl" key={(id = id + 1)}>
                    <td id="lbl" colSpan={4} key={(id = id + 1)}>
                      No {consentHistoryLang.consentHistoryLabel} to display
                    </td>
                  </tr>
              ):(
              consentHistory.map((consent) => (
                  <tr key={(id = id + 1)}>
                    <td key={(id = id + 1)}>
                        {(consent.amendedTime !== "") ? moment((consent.amendedTime * 1000)).format(
                        "DD MMM YYYY HH:mm:ss"
                        ) : ""}
                    </td>
                    <td key={(id = id + 1)}>
                      <div className="dataClusters">
                        <div>{getReason(consent.amendedReason)}</div>
                      </div>
                    </td>
                    <td key={(id = id + 1)}>
                      <div className="dataClusters">
                          <PreviousConsentDataItem permissionScope={consent.previousConsentData.permissions[0]}
                                                   consent = {consent}
                                                   headerLang={consentHistoryLang.consentHistoryTableHeaders}/>
                      </div>
                    </td>
                  </tr>
              ))
          )
      )}
      </tbody>
  );
};
