<!--
 ~  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 ~   
 ~  This software is the property of WSO2 Inc. and its suppliers, if any. 
 ~  Dissemination of any information or reproduction of any material contained 
 ~  herein is strictly forbidden, unless permitted by WSO2 in accordance with 
 ~  the WSO2 Commercial License available at http://wso2.com/licenses. 
 ~  For specific language governing the permissions and limitations under 
 ~  this license, please see the license as well as any agreement you’ve 
 ~  entered into with WSO2 governing the purchase of this software and any 
 ~  associated services.
-->

# WSO2 OB Compliance Toolkit cds

WSO2 OB Compliance Toolkit CDS provides the Toolkit Implementation for CDS Specification

### Building from the source

If you want to build WSO2 OB Compliance Toolkit CDS from the source code:

1. Install Java8 or above.
1. Install [Apache Maven 3.0.5](https://maven.apache.org/download.cgi) or above.
1. Install [MySQL](https://dev.mysql.com/doc/refman/5.5/en/windows-installation.html).
1. To get the WSO2 OB Compliance Toolkit CDS from [this repository](https://github.com/wso2-enterprise/ob-compliance-toolkit-cds.git), click **Clone or download**.
    * To **clone the solution**, copy the URL and execute the following command in a command prompt.
      `git clone <the copiedURL>`
    * To **download the pack**, click **Download ZIP** and unzip the downloaded file.
1. Navigate to the downloaded solution using a command prompt and run the Maven command.

   |  Command | Description |
         | :--- |:--- |
   | ```mvn install``` | This starts building the pack without cleaning the folders. |
   | ```mvn clean install``` | This cleans the folders and starts building the solution pack from scratch. |
   | ```mvn clean install -P solution``` | This cleans the folders and starts building the solution pack from scratch. Finally creates the toolkit zip files containing the artifacts required to setup the toolkit. |

1. Once the build is created, navigate to the relevant folder to get the toolkit for each product.

|  Product | Toolkit Path |
      | :--- |:--- |
| ```Identity Server``` | `/ob-compliance-toolkit-cds/toolkits/ob-is/target` |
| ```API Manager``` | `/ob-compliance-toolkit-cds/toolkits/ob-apim/target` |


### Running the products

Please refer the following READ.ME files to run the products.

|  Product | Instructions Path |
| :--- |:--- |
| ```Identity Server``` | `/wso2ob-is-toolkit-cds-1.0.0-RC/README.md` |
| ```API Manager``` | `/wso2ob-apim-toolkit-cds-1.0.0-RC/README.md` |


### Reporting Issues

We encourage you to report issues, documentation faults, and feature requests regarding the WSO2 OB Compliance Toolkit CDS through the [WSO2 OB Compliance Toolkit CDS Issue Tracker](https://github.com/wso2-enterprise/ob-compliance-toolkit-cds/issues).

### License

WSO2 Inc. licenses this source under the WSO2 Software License ([LICENSE](LICENSE)).
