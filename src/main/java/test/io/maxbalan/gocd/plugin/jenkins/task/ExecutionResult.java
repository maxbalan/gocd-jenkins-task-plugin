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

package test.io.maxbalan.gocd.plugin.jenkins.task;


import java.util.HashMap;
import java.util.Map;

/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
public class ExecutionResult {
    private boolean success;
    private String message;
    private Throwable exception;

    public ExecutionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ExecutionResult(boolean success, String message, Throwable exception) {
        this(success, message);
        this.exception = exception;
    }

    public Map toMap() {
        final Map<String, Object> result = new HashMap<>();
        result.put("success", this.success);
        result.put("message", this.message);
        result.put("exception", this.exception);
        return result;
    }

    public boolean isSuccess() {
        return this.success;
    }
}
