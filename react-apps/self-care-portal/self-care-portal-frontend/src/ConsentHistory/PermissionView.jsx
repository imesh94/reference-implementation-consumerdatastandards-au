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

import React, { useState } from "react";
import {PermissionItem} from "../detailedAgreementPage";

export const PermissionView = ({ AmendedPermissions }) => {

    var id = 0;
    const [readMore,setReadMore] = useState(false);
    const permissionsExpanded = AmendedPermissions.slice(1, AmendedPermissions.length + 1);

    const linkName=readMore?'Show Less':'Show More'

    return (
        <div>
            <div className="dataClusters">
                <PermissionItem permissionScope={AmendedPermissions[0]} />
                {readMore &&  permissionsExpanded.map((permission) => (
                        <PermissionItem permissionScope={permission} key={(id = id + 1)} />
                    ))}
                {
                    permissionsExpanded.length > 0 ?
                        (<a className="read-more-link" onClick={()=>{setReadMore(!readMore)}}>
                        <span>{linkName}</span> </a>) : (<div/>)
                }
            </div>
        </div>

    );
};
