/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.identity.push.auth.extension.request.validator.util;

public class CDSPushAuthRequestValidatorTestData {

    public static final String VALID_SIGNED_JWT = "eyJraWQiOiJXX1RjblFWY0hBeTIwcTh6Q01jZEJ5cm9vdHciLCJhbGciOiJQUz" +
            "I1NiJ9.eyJhdWQiOiJodHRwczovL2xvY2FsaG9zdDo5NDQ2L29hdXRoMi90b2tlbiIsIm1heF9hZ2UiOjg2NDAwLCJzY29wZSI6I" +
            "mJhbms6YWNjb3VudHMuYmFzaWM6cmVhZCBiYW5rOmFjY291bnRzLmRldGFpbDpyZWFkIGJhbms6dHJhbnNhY3Rpb25zOnJlYWQgY" +
            "mFuazpwYXllZXM6cmVhZCBiYW5rOnJlZ3VsYXJfcGF5bWVudHM6cmVhZCBjb21tb246Y3VzdG9tZXIuYmFzaWM6cmVhZCBjb21tb" +
            "246Y3VzdG9tZXIuZGV0YWlsOnJlYWQgY2RyOnJlZ2lzdHJhdGlvbiBvcGVuaWQiLCJleHAiOjE5NTQ3MDg3MTAsImNsYWltcyI6e" +
            "yJzaGFyaW5nX2R1cmF0aW9uIjo3Nzc2MDAwLCJjZHJfYXJyYW5nZW1lbnRfaWQiOiIwMmU3YzlkOS1jZmU3LTRjM2UtOGY2NC1lO" +
            "TExNzNjODRlY2IiLCJpZF90b2tlbiI6eyJhY3IiOnsidmFsdWVzIjpbInVybjpjZHMuYXU6Y2RyOjIiXSwiZXNzZW50aWFsIjp0c" +
            "nVlfX0sInVzZXJpbmZvIjp7ImdpdmVuX25hbWUiOm51bGwsImZhbWlseV9uYW1lIjpudWxsfX0sImlzcyI6IndIS0g2amQ1WVJKd" +
            "EdfQ1hTTFZmY1N0TWZPQWEiLCJyZXNwb25zZV90eXBlIjoiY29kZSIsInJlc3BvbnNlX21vZGUiOiJqd3QiLCJyZWRpcmVjdF91c" +
            "mkiOiJodHRwczovL3d3dy5nb29nbGUuY29tL3JlZGlyZWN0cy9yZWRpcmVjdDEiLCJzdGF0ZSI6IjBwTjBOQlRIY3YiLCJub25jZ" +
            "SI6Im4takJYaE9tT0tDQiIsImNsaWVudF9pZCI6IndIS0g2amQ1WVJKdEdfQ1hTTFZmY1N0TWZPQWEifQ.TnzCaUDz3qLUcFsUX4" +
            "K_CmDi9JNKHkhfzNaa2_NR9T9W71KX_olM48eTEcdYmn7O63zMn8x2SP9p3KSiNMTYPOdIPTWsheu1Z11RYhX18Gjxp8K9OXVu5B" +
            "QY9vqubmqzodcKSKGn_aK2yM9WnowXW5jmhwH2NTXgQYE7Gdh8AN-Yy_8wDaVQmaBrkiTrDWUQyvpTw4OzPk93VhzMrLvz3XYoF2" +
            "8TcCLuPnTsATWyZegkl60JvNuIQSQw_Wm7dLiNxnS4ad4xgM8CM8uXjHvnIKAXYPvj-m5JmGPpgTUm6yZgbY2LUfsABZnjpk7hmA" +
            "-hISYt2TX9orbNmTbyVYnxnQ";

    public static final String INVALID_SIGNED_JWT = "eyJraWQiOiJXX1RjblFWY0hBeTIwcTh6Q01jZEJ5cm9vdHciLCJhbGciOiJ" +
            "QUzI1NiJ9.eyJhdWQiOiJodHRwczovL2xvY2FsaG9zdDo5NDQ2L29hdXRoMi90b2tlbiIsIm1heF9hZ2UiOjg2NDAwLCJzY29wZ" +
            "SI6ImJhbms6YWNjb3VudHMuYmFzaWM6cmVhZCBiYW5rOmFjY291bnRzLmRldGFpbDpyZWFkIGJhbms6dHJhbnNhY3Rpb25zOnJl" +
            "YWQgYmFuazpwYXllZXM6cmVhZCBiYW5rOnJlZ3VsYXJfcGF5bWVudHM6cmVhZCBjb21tb246Y3VzdG9tZXIuYmFzaWM6cmVhZCB" +
            "jb21tb246Y3VzdG9tZXIuZGV0YWlsOnJlYWQgY2RyOnJlZ2lzdHJhdGlvbiBvcGVuaWQiLCJleHAiOjE5NTQ3MDg3MTAsImNsYW" +
            "ltcyI6eyJzaGFyaW5nX2R1cmF0aW9uIjotMSwiY2RyX2FycmFuZ2VtZW50X2lkIjoiMDJlN2M5ZDktY2ZlNy00YzNlLThmNjQtZ" +
            "TkxMTczYzg0ZWNiIiwiaWRfdG9rZW4iOnsiYWNyIjp7InZhbHVlcyI6WyJ1cm46Y2RzLmF1OmNkcjoyIl0sImVzc2VudGlhbCI6" +
            "dHJ1ZX19LCJ1c2VyaW5mbyI6eyJnaXZlbl9uYW1lIjpudWxsLCJmYW1pbHlfbmFtZSI6bnVsbH19LCJpc3MiOiJ3SEtINmpkNVl" +
            "SSnRHX0NYU0xWZmNTdE1mT0FhIiwicmVzcG9uc2VfdHlwZSI6ImNvZGUgaWRfdG9rZW4iLCJyZWRpcmVjdF91cmkiOiJodHRwcz" +
            "ovL3d3dy5nb29nbGUuY29tL3JlZGlyZWN0cy9yZWRpcmVjdDEiLCJzdGF0ZSI6IjBwTjBOQlRIY3YiLCJub25jZSI6Im4takJYa" +
            "E9tT0tDQiIsInJlcXVlc3QiOiJpbnZsYWlkUGFyYW0iLCJjbGllbnRfaWQiOiJ3SEtINmpkNVlSSnRHX0NYU0xWZmNTdE1mT0Fh" +
            "In0.OvDY9UM95zefqAXHT45LjUvuCtMO6F2z4jj1FgQwL-lHV38bZeTYordRL2fac36VrW9iEUsBgLoXrM9ZlXmXZR1dagtDSbJ" +
            "vUMLSnsicMEiuykK2eyfgDZJvjzteQydNDKvYKDp_g1qkUjRbFIAAqI-tOKSG2qE2jiwkK-zJz2KD9yUEDJa52fR_47ugGmWyVY" +
            "dTeV4DbeQA_UXe1D18Q4TzMTef3VuL9yRWNvwARFdmYLHGNi9iuE6r2YPH8fcYj_N0Dwi0A3P9Owc0uaBfpz1vGTFqan4gGDlaa" +
            "wyb_xitHY5tdEGQE13b9mVoZ_kjBNDN16njNJtlc5KZYs78HA";
}
