package java.io.maxbalan.gocd.plugin.jenkins;

import static java.io.maxbalan.gocd.plugin.jenkins.helpers.ConfigParams.JenkinsAuthenticationPassword;
import static java.io.maxbalan.gocd.plugin.jenkins.helpers.ConfigParams.JenkinsAuthenticationUser;
import static java.io.maxbalan.gocd.plugin.jenkins.helpers.ConfigParams.JenkinsJobAuthenticationToken;
import static java.io.maxbalan.gocd.plugin.jenkins.helpers.ConfigParams.JenkinsJobName;
import static java.io.maxbalan.gocd.plugin.jenkins.helpers.ConfigParams.JenkinsServerUrl;
import static java.io.maxbalan.gocd.plugin.jenkins.helpers.ConfigParams.JobParameters;
import static java.io.maxbalan.gocd.plugin.jenkins.helpers.ConfigParams.LogPrint;
import static java.io.maxbalan.gocd.plugin.jenkins.helpers.GsonHelper.fromGson;
import static java.io.maxbalan.gocd.plugin.jenkins.helpers.PluginHelper.errorResponse;
import static java.io.maxbalan.gocd.plugin.jenkins.helpers.PluginHelper.processTemplate;
import static java.io.maxbalan.gocd.plugin.jenkins.helpers.PluginHelper.successResponse;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;

import java.io.maxbalan.gocd.plugin.jenkins.helpers.RequestType;
import java.io.maxbalan.gocd.plugin.jenkins.helpers.TaskExecutorFactory;
import java.io.maxbalan.gocd.plugin.jenkins.task.ExecutionResult;
import java.io.maxbalan.gocd.plugin.jenkins.task.TaskConfig;
import java.io.maxbalan.gocd.plugin.jenkins.task.TaskContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.thoughtworks.go.plugin.api.AbstractGoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
@Extension
public class JenkinsPlugin extends AbstractGoPlugin {
    public static final Logger LOG = Logger.getLoggerFor(JenkinsPlugin.class);
    private static final Pattern PARAMS_PATTERN = Pattern.compile("\\w+=(\\$(\\{\\w+}|\\w+)|\\w+)([,\\n]\\w+=(\\$(\\{\\w+}|\\w+)|\\w+))*");

    private final TaskExecutorFactory taskExecutorFactory;

    public JenkinsPlugin() {
        this(TaskExecutorFactory.getFactory());
    }

    JenkinsPlugin(TaskExecutorFactory taskExecutorFactory) {
        this.taskExecutorFactory = taskExecutorFactory;
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest requestMessage) throws UnhandledRequestTypeException {
        final String requestName = requestMessage.requestName();
        LOG.info("[Jenkins Plugin] Got request [{}], body: {}", requestName, requestMessage.requestBody());
        if (RequestType.TASK_CONFIGURATION.getDescriptor().equalsIgnoreCase(requestName)) {
            return handleGetConfigRequest();
        } else if (RequestType.TASK_VALIDATE.getDescriptor().equalsIgnoreCase(requestName)) {
            return handleValidation(requestMessage);
        } else if (RequestType.TASK_EXECUTE.getDescriptor().equalsIgnoreCase(requestName)) {
            return handleTaskExecution(requestMessage);
        } else if (RequestType.TASK_VIEW.getDescriptor().equalsIgnoreCase(requestName)) {
            return handleTaskView();
        }

        throw new UnhandledRequestTypeException(requestName);
    }

    private GoPluginApiResponse handleGetConfigRequest() {
        final Map<String, Object> configMap = new HashMap<>();
        configMap.put(JenkinsServerUrl.getDescriptor(), createField(JenkinsServerUrl.getDescriptor(), true, false, "0"));
        configMap.put(JenkinsJobName.getDescriptor(), createField(JenkinsJobName.getDescriptor(), true, false, "1"));
        configMap.put(JenkinsJobAuthenticationToken.getDescriptor(),
                      createField(JenkinsJobAuthenticationToken.getDescriptor(), false, true, "2"));
        configMap.put(JenkinsAuthenticationUser.getDescriptor(),
                      createField(JenkinsAuthenticationUser.getDescriptor(), true, false, "3"));
        configMap.put(JenkinsAuthenticationPassword.getDescriptor(),
                      createField(JenkinsAuthenticationPassword.getDescriptor(), false, true, "4"));
        configMap.put(JobParameters.getDescriptor(), createField(JobParameters.getDescriptor(), false, false, "5"));
        configMap.put(LogPrint.getDescriptor(), createField(LogPrint.getDescriptor(), false, false, "6"));
        return successResponse(configMap);
    }

    private GoPluginApiResponse handleValidation(GoPluginApiRequest requestMessage) {
        Map<String, String> errors = new HashMap<>();
        Map request = fromGson(requestMessage.requestBody(), Map.class);

        String paramsValue = getParamValue(request);
        if (!paramsValue.isEmpty() && !PARAMS_PATTERN.matcher(paramsValue).matches()) {
            errors.put(JobParameters.getDescriptor(), "Params syntax is <PARAM>=<VALUE>, with COMMA or NEWLINE delimiter");
        }

        return successResponse(singletonMap("errors", errors));
    }

    private GoPluginApiResponse handleTaskExecution(GoPluginApiRequest requestMessage) {
        try {
            Map request = fromGson(requestMessage.requestBody(), Map.class);
            TaskContext taskContext = createTaskContext((Map) request.get("context"));
            TaskConfig taskConfig = createTaskConfig((Map) request.get("config"),
                                                     taskContext.getEnvironmentVariables());

            ExecutionResult taskResult = taskExecutorFactory.getTaskExecutor().execute(taskConfig, taskContext);
            return successResponse(taskResult.toMap());
        } catch (Exception e) {
            String errorMessage = "Failed task execution: " + e.getMessage();
            LOG.error(errorMessage, e);
            return errorResponse(singletonMap("exception", errorMessage));
        }
    }

    private TaskContext createTaskContext(Map context) {
        return new TaskContext(context);
    }

    TaskConfig createTaskConfig(Map config, Map<String, String> environmentVariables) {
        String params = getParamValue(config);
        return new TaskConfig(
                processTemplate(getOrEmpty(config, JenkinsServerUrl.getDescriptor()), environmentVariables),
                processTemplate(getOrEmpty(config, JenkinsJobName.getDescriptor()), environmentVariables),
                processTemplate(getOrEmpty(config, JenkinsJobAuthenticationToken.getDescriptor()), environmentVariables),
                processTemplate(getOrEmpty(config, JenkinsAuthenticationUser.getDescriptor()), environmentVariables),
                processTemplate(getOrEmpty(config, JenkinsAuthenticationPassword.getDescriptor()), environmentVariables),
                Boolean.parseBoolean(getOrEmpty(config, LogPrint.getDescriptor())),
                params.isEmpty() ?
                    emptyMap() :
                    Arrays.stream(processTemplate(params, environmentVariables).split("[,\\n]"))
                          .map(s -> s.split("="))
                          .collect(Collectors.toMap(s -> ((String) s[0]).trim(),
                                                    s -> ((String) s[1]).trim()))
        );
    }

    public String getOrEmpty(Map map, String key) {
        return (String) ((Map) map.getOrDefault(key, emptyMap())).getOrDefault("value", "");
    }

    private String getParamValue(Map request) {
        return getOrEmpty(request, JobParameters.getDescriptor()).replaceAll("\\r", "");
    }

    private GoPluginApiResponse handleTaskView() {
        Map<String, Object> view = new HashMap<>();
        view.put("displayValue", "Jenkins");
        try {
            view.put("template",
                     IOUtils.toString(getClass().getResourceAsStream("/views/task.template.html"), "UTF-8"));
            return successResponse(view);
        } catch (Exception e) {
            String errorMessage = "Failed to find template: " + e.getMessage();
            view.put("exception", errorMessage);
            LOG.error(errorMessage, e);
            return errorResponse(view);
        }
    }

    public Map<String, Object> createField(String displayName,
                                           boolean isRequired,
                                           boolean isSecure,
                                           String displayOrder) {
        Map<String, Object> field = new HashMap<>();
        field.put("display-name", displayName);
        field.put("required", isRequired);
        field.put("secure", isSecure);
        field.put("display-order", displayOrder);
        return field;
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier("task", Collections.singletonList("1.0"));
    }
}
