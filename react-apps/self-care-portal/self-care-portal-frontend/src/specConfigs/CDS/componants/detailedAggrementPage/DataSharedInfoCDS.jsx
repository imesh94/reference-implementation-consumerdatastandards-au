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
import {specConfigurations} from "../../../specConfigurations";
import {PermissionItem} from "../../../../detailedAgreementPage";
import {getValueFromConsent} from "../../../../services";
import {permissionBindTypes} from "../../../common";

let id = 0;
export const DataSharedInfoCDS = ({consent, infoLabels}) => {

    let permissions = [];
    if (specConfigurations.consent.permissionsView.permissionBindType ===
        permissionBindTypes.samePermissionSetForAllAccounts) {
        permissions = getValueFromConsent(
            specConfigurations.consent.permissionsView.permissionsAttribute, consent)
        permissions = filterProfilePermissions(permissions);
        if (permissions === "" || permissions === undefined) {
            permissions = [];
        }
    } else {
        permissions = {};
        let detailedAccountsList = getValueFromConsent("consentMappingResources", consent);
        detailedAccountsList.map((detailedAccount) => {
            if (permissions[detailedAccount.accountId] === undefined) {
                permissions[detailedAccount.accountId] = []
                permissions[detailedAccount.accountId].push(detailedAccount.permission)
            } else {
                permissions[detailedAccount.accountId].push(detailedAccount.permission)
            }
        })
    }
    return (
        <div className="dataSharedBody">
            <h5>{infoLabels.dataSharedLabel}</h5>
            {specConfigurations.consent.permissionsView.permissionBindType ===
            permissionBindTypes.differentPermissionsForEachAccount ?
                (
                    Object.keys(permissions).map((account) => {
                        return <>
                            <h5>Account : {account}</h5>
                            <div className="dataClusters">
                                {permissions[account].map((permission) => (
                                    <PermissionItem permissionScope={permission} key={id = id + 1}/>
                                ))}
                            </div>
                        </>
                    })
                ) : (
                    <div className="dataClusters">
                        {permissions.map((permission) => (
                            <PermissionItem permissionScope={permission} key={id = id + 1}/>
                        ))}
                    </div>
                )
            }
        </div>
    );

    function filterProfilePermissions(permissions) {
        const profilePermissions = [
            {
                name: "NAME",
                scopes: ["PROFILE", "NAME", "GIVENNAME", "FAMILYNAME", "UPDATEDAT"]
            }, {
                name: "EMAIL",
                scopes: ["EMAIL", "EMAILVERIFIED"]
            }, {
                name: "MAIL",
                scopes: ["ADDRESS"]
            }, {
                name: "PHONE",
                scopes: ["PHONENUMBER", "PHONENUMBERVERIFIED"]
            }
        ]
        let updatedProfilePermissions = [];

        for (let index = permissions.length - 1; index >= 0; index--) {
            const element = permissions[index];

            for (let i=0; i<profilePermissions.length; i++) {
                let name = profilePermissions[i].name;
                let scopes = profilePermissions[i].scopes;
                processPermissions(name, scopes, element, index, permissions, updatedProfilePermissions);
            }
        }
        updatedProfilePermissions.sort();

        let contactPermission = "";

        for (let i=0; i<updatedProfilePermissions.length; i++) {
            let permission = updatedProfilePermissions[i];
            if ("NAME" === permission) {
                permissions.push("NAME");
            } else {
                contactPermission = contactPermission.concat("_", permission);
            }
        }
        if (contactPermission !== "") {
            permissions.push(contactPermission.slice(1));
        }

        return permissions;
    }

    function processPermissions(name, scopes, element, index, permissions, updatedProfilePermissions) {
        if (scopes.includes(element)) {

            permissions.splice(index, 1);
            if (!updatedProfilePermissions.includes(name)) {
                updatedProfilePermissions.push(name)
            }
        }
    }
};
