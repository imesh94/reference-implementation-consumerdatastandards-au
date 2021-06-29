#!/bin/bash

#  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
#
#  This software is the property of WSO2 Inc. and its suppliers, if any.
#  Dissemination of any information or reproduction of any material contained
#  herein is strictly forbidden, unless permitted by WSO2 in accordance with
#  the WSO2 Software License available at https://wso2.com/licenses/eula/3.1.
#  For specific language governing the permissions and limitations under this
#  license, please see the license as well as any agreement you’ve entered into
#  with WSO2 governing the purchase of this software and any associated services.

set -o xtrace

HOME=`pwd`

function usage()
{
    echo "
    Run to stop the test server.
         "
}

SOURCE_HOME=`pwd`
RESOURCE_DIR=$SOURCE_HOME/../../resources/code_coverage
echo $RESOURCE_DIR

#=== FUNCTION ==================================================================
# NAME: get_prop
# DESCRIPTION: Retrieve specific property from server.properties file
# PARAMETER 1: property_value
#===============================================================================
function get_prop {
    local prop=$(grep -w "${1}" "${RESOURCE_DIR}/server.properties" | cut -d'=' -f2)
    echo $prop
}

SETUP_PROPERTIES_FILE=${RESOURCE_DIR}/server.properties

IAMSERVER_HOST=$(get_prop "IAMHostName")
IAMSERVER_IP=$(get_prop "IAMSeverIp")
IAMSERVER_SETUP_PATH=$(get_prop "IAMServerSetupPath")
AMSERVER_HOST=$(get_prop "AMHostName")
AMSERVER_IP=$(get_prop "AMSeverIp")
AMSERVER_SETUP_PATH=$(get_prop "AMServerSetupPath")
KEY_PATH=$(get_prop "ServerCertPath")
JAVA_HOME=$(get_prop "JavaHome")
waiting_time=20

ssh_to_server() {
    local serverIp=$1
    local command=$2
    ssh -i "${KEY_PATH}" "ubuntu@${serverIp}" "${command}"
}

if [ "$IAMSERVER_HOST" == "localhost" ] && [ "$AMSERVER_HOST" == "localhost" ]; then

#--------------Copy Coverage Dump from IAM Server-----------------#
    echo "Stop IAM Servers"
    cd "${IAMSERVER_SETUP_PATH}"/bin || exit
    JAVA_HOME=${JAVA_HOME} ./wso2server.sh stop

    echo "Waiting $waiting_time seconds"
    sleep $waiting_time

    echo "Zip jacoco folder"
    cd "${IAMSERVER_SETUP_PATH}"/repository/logs/jacoco || exit
    zip -r jacoco_iam.zip * || exit

    echo "Copy jacoco_iam.zip to the target"
    mkdir "${SOURCE_HOME}"/jacoco-stats
    cp "${IAMSERVER_SETUP_PATH}"/repository/logs/jacoco/jacoco_iam.zip "${SOURCE_HOME}"/jacoco-stats

    echo "Unzip jacoco_iam.zip file"
    cd "${SOURCE_HOME}"/jacoco-stats || exit
    unzip -d jacoco_iam jacoco_iam.zip

#--------------Copy Coverage Dump from AM Server-----------------#
    echo "Stop AM Servers"
    cd "${AMSERVER_SETUP_PATH}"/bin || exit
    JAVA_HOME=${JAVA_HOME} ./wso2server.sh stop

    echo "Waiting $waiting_time seconds"
    sleep $waiting_time

    echo "Zip jacoco folder"
    cd "${AMSERVER_SETUP_PATH}"/repository/logs/jacoco || exit
    zip -r jacoco_am.zip * || exit

    echo "Copy jacoco_am.zip to the target"
    cp "${AMSERVER_SETUP_PATH}"/repository/logs/jacoco/jacoco_am.zip "${SOURCE_HOME}"/jacoco-stats

    echo "Unzip jacoco_am.zip file"
    cd "${SOURCE_HOME}"/jacoco-stats || exit
    unzip -d jacoco_am jacoco_am.zip

else

#--------------Copy Coverage Dump from IAM Server-----------------#
    echo "Stop IAM Servers"
    ssh_to_server "${IAMSERVER_IP}" "sudo JAVA_HOME=${JAVA_HOME} ${IAMSERVER_SETUP_PATH}/bin/wso2server.sh stop"

    echo "Waiting $waiting_time seconds"
    sleep $waiting_time

    echo "Zip jacoco folder"
    ssh_to_server "${IAMSERVER_IP}" "cd ${IAMSERVER_SETUP_PATH}/repository/logs/jacoco; zip -r jacoco_iam.zip *"

    echo "Copy jacoco_iam.zip to the target"
    mkdir "${SOURCE_HOME}"/jacoco-stats
    scp -i "${KEY_PATH}" -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "ubuntu@${IAMSERVER_IP}:"${IAMSERVER_SETUP_PATH}/repository/logs/jacoco/jacoco_iam.zip ${SOURCE_HOME}/jacoco-stats""

    echo "Waiting $waiting_time seconds"
    sleep $waiting_time

    echo "Unzip jacoco_iam.zip file"
    cd "${SOURCE_HOME}"/jacoco-stats || exit
    unzip -d jacoco_iam jacoco_iam.zip

#--------------Copy Coverage Dump from AM Server-----------------#

    echo "Stop AM Servers"
    ssh_to_server "${AMSERVER_IP}" "sudo JAVA_HOME=${JAVA_HOME} ${AMSERVER_SETUP_PATH}/bin/wso2server.sh stop"

    echo "Waiting $waiting_time seconds"
    sleep $waiting_time

    echo "Zip jacoco folder"
    ssh_to_server "${AMSERVER_IP}" "cd ${AMSERVER_SETUP_PATH}/repository/logs/jacoco; zip -r jacoco_am.zip *"

    echo "Copy jacoco_am.zip to the target"
    mkdir "${SOURCE_HOME}"/jacoco-stats
    scp -i "${KEY_PATH}" -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "ubuntu@${AMSERVER_IP}:"${AMSERVER_SETUP_PATH}/repository/logs/jacoco/jacoco_am.zip ${SOURCE_HOME}/jacoco-stats""

    echo "Waiting $waiting_time seconds"
    sleep $waiting_time

    echo "Unzip jacoco_am.zip file"
    cd "${SOURCE_HOME}"/jacoco-stats || exit
    unzip -d jacoco_am jacoco_am.zip
fi
