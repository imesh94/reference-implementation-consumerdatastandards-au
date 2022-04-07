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

import axios from "axios";
import { CONFIG } from "../config";
import Cookies from "js-cookie";
import User from "../data/User";

/**
 * Get the consent amendments history of a consent from the API.
 */
export const getConsentHistoryFromAPI = (consentId, userId) => {
  var consentHistoryUrl;

  consentHistoryUrl =
      `${CONFIG.BACKEND_URL}/admin/consent-amendment-history?cdrArrangementID=${consentId}&userID=${userId}`;

  const requestConfig = {
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + Cookies.get(User.CONST.OB_SCP_ACC_TOKEN_P1),
    },
    method: "GET",
    url: `${consentHistoryUrl}`
  };
  return axios
    .request(requestConfig)
    .then((response) => {
      return Promise.resolve(response);
    })
    .catch((error) => {
      return Promise.reject(error);
    });
};
