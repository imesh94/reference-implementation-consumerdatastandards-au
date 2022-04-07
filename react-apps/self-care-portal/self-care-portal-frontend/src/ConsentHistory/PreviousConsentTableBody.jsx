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
import {PermissionView} from "./PermissionView";

let id = 0;
export const PreviousConsentTableBody = ({ previousConsent}) => {

  function getAccountList(consent) {
    const accounts = [];
    consent.userList.map((userList) => (
        userList.accountList.map((account) => (
            accounts.push(account)
        ))
    ))
    return Array.from(new Set(accounts))
  }

  function getSharingDurationDays(sharingDuration) {
    return Math.floor(sharingDuration / 86400)
  }

  function getSharingDurationHours(sharingDuration) {
    // reduce the of days and calculate the remaining hours
    const days = getSharingDurationDays(sharingDuration)
    sharingDuration -= days * 86400;
    return Math.round(sharingDuration/3600 * 10) / 10;
  }

  return (
      <tbody key={id = id + 1}>
                  <tr key={(id = id + 1)}>
                    <td key={(id = id + 1)}>
                      <div className="dataClusters">
                        {getAccountList(previousConsent).map((account) => (
                                <div>{account}</div>
                            )
                        )}
                      </div>
                    </td>
                    <td key={(id = id + 1)}>
                      <PermissionView AmendedPermissions={previousConsent.permissions}/>
                    </td>
                    <td key={(id = id + 1)}>
                      {getSharingDurationDays(previousConsent.sharingDuration)} Days &nbsp;
                      {getSharingDurationHours(previousConsent.sharingDuration)} Hours
                    </td>
                  </tr>
      </tbody>
  );
};
