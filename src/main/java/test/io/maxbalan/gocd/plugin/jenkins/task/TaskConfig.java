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

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
public class TaskConfig {
    private final String url;
    private final String jobName;
    private final String jobToken;
    private final String username;
    private final String password;
    private final boolean printLog;
    private final Map<String, String> params;

    public TaskConfig(String url,
                      String jobName,
                      String jobToken,
                      String username,
                      String password,
                      boolean printLog,
                      Map<String, String> params) {
        this.url = url;
        this.jobName = jobName;
        this.jobToken = jobToken;
        this.username = username;
        this.password = password;
        this.printLog = printLog;
        this.params = params;
    }

    public String getUrl() {
        return this.url;
    }

    public String getJobName() {
        return this.jobName;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isPrintLog() {
        return this.printLog;
    }

    public String getJobToken() {
        return this.jobToken;
    }

    public boolean isTokenAuthentication() {
        return StringUtils.isNotBlank(this.jobToken);
    }

    public Map<String, String> getParams() {
        final HashMap<String, String> map = new HashMap<>(this.params);
        if (this.isTokenAuthentication()) {
            map.put("token", this.jobToken);
        }

        return map;
    }
}
