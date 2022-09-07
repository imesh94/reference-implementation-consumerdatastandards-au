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

import React, {useContext, useEffect, useState} from "react";
import {LandingTable} from "./LandingTable";
import "../css/LandingTabs.css";
import Tab from "react-bootstrap/Tab";
import Tabs from "react-bootstrap/Tabs";
import {lang} from "../specConfigs";
import {PaginationTable} from "./PaginationTable";
import { SearchObjectContext } from "../context/SearchObjectContext";
import { ConsentContext } from "../context/ConsentContext";
import { AppInfoContext } from "../context/AppInfoContext";
import { UserContext } from "../context/UserContext";


export const LandingTabs = () => {
    const {getContextConsentsForSearch} = useContext(ConsentContext);
    const {contextSearchObject,setContextSearchObject} = useContext(SearchObjectContext);
    const {contextAppInfo} = useContext(AppInfoContext);
    const {currentContextUser} = useContext(UserContext);

    let searchObj = contextSearchObject;
    const appInfo = contextAppInfo.appInfo;

    const [key, setKey] = useState(searchObj.consentStatuses);
    const currentUser = currentContextUser.user;

    useEffect(() => {
        let search = {
            ...searchObj,
            consentStatuses: key,
            offset: 0
        }
        setContextSearchObject(search);
        getContextConsentsForSearch(search, currentUser, appInfo);
    }, [key])

    return (
        <div>
            <Tabs id="status-tab" activeKey={key} onSelect={(k) => setKey(k)}>
                {lang.map(({label, id, description}) => (
                    <Tab key={id} eventKey={id} title={label}>
                        <LandingTable status={id} description={description} currentTab={{key}}/>
                    </Tab>
                ))}
            </Tabs>
            <PaginationTable
                currentTab={key}
            />
        </div>
    );
};
