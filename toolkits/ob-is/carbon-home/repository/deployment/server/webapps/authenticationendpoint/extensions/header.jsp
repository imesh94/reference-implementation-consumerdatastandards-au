<%--
  ~ Copyright (c) 2022, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.

  ~ This software is the property of WSO2 LLC. and its suppliers, if any.
  ~ Dissemination of any information or reproduction of any material contained
  ~ herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
  ~ You may not alter or remove any copyright or other notice from copies of this content.
--%>

<!-- localize.jsp MUST already be included in the calling script -->
<%@include file="../includes/localize.jsp" %>
<%@include file="../includes/init-url.jsp" %>
<%@ page import="org.wso2.carbon.identity.application.authentication.endpoint.util.AuthenticationEndpointUtil" %>

<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="icon" href="libs/theme/assets/images/favicon.ico" type="image/x-icon"/>
<link href="libs/theme/wso2-default.min.css" rel="stylesheet">
<link href="libs/themes/default/theme.min.css" rel="stylesheet">
<title><%=AuthenticationEndpointUtil.i18n(resourceBundle, "wso2.identity.server")%></title>


<style>

    .disclaimer {
	font-size: large;
    }

    body {
        flex-direction: column;
        display: flex;
        color: #ffffff;
        background: #efefef;
        font-family: "Open Sans", "Helvetica", "Arial", sans-serif;
    }

    main.center-segment {
        margin: auto;
        display: flex;
        align-items: center;
    }

    main.center-segment > .ui.container.medium {
        max-width: 450px !important;
    }

    main.center-segment > .ui.container.large {
        max-width: 700px !important;
    }

    main.center-segment > .ui.container > .ui.segment {
        background-image: linear-gradient(to bottom, #1a1f28 0%,#2e3b41 100%);
        background-image: url(extensions/images/login-back.svg), linear-gradient(to bottom, #1a1f28 0%,#2e3b41 100%);
        background-repeat: no-repeat;
        background-position: left bottom;
        background-size: contain;
        border: 1px solid #000;
        border-radius: 10px;
        color: '#fff';
        padding: 3rem;
    }

    .login-portal.layout .center-segment > .ui.container > .ui.segment {
    	padding: 3rem;
    	border-radius: 10px;
    }

    main.center-segment > .ui.container > .ui.segment .segment-form .buttons {
        margin-top: 1em;
    }

    main.center-segment > .ui.container > .ui.segment .segment-form .buttons.align-right button,
    main.center-segment > .ui.container > .ui.segment .segment-form .buttons.align-right input {
        margin: 0 0 0 0.25em;
	background: #171771;
        color: #ffffff;
    }

    main.center-segment > .ui.container > .ui.segment .segment-form .column .buttons.align-left button.link-button,
    main.center-segment > .ui.container > .ui.segment .segment-form .column .buttons.align-left input.link-button {
        padding: .78571429em 1.5em .78571429em 0;
	color: #ffffff;
    }

    main.center-segment > .ui.container > .ui.segment .segment-form {
        text-align: left;
    }

    main.center-segment > .ui.container > .ui.segment .segment-form .align-center {
        text-align: center;
    }

    main.center-segment > .ui.container > .ui.segment .segment-form .align-right {
        text-align: right;
    }

    .ui.header {
        color: #efefef;
    }

    footer {
        padding: 2rem 0;
        color:rgb(22, 20, 20);
    }

    .ui.large.form {
        font-size: 12px;
    }

    .ui.button.link-button {
    background: 0 0 !important;
    color: #ffffff;
    }

    .ui.primary.button:hover, .ui.primary.buttons .button:hover {
    background-color: #171771;
    color: #ffffff;
    text-shadow: none;
    }

    .ui.primary.button, .ui.primary.buttons {
    background-color: #171771;
    color: #ffffff;
    text-shadow: none;
    }

    .ui.checkbox label:hover, .ui.checkbox + label:hover {
    color: #ffffff;;
    }

    .ui.checkbox label, .ui.checkbox + label {
    color: #ffffff;;
    }

    a:hover {
    color: #2ab9e5;
    text-decoration: underline;
    }

    a {
    color: #2ab9e5;
    text-decoration: none;
}
</style>

<script src="libs/jquery_3.6.0/jquery-3.6.0.js"></script>
