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

import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Map;

/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
public class PluginHelper {
    public static <T> GoPluginApiResponse successResponse(final T body) {
        return DefaultGoPluginApiResponse.success(GsonHelper.toGson(body));
    }

    public static <T> GoPluginApiResponse errorResponse(final T body) {
        return DefaultGoPluginApiResponse.error(GsonHelper.toGson(body));
    }

    public static String processTemplate(String templateString, Map<String, String> bindings) {
        for (Map.Entry<String, String> entry : bindings.entrySet()) {
            String key = entry.getKey();
            if (templateString.contains("${" + key + "}")) {
                templateString = templateString.replace("${" + key + "}", entry.getValue());
            } else if (templateString.contains("$" + key)) {
                templateString = templateString.replace("$" + key, entry.getValue());
            }
        }
        return templateString;
    }
}
