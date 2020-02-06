/*
 * Copyright 2020 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.io.maxbalan.gocd.plugin.jenkins.helpers;


import java.util.Arrays;

/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 */
public enum RequestType {
    TASK_CONFIGURATION("configuration"),
    TASK_VIEW("view"),
    TASK_VALIDATE("validate"),
    TASK_EXECUTE("execute"),
    UNKNOWN("unknown");

    private String descriptor;

    private RequestType(String descriptor) {
        this.descriptor = descriptor;
    }

    public static RequestType fromString(final String value) {
        RequestType r = UNKNOWN;

        r = Arrays.stream(values())
                  .filter(t -> t.descriptor.equalsIgnoreCase(value))
                  .findFirst()
                  .orElse(UNKNOWN);

        return r;
    }

    public static boolean has(final String value) {
        RequestType r = UNKNOWN;

        r = Arrays.stream(values())
                  .filter(t -> t.descriptor.equalsIgnoreCase(value))
                  .findFirst()
                  .orElse(UNKNOWN);

        return r != UNKNOWN;
    }

    public String getDescriptor() {
        return this.descriptor;
    }

}
