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

import { SET_APPINFO, SET_APPINFO_REQUEST_LOADING_STATUS } from "../actions/action-types";

/**
 * Initial reducer state.
 *
 */
const initialState = {
    isGetRequestLoading: false,
    appInfo: []
};

export const appInfoReducer = (state = initialState, action) => {
    switch (action.type) {
        case SET_APPINFO:
            return {
                ...state,
                appInfo: action.payload
            };
        case SET_APPINFO_REQUEST_LOADING_STATUS:
            return {
                ...state,
                isGetRequestLoading: action.payload
            };
        default:
            return state;
    }
};
