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


import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueItem;
import com.offbytwo.jenkins.model.QueueReference;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import test.io.maxbalan.gocd.plugin.jenkins.JenkinsPlugin;

import test.io.maxbalan.gocd.plugin.jenkins.helpers.JenkinsServerFactory;
import java.net.URI;

import static com.offbytwo.jenkins.model.BuildResult.SUCCESS;

/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 */
public class TaskExecutor {
    private final static int JOB_CHECK_TRESHOLD = 5; //in seconds
    private final JenkinsServerFactory jenkinsServerFactory;
    private final JobConsoleLogger console;

    public TaskExecutor() {
        this(JenkinsServerFactory.getFactory(), JobConsoleLogger.getConsoleLogger());
    }

    TaskExecutor(JenkinsServerFactory jenkinsServerFactory, JobConsoleLogger console) {
        this.jenkinsServerFactory = jenkinsServerFactory;
        this.console = console;
    }

    public ExecutionResult execute(TaskConfig taskConfig, TaskContext taskContext) {
        JenkinsPlugin.LOG.info("[Jenkins Plugin] Trigger job [ {} ]", taskConfig.getJobName());

        try {
            JenkinsServer jenkinsServer;
            if (taskConfig.isTokenAuthentication()) {
                JenkinsPlugin.LOG.info("[Jenkins Plugin] using job token authentication");
                jenkinsServer = jenkinsServerFactory.getJenkinsServer(taskConfig.getUrl());
            } else {
                JenkinsPlugin.LOG.info("[Jenkins Plugin] using basic authentication");
                jenkinsServer = jenkinsServerFactory.getJenkinsServer(new URI(taskConfig.getUrl()),
                                                                      taskConfig.getUsername(),
                                                                      taskConfig.getPassword());
            }

            return exec(jenkinsServer, taskConfig, taskContext);
        } catch (Exception e) {
            JenkinsPlugin.LOG.error("[Jenkins Plugin] task execution failed", e);
            return new ExecutionResult(false, "Failed executing task", e);
        }
    }

    private ExecutionResult exec(JenkinsServer jenkinsServer,
                                 TaskConfig taskConfig,
                                 TaskContext taskContext) throws Exception {
        JobWithDetails job = jenkinsServer.getJob(taskConfig.getJobName());
        JenkinsPlugin.LOG.info("[Jenkins Plugin] Job found [ {} ]", job.getName());

        QueueReference queueReference = taskConfig.getParams()
                                                  .isEmpty() ? job.build() : job.build(taskConfig.getParams());

        JenkinsPlugin.LOG.info("[Jenkins Plugin] Jenkins Job started");
        QueueItem queueItem = jenkinsServer.getQueueItem(queueReference);

        while (queueItem.getExecutable() == null) {
            JenkinsPlugin.LOG.info("[Jenkins Plugin] Job is in the queue, will wait [ {} seconds ] before checking again",
                     JOB_CHECK_TRESHOLD);
            Thread.sleep(JOB_CHECK_TRESHOLD * 1000);

            queueItem = jenkinsServer.getQueueItem(queueReference);
        }

        Build build = jenkinsServer.getBuild(queueItem);
        JenkinsPlugin.LOG.info("[Jenkins Plugin] Job [ {} ] was triggered, current build [ {} ]",
                 taskConfig.getJobName(),
                 build.getNumber());

        while (build.details().isBuilding()) {
            JenkinsPlugin.LOG.info(
                "[Jenkins Plugin] Looks like the job [ {} ] is still running, will wait for [ {} seconds] before checking again",
                taskConfig.getJobName(),
                JOB_CHECK_TRESHOLD);
            Thread.sleep(JOB_CHECK_TRESHOLD * 1000);
        }

        JenkinsPlugin.LOG.info("[Jenkins Plugin] Job has finished [ {} ]", build.details().getResult());
        if (taskConfig.isPrintLog()) {
            JenkinsPlugin.LOG.info("[Jenkins Plugin] Job [ {} ] console output [ {} ]",
                     taskConfig.getJobName(),
                     build.details().getConsoleOutputText());
        }

        return new ExecutionResult(SUCCESS.equals(build.details().getResult()),
                                   "[Jenkins Plugin] Job [ " + taskConfig.getJobName() + " ] console output:\n" + build.details().getConsoleOutputText());
    }
}
