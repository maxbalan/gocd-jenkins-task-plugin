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


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
public class TaskContext {
    private final Map environmentVariables;
    private final String workingDir;

    public TaskContext(Map context) {
        this.environmentVariables = new HashMap((Map) context.getOrDefault("environmentVariables", Collections.EMPTY_MAP));
        this.workingDir = (String) context.getOrDefault("workingDirectory", "");
    }

    public Map getEnvironmentVariables() {
        return new HashMap(this.environmentVariables);
    }

    public String getWorkingDir() {
        return this.workingDir;
    }

}
