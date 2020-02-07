package groovy.io.maxbalan.gocd.plugin.jenkins.helpers

import static java.util.Collections.emptyMap

import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse

/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
trait PluginHelper implements GsonHelper {
    public static <T> GoPluginApiResponse successResponse(final T body) {
        return DefaultGoPluginApiResponse.success(toGson(body))
    }

    public static <T> GoPluginApiResponse errorResponse(final T body) {
        return DefaultGoPluginApiResponse.error(toGson(body))
    }

    public static Map<String, Object> createField(String displayName,
                                                  boolean isRequired,
                                                  boolean isSecure,
                                                  String displayOrder) {
        Map<String, Object> field = new HashMap<>()
        field.put("display-name", displayName)
        field.put("required", isRequired)
        field.put("secure", isSecure)
        field.put("display-order", displayOrder)
        return field
    }

    public static String getOrEmpty(Map map, String key) {
       (map.getOrDefault(key, emptyMap()) as Map).getOrDefault("value", "")
    }

    public static String replaceWithEnv(String str, Map<String, String> env) {
        for (Map.Entry<String, String> entry : env.entrySet()) {
            String key = entry.getKey()
            if (str.contains("\${" + key + "}"))
                str = str.replace("\${" + key + "}", entry.getValue())
            if (str.contains("\$" + key))
                str = str.replace("\$" + key, entry.getValue())
        }
        return str
    }
}
