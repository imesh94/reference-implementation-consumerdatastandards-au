import React from 'react'
import UserContextProvider from './UserContext';
import SearchObjectContextProvider from './SearchObjectContext';
import ConsentContextProvider from './ConsentContext';
import AppInfoContextProvider from './AppInfoContext';
import DeviceRegistrationContextProvider from './DeviceRegistrationContext';
import ConsentHistoryContextProvider from './ConsentHistoryContext';

const contextProviderArray = [ConsentHistoryContextProvider,
    DeviceRegistrationContextProvider,
    AppInfoContextProvider,
    ConsentContextProvider,
    SearchObjectContextProvider,
    UserContextProvider]


const AppContextProvider = (props) => {
    return ( 
        contextProviderArray.reduce((Children,Provider)=>{
            return(
                <Provider>{Children}</Provider>
            );
        },props.children)
     );
}
 
export default AppContextProvider;