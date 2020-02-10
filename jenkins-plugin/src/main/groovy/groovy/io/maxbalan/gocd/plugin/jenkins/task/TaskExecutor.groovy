package groovy.io.maxbalan.gocd.plugin.jenkins.task

import groovy.io.maxbalan.gocd.plugin.jenkins.helpers.JenkinsServerFactory

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
    private final static int JOB_CHECK_THRESHOLD = 5 //in seconds

    private final JenkinsServerFactory jenkinsServerFactory
    private final JobConsoleLogger console

    public TaskExecutor() {
        this(JenkinsServerFactory.getFactory(), JobConsoleLogger.getConsoleLogger())
    }

    TaskExecutor(JenkinsServerFactory jenkinsServerFactory, JobConsoleLogger console) {
        this.jenkinsServerFactory = jenkinsServerFactory
        this.console = console
    }

    public ExecutionResult execute(TaskConfig taskConfig, TaskContext taskContext) {
        LOG.info("[Jenkins Plugin] Trigger job [ {} ]", taskConfig.getJobName())

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
            LOG.error("[Jenkins Plugin] task configuration used [ {} ]", taskConfig)
            LOG.error("[Jenkins Plugin] task context used [ {} ]", taskContext)
            LOG.error("[Jenkins Plugin] task execution failed", e)
            return new ExecutionResult(false, "Failed executing task", e)
        }
    }

    private ExecutionResult exec(JenkinsServer jenkinsServer,
                                 TaskConfig taskConfig,
                                 TaskContext taskContext) throws Exception {
        LOG.info("[Jenkins Plugin] Job parameters [ {} ]", taskConfig.getParams())

        JobWithDetails job = jenkinsServer.getJob(taskConfig.getJobName())

        LOG.info("[Jenkins Plugin] Job found [ {} ]", job.getName())

        QueueReference queueReference = taskConfig.getParams()
                .isEmpty() ? job.build() : job.build(taskConfig.getParams())

        LOG.info("[Jenkins Plugin] Jenkins Job started")
        QueueItem queueItem = jenkinsServer.getQueueItem(queueReference)

        while (queueItem.getExecutable() == null) {
            LOG.info("[Jenkins Plugin] Job is in the queue, will wait [ {} seconds ] before checking again",
                     JOB_CHECK_THRESHOLD)
            Thread.sleep(JOB_CHECK_THRESHOLD * 1000)

            queueItem = jenkinsServer.getQueueItem(queueReference)
        }

        Build build = jenkinsServer.getBuild(queueItem)
        LOG.info("[Jenkins Plugin] Job [ {} ] was triggered, current build [ {} ]",
                 taskConfig.getJobName(),
                 build.getNumber())
        while (build.details().isBuilding()) {
            LOG.info("[Jenkins Plugin] Looks like the job [ {} ] is still running, will wait for [ {} seconds] before checking again", taskConfig.getJobName(), JOB_CHECK_THRESHOLD)
            Thread.sleep(JOB_CHECK_THRESHOLD * 1000)
        }

        LOG.info("[Jenkins Plugin] Job has finished [ {} ]", build.details().getResult())
        if (taskConfig.isPrintLog()) {
            console.printLine("Jenkins job console:\n" + build.details().getConsoleOutputText());
        }

        return new ExecutionResult(
                SUCCESS.equals(build.details().getResult()),
                "[Jenkins Plugin] Job [ " + taskConfig.getJobName() + " ] console output:\n" + build.details().getConsoleOutputText())
    }
}
