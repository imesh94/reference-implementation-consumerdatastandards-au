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

# command to execute
# ./configure.sh <WSO2_OB_BI_HOME>

source $(pwd)/../repository/conf/configure.properties
WSO2_OB_BI_HOME=$1

# set accelerator home
cd ../
ACCELERATOR_HOME=$(pwd)
echo "Accelerator Home: ${ACCELERATOR_HOME}"

# set product home
if [ "${WSO2_OB_BI_HOME}" == "" ]
  then
    cd ../
    WSO2_OB_BI_HOME=$(pwd)
    echo "Product Home: ${WSO2_OB_BI_HOME}"
fi

# validate product home
if [ ! -d "${WSO2_OB_BI_HOME}/deployment/siddhi-files" ]; then
  echo -e "\n\aERROR:specified product path is not a valid carbon product path\n";
  exit 2;
else
  echo -e "\nValid carbon product path.\n";
fi

# read CDSAvailabilityMetricsApp.siddhi file
AVAILABILITY_METRICS_APP=${WSO2_OB_BI_HOME}/deployment/siddhi-files/CDSAvailabilityMetricsApp.siddhi;
AGGREGATED_AVAILABILITY_METRICS_APP=${WSO2_OB_BI_HOME}/deployment/siddhi-files/CDSAvailabilityMetricsAggregationApp.siddhi;
AGGREGATED_PEAK_TPS_METRICS_APP=${WSO2_OB_BI_HOME}/deployment/siddhi-files/CDSPeakTPSMetricsAggregationApp.siddhi;

echo -e "\nReplace hostnames \n"
echo -e "================================================\n"
sed -i -e 's|<BI_HOSTNAME>|'${BI_HOSTNAME}'|g' ${AVAILABILITY_METRICS_APP}
sed -i -e 's|<BI_HOSTNAME>|'${BI_HOSTNAME}'|g' ${AGGREGATED_AVAILABILITY_METRICS_APP}
sed -i -e 's|<BI_HOSTNAME>|'${BI_HOSTNAME}'|g' ${AGGREGATED_PEAK_TPS_METRICS_APP}
