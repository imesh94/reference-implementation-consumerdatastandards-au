/**
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.openbanking.cds.metrics.util;

/**
 * Enum for Authenticated and Unauthenticated aspects.
 */
public enum AspectEnum {

    AUTHENTICATED("authenticated"),
    UNAUTHENTICATED("unauthenticated"),
    ALL("all");

    private String value;

    AspectEnum(String value) {
        this.value = value;
    }

    public static AspectEnum fromValue(String text) {

        for (AspectEnum b : AspectEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    public String toString() {
        return String.valueOf(value);
    }

}
