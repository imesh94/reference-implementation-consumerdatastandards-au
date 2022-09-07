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
import {Container, Row} from "react-bootstrap";
import "../css/Body.css";

import {LandingTabs} from "../landing_page";
import {AdvanceSearch} from "./AdvanceSearch";

export const Body = () => {
    return (
        <Container className="boxContainer">
            <div className="box">
                <Row className="infoSearchRow">
                    <div className="searchBox" style={{width: "100%"}}>
                        <AdvanceSearch/>
                    </div>
                </Row>
                <LandingTabs/>
            </div>
        </Container>
    );
};