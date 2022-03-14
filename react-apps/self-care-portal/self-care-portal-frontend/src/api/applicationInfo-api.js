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
import {CONFIG} from "../config";

/**
 * Get the service provider application information.
 */
export const getApplicationInfo = async (clientIdList) => {
    var serverURL = CONFIG.SERVER_URL
    const requestConfig = {
        method: "GET",
        url: `${serverURL}/api/openbanking/application/all/metadata`
    };

    return await axios
        .request(requestConfig)
        .then((response) => {
            return Promise.resolve(response);
        })
        .catch((error) => {
            return Promise.reject(error);
        });
};
