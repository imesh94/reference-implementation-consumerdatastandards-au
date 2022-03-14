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

import React from 'react'
import { common } from '../specConfigs'
import '../css/Footer.css'
import Container from 'react-bootstrap/Col'
import Row from 'react-bootstrap/Row'
import Col from 'react-bootstrap/Col'

export const Footer = () => {

    return(
        <Container className = "Footer">
            <Col className = "footerCol">
                <Row className = "footerText"><a href = "#" className = "complaintText">{common.complaintHandleLinkText}</a></Row>
                <Row className = "footerText">{common.footerContent}</Row>
            </Col>
        </Container>
    )
}
