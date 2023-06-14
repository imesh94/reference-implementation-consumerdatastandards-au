#!/bin/bash
# ------------------------------------------------------------------------
#
# Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 Inc. and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein is strictly forbidden, unless permitted by WSO2 in accordance with
# the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
# language governing the permissions and limitations under this license,
# please see the license as well as any agreement you’ve entered into with
# WSO2 governing the purchase of this software and any associated services.
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

echo -e "\nRemoving old open banking artifacts from base product\n"
find "${WSO2_OB_IS_HOME}"/repository/components/dropins -name "com.wso2.openbanking.cds.*" -exec rm -rf {} \;
find "${WSO2_OB_IS_HOME}"/repository/components/lib -name "com.wso2.openbanking.cds.*" -exec rm -rf {} \;

echo -e "\nCopying open banking artifacts\n"
echo -e "================================================\n"
cp -r ${TOOLKIT_HOME}/carbon-home/* "${WSO2_OB_IS_HOME}"/
echo -e "\nComplete!\n"
