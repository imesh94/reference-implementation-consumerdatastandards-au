#!/bin/bash
# ------------------------------------------------------------------------
#
# Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 LLC. and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
# You may not alter or remove any copyright or other notice from copies of this content.
#
# ------------------------------------------------------------------------

# merge.sh script copy the WSO2 OB IS CDS Toolkit artifacts on top of WSO2 IS base product
#
# merge.sh <WSO2_OB_IS_HOME>

WSO2_OB_IS_HOME=$1

# set toolkit home
cd ../
TOOLKIT_HOME=$(pwd)
echo "Toolkit home is: ${TOOLKIT_HOME}"

# set product home
if [ "${WSO2_OB_IS_HOME}" == "" ];
  then
    cd ../
    WSO2_OB_IS_HOME=$(pwd)
    echo "Product home is: ${WSO2_OB_IS_HOME}"
fi

# validate product home
if [ ! -d "${WSO2_OB_IS_HOME}/repository/components" ]; then
  echo -e "\n\aERROR:specified product path is not a valid carbon product path\n";
  exit 2;
else
  echo -e "\nValid carbon product path.\n";
fi

echo -e "\nRemoving old open banking artifacts from the base product\n"
echo -e "================================================\n"
find "${WSO2_OB_IS_HOME}"/repository/components/dropins -name "com.wso2.openbanking.cds.*" -exec rm -rf {} \;
find "${WSO2_OB_IS_HOME}"/repository/components/lib -name "com.wso2.openbanking.cds.*" -exec rm -rf {} \;
find "${WSO2_OB_IS_HOME}"/repository/deployment/server/webapps -name "api#openbanking#account-type-mgt.war" -exec rm -rf {} \;
find "${WSO2_OB_IS_HOME}"/repository/deployment/server/webapps -name "consentmgr.war" -exec rm -rf {} \;

echo -e "\nCopying open banking artifacts\n"
echo -e "================================================\n"
cp -r ${TOOLKIT_HOME}/carbon-home/* "${WSO2_OB_IS_HOME}"/
echo -e "\nComplete!\n"
