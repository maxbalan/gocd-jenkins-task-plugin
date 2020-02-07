package groovy.io.maxbalan.gocd.plugin.jenkins

import groovy.io.maxbalan.gocd.plugin.jenkins.helpers.GsonHelper
import groovy.io.maxbalan.gocd.plugin.jenkins.helpers.PluginHelper
import groovy.io.maxbalan.gocd.plugin.jenkins.helpers.RequestType
import groovy.io.maxbalan.gocd.plugin.jenkins.helpers.TaskExecutorFactory
import groovy.io.maxbalan.gocd.plugin.jenkins.helpers.TemplateHelper
import groovy.io.maxbalan.gocd.plugin.jenkins.task.ExecutionResult
import groovy.io.maxbalan.gocd.plugin.jenkins.task.TaskConfig
import groovy.io.maxbalan.gocd.plugin.jenkins.task.TaskContext

import static groovy.io.maxbalan.gocd.plugin.jenkins.helpers.ConfigParams.JenkinsAuthenticationPassword
import static groovy.io.maxbalan.gocd.plugin.jenkins.helpers.ConfigParams.JenkinsAuthenticationUser
import static groovy.io.maxbalan.gocd.plugin.jenkins.helpers.ConfigParams.JenkinsJobAuthenticationToken
import static groovy.io.maxbalan.gocd.plugin.jenkins.helpers.ConfigParams.JenkinsJobName
import static groovy.io.maxbalan.gocd.plugin.jenkins.helpers.ConfigParams.JenkinsServerUrl
import static groovy.io.maxbalan.gocd.plugin.jenkins.helpers.ConfigParams.JobParameters
import static groovy.io.maxbalan.gocd.plugin.jenkins.helpers.ConfigParams.LogPrint
import static java.util.Collections.emptyMap
import static java.util.Collections.singletonMap

import java.util.regex.Pattern
import java.util.stream.Collectors

import org.apache.commons.io.IOUtils

import com.thoughtworks.go.plugin.api.AbstractGoPlugin
import com.thoughtworks.go.plugin.api.GoPluginIdentifier
import com.thoughtworks.go.plugin.api.annotation.Extension
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException
import com.thoughtworks.go.plugin.api.logging.Logger
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse

/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
@Extension
class JenkinsPlugin extends AbstractGoPlugin implements TemplateHelper, GsonHelper, PluginHelper {
    public static final Logger LOG = Logger.getLoggerFor(JenkinsPlugin.class)
    public static final Pattern JobParametersPattern = Pattern.compile("\\w+=(\\\$(\\{\\w+}|\\w+)|\\w+)([,\\n]\\w+=(\\\$(\\{\\w+}|\\w+)|\\w+))*")

    private final TaskExecutorFactory taskExecutorFactory

    public JenkinsPlugin() {
        this(TaskExecutorFactory.getFactory())
    }

    JenkinsPlugin(TaskExecutorFactory taskExecutorFactory) {
        this.taskExecutorFactory = taskExecutorFactory
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest requestMessage) throws UnhandledRequestTypeException {
        final String requestName = requestMessage.requestName()
        LOG.info("[Jenkins Plugin] Got request [{}], body: {}", requestName, requestMessage.requestBody())
        if (RequestType.TASK_CONFIGURATION.descriptor.equals(requestName)) {
            return handleGetConfigRequest()
        } else if (RequestType.TASK_VALIDATE.descriptor.equals(requestName)) {
            return handleValidation(requestMessage)
        } else if (RequestType.TASK_EXECUTE.descriptor.equals(requestName)) {
            return handleTaskExecution(requestMessage)
        } else if (RequestType.TASK_VIEW.descriptor.equals(requestName)) {
            return handleTaskView()
        }

        throw new UnhandledRequestTypeException(requestName)
    }

    private GoPluginApiResponse handleGetConfigRequest() {
        final Map<String, Object> configMap = new HashMap<>()
        configMap.put(JenkinsServerUrl.descriptor, createField(JenkinsServerUrl.descriptor, true, false, "0"))
        configMap.put(JenkinsJobName.descriptor, createField(JenkinsJobName.descriptor, true, false, "1"))
        configMap.put(JenkinsJobAuthenticationToken.descriptor,
                      createField(JenkinsJobAuthenticationToken.descriptor, false, true, "2"))
        configMap.put(JenkinsAuthenticationUser.descriptor,
                      createField(JenkinsAuthenticationUser.descriptor, true, false, "3"))
        configMap.put(JenkinsAuthenticationPassword.descriptor,
                      createField(JenkinsAuthenticationPassword.descriptor, false, true, "4"))
        configMap.put(JobParameters.descriptor, createField(JobParameters.descriptor, false, false, "5"))
        configMap.put(LogPrint.descriptor, createField(LogPrint.descriptor, false, false, "6"))
        return successResponse(configMap)
    }

    private GoPluginApiResponse handleValidation(GoPluginApiRequest requestMessage) {
        Map<String, String> errors = new HashMap<>()
        Map request = fromGson(requestMessage.requestBody(), Map.class)

        String paramsValue = unpackJobParameters(request)
        if (!paramsValue.isEmpty() && !JobParametersPattern.matcher(paramsValue).matches()) {
            errors.put(JobParameters.descriptor, "Params syntax is <PARAM>=<VALUE>, with COMMA or NEWLINE delimiter")
        }

        return successResponse(singletonMap("errors", errors))
    }

    private GoPluginApiResponse handleTaskExecution(GoPluginApiRequest requestMessage) {
        try {
            Map request = fromGson(requestMessage.requestBody(), Map.class)
            TaskContext taskContext = createTaskContext((Map) request.get("context"))
            TaskConfig taskConfig = createTaskConfig((Map) request.get("config"),
                                                     taskContext.getEnvironmentVariables())

            ExecutionResult taskResult = taskExecutorFactory.getTaskExecutor().execute(taskConfig, taskContext)
            return successResponse(taskResult.toMap())
        } catch (Exception e) {
            String errorMessage = "Failed task execution: " + e.getMessage()
            LOG.error(errorMessage, e)
            return errorResponse(singletonMap("exception", errorMessage))
        }
    }

    private TaskContext createTaskContext(Map context) {
        return new TaskContext(context)
    }

    TaskConfig createTaskConfig(Map config, Map<String, String> environmentVariables) {
        String params = unpackJobParameters(config)

        def configTask = new TaskConfig(processTemplate(getOrEmpty(config, JenkinsServerUrl.descriptor), environmentVariables),
                                        processTemplate(getOrEmpty(config, JenkinsJobName.descriptor), environmentVariables),
                                        processTemplate(getOrEmpty(config, JenkinsJobAuthenticationToken.descriptor), environmentVariables),
                                        processTemplate(getOrEmpty(config, JenkinsAuthenticationUser.descriptor), environmentVariables),
                                        processTemplate(getOrEmpty(config, JenkinsAuthenticationPassword.descriptor), environmentVariables),
                                        Boolean.parseBoolean(getOrEmpty(config, LogPrint.descriptor)),
                                        params.isEmpty() ?
                                                emptyMap() :
                                                Arrays.stream(replaceWithEnv(params, environmentVariables).split("[,\\n]"))
                                                        .map({ s -> s.split("=") })
                                                        .collect(Collectors.toMap({ s -> (s[0] as String).trim() },
                                                                                  { s -> (s[1] as String).trim() })))

        LOG.info("[Jenkins Plugin] Parsed job configuration [ {} ]", configTask)

        configTask
    }

    private String unpackJobParameters(Map request) {
        def jobParams = getOrEmpty(request, JobParameters.descriptor).replaceAll("\\r", "")

        LOG.info("[Jenkins Plugin] Job parameter received [ {} ]", jobParams)

        jobParams
    }

    private GoPluginApiResponse handleTaskView() {
        Map<String, Object> view = new HashMap<>()
        view.put("displayValue", "Jenkins")
        try {
            view.put("template",
                     IOUtils.toString(getClass().getResourceAsStream("/task.template.html"), "UTF-8"))
            return successResponse(view)
        } catch (Exception e) {
            String errorMessage = "Failed to find template: " + e.getMessage()
            view.put("exception", errorMessage)
            LOG.error(errorMessage, e)
            return errorResponse(view)
        }
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier("task", Collections.singletonList("1.0"))
    }
}
