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
import {useSelector } from "react-redux";
import "../css/LandingTable.css";
import Table from "react-bootstrap/Table";
import {ConsentHistoryTableHeader} from "./ConsentHistoryTableHeader";
import {ConsentHistoryTableBody} from "./ConsentHistoryTableBody";
import {lang, specConfigurations} from "../specConfigs/specConfigurations";

export const ConsentHistoryLandingTable = (props) => {

  var consentHistoryResponse = useSelector((state) => state.consentHistory.consentHistory);
  const consentHistory = consentHistoryResponse.consentAmendmentHistory;

  const consentHistoryLang = specConfigurations.consentHistory;

  return (
    <>
      <hr id="sharingDetailsHr" className="horizontalLine" />
      <div id="ConsentHistoryBox" className="infoBox">
        <h5>{consentHistoryLang.consentHistoryLabel}</h5>
      </div>
      <div className={"consentHistoryBody"}>
        <Table responsive="sm" className="landingTable">
          <ConsentHistoryTableHeader
              headerLang={consentHistoryLang.consentHistoryTableHeaders}
          />
          <ConsentHistoryTableBody
              consentHistory={consentHistory}
              consentHistoryLang={consentHistoryLang}
          />
        </Table>
      </div>
    </>
  );
}
;
