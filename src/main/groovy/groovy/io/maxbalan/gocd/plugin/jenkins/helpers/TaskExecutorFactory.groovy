package groovy.io.maxbalan.gocd.plugin.jenkins.helpers

import groovy.io.maxbalan.gocd.plugin.jenkins.task.TaskExecutor

import static groovy.io.maxbalan.gocd.plugin.jenkins.JenkinsPlugin.LOG

/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
class TaskExecutorFactory {
    private static TaskExecutorFactory INSTANCE = new TaskExecutorFactory()

    public static TaskExecutorFactory getFactory() {
        return INSTANCE
    }

    private TaskExecutorFactory() {
    }

    public TaskExecutor getTaskExecutor() {
        LOG.info("[Jenkins Plugin] building new task executor")
        return new TaskExecutor()
    }
}
