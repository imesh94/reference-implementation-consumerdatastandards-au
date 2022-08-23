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