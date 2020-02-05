package java.io.maxbalan.gocd.plugin.jenkins.helpers

import static java.io.maxbalan.gocd.plugin.jenkins.JenkinsPlugin.LOG

import java.io.maxbalan.gocd.plugin.jenkins.task.TaskExecutor

/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
class TaskExecutorFactory {
    private static TaskExecutorFactory instance = new TaskExecutorFactory()

    public static TaskExecutorFactory getFactory() {
        return instance
    }

    private TaskExecutorFactory() {
    }

    public TaskExecutor getTaskExecutor() {
        LOG.info("[Jenkins Plugin] building new task executor")
        return new TaskExecutor()
    }
}
