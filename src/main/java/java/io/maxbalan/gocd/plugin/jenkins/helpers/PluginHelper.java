package java.io.maxbalan.gocd.plugin.jenkins.helpers;

import java.util.Map;

import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

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
