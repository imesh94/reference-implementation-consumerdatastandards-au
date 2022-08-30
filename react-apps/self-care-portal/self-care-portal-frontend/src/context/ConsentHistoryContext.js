/*
 * Copyright (c) 2022, WSO2 LLC (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

import React, { useState ,createContext} from 'react'
import { getConsentHistoryFromAPI } from '../api';

export const ConsentHistoryContext = createContext();

const ConsentHistoryContextProvider = (props) => {
    const [contextConsentHistory,setContextConsentHistory] = useState({
        isGetRequestLoading:false,
        consentHistory:[]
    });

    const setConsentHistory = (payload) => {
        setContextConsentHistory((prevState)=>({
            ...prevState,
            consentHistory:payload
        }))
    };

    const setConsentHistoryRequestLoadingStatus = (payload) => {
        setContextConsentHistory((prevState)=>({
            ...prevState,
            isGetRequestLoading:payload
        }))
    };

    const getContextConsentHistory = (consentId,userId)=>{
        setConsentHistoryRequestLoadingStatus(true);
        getConsentHistoryFromAPI(consentId,userId)
            .then((response)=>setConsentHistory(response.data))
            .catch((error)=>{
                /* Log the error */
            })
            .finally(()=>setConsentHistoryRequestLoadingStatus(false));
        };

    const value = {
        contextConsentHistory,
        getContextConsentHistory
    }
    return ( 
        <ConsentHistoryContext.Provider value = {value}>
            {props.children}
        </ConsentHistoryContext.Provider>
     );
}
 
export default ConsentHistoryContextProvider;
