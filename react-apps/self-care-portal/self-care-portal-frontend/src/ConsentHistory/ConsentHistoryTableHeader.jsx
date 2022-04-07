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

export const ConsentHistoryTableHeader = ({ headerLang }) => {
    return (
      <thead>
        <tr>
            <>
            <th>{headerLang.historyUpdatedTime}</th>
            <th>{headerLang.reason}</th>
            <th>{headerLang.previousConsent}</th>
            </>
        </tr>
      </thead>
    );
};
