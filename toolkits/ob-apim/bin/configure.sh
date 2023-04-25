#!/bin/bash
# ------------------------------------------------------------------------
#
# Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 LLC. and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
# You may not alter or remove any copyright or other notice from copies of this content.
# ------------------------------------------------------------------------

# command to execute
# ./merge.sh <WSO2_OB_APIM_HOME>

source $(pwd)/../repository/conf/configure.properties
WSO2_OB_APIM_HOME=$1

# set accelerator home
cd ../
ACCELERATOR_HOME=$(pwd)
echo "Accelerator Home: ${ACCELERATOR_HOME}"

# set product home
if [ "${WSO2_OB_APIM_HOME}" == "" ]
  then
    cd ../
    WSO2_OB_APIM_HOME=$(pwd)
    echo "Product Home: ${WSO2_OB_APIM_HOME}"
fi

# validate product home
if [ ! -d "${WSO2_OB_APIM_HOME}/repository/components" ]; then
  echo -e "\n\aERROR:specified product path is not a valid carbon product path\n";
  exit 2;
else
  echo -e "\nValid carbon product path.\n";
fi

# read deployment.toml file
DEPLOYMENT_TOML_FILE=${ACCELERATOR_HOME}/repository/resources/deployment.toml;
cp ${ACCELERATOR_HOME}/${PRODUCT_CONF_PATH} ${DEPLOYMENT_TOML_FILE};

# read open-banking-cds.xml file
OPENBANKING_CDS_XML_FILE=${WSO2_OB_APIM_HOME}/repository/conf/open-banking-cds.xml;

# read main.xml file
MAIN_ERROR_SEQUENCE_FILE=${WSO2_OB_APIM_HOME}/repository/deployment/server/synapse-configs/default/sequences/main.xml

configure_datasources() {
    if [ "${DB_TYPE}" == "mysql" ]
        then
            # APIM
            sed -i -e 's|DB_APIMGT_URL|jdbc:mysql://'${DB_HOST}':3306/'${DB_APIMGT}'?autoReconnect=true\&amp;useSSL=false|g' ${DEPLOYMENT_TOML_FILE}
            sed -i -e 's|DB_AM_CONFIG_URL|jdbc:mysql://'${DB_HOST}':3306/'${DB_AM_CONFIG}'?autoReconnect=true\&amp;useSSL=false|g' ${DEPLOYMENT_TOML_FILE}
            sed -i -e 's|DB_GOV_URL|jdbc:mysql://'${DB_HOST}':3306/'${DB_GOV}'?autoReconnect=true\&amp;useSSL=false|g' ${DEPLOYMENT_TOML_FILE}
            sed -i -e 's|DB_USER_STORE_URL|jdbc:mysql://'${DB_HOST}':3306/'${DB_USER_STORE}'?autoReconnect=true\&amp;useSSL=false|g' ${DEPLOYMENT_TOML_FILE}
            sed -i -e 's|DB_USER|'${DB_USER}'|g' ${DEPLOYMENT_TOML_FILE}
            sed -i -e 's|DB_PASS|'${DB_PASS}'|g' ${DEPLOYMENT_TOML_FILE}
            sed -i -e 's|DB_DRIVER|'${DB_DRIVER}'|g' ${DEPLOYMENT_TOML_FILE}

        else
            # IS
            sed -i -e 's|DB_APIMGT_URL|jdbc:sqlserver://'${DB_HOST}':1433;databaseName='${DB_APIMGT}';encrypt=false|g' ${DEPLOYMENT_TOML_FILE}
            sed -i -e 's|DB_AM_CONFIG_URL|jdbc:sqlserver://'${DB_HOST}':1433;databaseName='${DB_AM_CONFIG}';encrypt=false|g' ${DEPLOYMENT_TOML_FILE}
            sed -i -e 's|DB_GOV_URL|jdbc:sqlserver://'${DB_HOST}':1433;databaseName='${DB_GOV}';encrypt=false|g' ${DEPLOYMENT_TOML_FILE}
            sed -i -e 's|DB_USER_STORE_URL|jdbc:sqlserver://'${DB_HOST}':1433;databaseName='${DB_USER_STORE}';encrypt=false|g' ${DEPLOYMENT_TOML_FILE}
            sed -i -e 's|DB_USER|'${DB_USER}'|g' ${DEPLOYMENT_TOML_FILE}
            sed -i -e 's|DB_PASS|'${DB_PASS}'|g' ${DEPLOYMENT_TOML_FILE}
            sed -i -e 's|DB_DRIVER|'${DB_DRIVER}'|g' ${DEPLOYMENT_TOML_FILE}
    fi
}

configure_error_sequences() {
  sed -i -e 's|{"code":"404","type":"Status report","message":"Not Found","description":"The requested resource is not available."}|\
{"errors":[{"code": "urn:au-cds:error:cds-all:Resource/NotFound","title": "Resource Not Found"}]}|' ${MAIN_ERROR_SEQUENCE_FILE}
}

echo -e "\nReplace hostnames \n"
echo -e "================================================\n"
sed -i -e 's|APIM_HOSTNAME|'${APIM_HOSTNAME}'|g' ${DEPLOYMENT_TOML_FILE}
sed -i -e 's|IS_HOSTNAME|'${IS_HOSTNAME}'|g' ${DEPLOYMENT_TOML_FILE}
sed -i -e 's|BI_HOSTNAME|'${BI_HOSTNAME}'|g' ${DEPLOYMENT_TOML_FILE}

sed -i -e 's|APIM_HOSTNAME|'${APIM_HOSTNAME}'|g' ${OPENBANKING_CDS_XML_FILE}

echo -e "\nConfigure datasources \n"
echo -e "================================================\n"
configure_datasources;

echo -e "\nConfigure cds error sequences \n"
echo -e "================================================\n"
configure_error_sequences;

echo -e "\nCopy deployment.toml file to repository/conf \n"
echo -e "================================================\n"
cp ${DEPLOYMENT_TOML_FILE} ${WSO2_OB_APIM_HOME}/repository/conf/
rm ${DEPLOYMENT_TOML_FILE}
rm ${DEPLOYMENT_TOML_FILE}-e
