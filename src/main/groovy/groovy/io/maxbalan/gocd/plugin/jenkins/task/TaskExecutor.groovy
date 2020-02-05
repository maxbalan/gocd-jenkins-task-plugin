package groovy.io.maxbalan.gocd.plugin.jenkins.task


import static com.offbytwo.jenkins.model.BuildResult.SUCCESS
import static groovy.io.maxbalan.gocd.plugin.jenkins.JenkinsPlugin.LOG

import com.offbytwo.jenkins.JenkinsServer
import com.offbytwo.jenkins.model.Build
import com.offbytwo.jenkins.model.JobWithDetails
import com.offbytwo.jenkins.model.QueueItem
import com.offbytwo.jenkins.model.QueueReference
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger

/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
class TaskExecutor {

    private final groovy.io.maxbalan.gocd.plugin.jenkins.helpers.JenkinsServerFactory jenkinsServerFactory
    private final JobConsoleLogger console

    public TaskExecutor() {
        this(groovy.io.maxbalan.gocd.plugin.jenkins.helpers.JenkinsServerFactory.getFactory(), JobConsoleLogger.getConsoleLogger())
    }

    TaskExecutor(groovy.io.maxbalan.gocd.plugin.jenkins.helpers.JenkinsServerFactory jenkinsServerFactory, JobConsoleLogger console) {
        this.jenkinsServerFactory = jenkinsServerFactory
        this.console = console
    }

    public ExecutionResult execute(TaskConfig taskConfig, TaskContext taskContext) {
        console.printLine("Executing request to url [" + taskConfig.getUrl() + "] job [" + taskConfig.getJobName() + "]")
        console.printEnvironment(taskConfig.getParams())

        try {
            JenkinsServer jenkinsServer
            if (taskConfig.isTokenAuthentication()) {
                LOG.info("[Jenkins Plugin] using job token authentication")
                jenkinsServer = jenkinsServerFactory.getJenkinsServer(taskConfig.getUrl())
            } else {
                LOG.info("[Jenkins Plugin] using basic authentication")
                jenkinsServer = jenkinsServerFactory.getJenkinsServer(new URI(taskConfig.getUrl()),
                                                                      taskConfig.getUsername(),
                                                                      taskConfig.getPassword())
            }

            return exec(jenkinsServer, taskConfig, taskContext)
        } catch (Exception e) {
            LOG.error("[Jenkins Plugin] task execution failed", e)
            return new ExecutionResult(false, "Failed executing task", e)
        }
    }

    private ExecutionResult exec(JenkinsServer jenkinsServer,
                                 TaskConfig taskConfig,
                                 TaskContext taskContext) throws Exception {
        JobWithDetails job = jenkinsServer.getJob(taskConfig.getJobName())
        LOG.info("[Jenkins Plugin] Job found [ {} ]", job.getName())
        LOG.info("[Jenkins Plugin] Job parameters [ {} ]", taskConfig.getParams())

        console.printLine("Building job...")
        QueueReference queueReference = taskConfig.getParams()
                .isEmpty() ? job.build() : job.build(taskConfig.getParams())

        LOG.info("[Jenkins Plugin] Jenkins Job started")
        QueueItem queueItem = jenkinsServer.getQueueItem(queueReference)

        while (queueItem.getExecutable() == null) {
            console.printLine("Job still in queue" + queueItem.getExecutable())
            Thread.sleep(2000)

            queueItem = jenkinsServer.getQueueItem(queueReference)
        }

        Build build = jenkinsServer.getBuild(queueItem)
        console.printLine("Job id [" + build.getNumber() + "] is in progress. URL: " + build.getUrl())
        while (build.details().isBuilding()) {
            console.printLine("Job still running")
            Thread.sleep(10000)
        }

        console.printLine("Job done. Result = " + build.details().getResult())
        if (taskConfig.isPrintLog())
            console.printLine("Jenkins console output:\n" + build.details().getConsoleOutputText())

        return new ExecutionResult(
                SUCCESS.equals(build.details().getResult()),
                "Jenkins Console:\n" + build.details().getConsoleOutputText()
        )
    }
}
