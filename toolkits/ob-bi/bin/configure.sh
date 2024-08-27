#!/bin/bash
# ------------------------------------------------------------------------
#
# Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
#
# WSO2 LLC. licenses this file to you under the Apache License,
# Version 2.0 (the "License"); you may not use this file except
# in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
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

# read CDSCurrentPeakTPSApp.siddhi file
CURRENT_PEAK_TPS_APP=${WSO2_OB_BI_HOME}/deployment/siddhi-files/CDSCurrentPeakTPSApp.siddhi;

# read CDSAuthorisationMetricsApp.siddhi file
AUTHORISATION_METRICS_APP=${WSO2_OB_BI_HOME}/deployment/siddhi-files/CDSAuthorisationMetricsApp.siddhi;

echo -e "\nReplace hostnames \n"
echo -e "================================================\n"
sed -i -e 's|<BI_HOSTNAME>|'${BI_HOSTNAME}'|g' ${AVAILABILITY_METRICS_APP}
sed -i -e 's|<BI_HOSTNAME>|'${BI_HOSTNAME}'|g' ${CURRENT_PEAK_TPS_APP}
sed -i -e 's|<BI_HOSTNAME>|'${BI_HOSTNAME}'|g' ${AUTHORISATION_METRICS_APP}
