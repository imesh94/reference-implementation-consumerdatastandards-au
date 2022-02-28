/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

import {CONFIG} from "../config";
import {lang_CDS, specConfigurations_CDS} from "./CDS/specConfigurations_CDS";
import {lang_UK, specConfigurations_UK} from "./UK/specConfigurations_UK";
import {lang_BG, specConfigurations_BG} from "./BG/specConfigurations_BG";
import {lang_Default, specConfigurations_Default} from "./Default/specConfigurations_Default";

export let specConfigurations
export let lang

let spec = CONFIG.SPEC;

// common spec related configs
if (spec === "Default") {
    specConfigurations = specConfigurations_Default;
    lang = lang_Default;
}

if (spec === "CDS") {
    specConfigurations = specConfigurations_CDS;
    lang = lang_CDS;
}

if (spec === "UK") {
    specConfigurations = specConfigurations_UK;
    lang = lang_UK;
}

if (spec === "BG") {
    specConfigurations = specConfigurations_BG;
    lang = lang_BG;
}