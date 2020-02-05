package groovy.io.maxbalan.gocd.plugin.jenkins.helpers


/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
enum ConfigParams {
    JenkinsServerUrl("jenkinsUrl"),
    JenkinsJobName("jobName"),
    JenkinsJobAuthenticationToken("jobToken"),
    JenkinsAuthenticationUser("username"),
    JenkinsAuthenticationPassword("password"),
    JobParameters("jobParams"),
    LogPrint("shouldPrintLog"),

    private def descriptor

    private ConfigParams(String descriptor) {
        this.descriptor = descriptor
    }

    String getDescriptor() {
        this.descriptor
    }

}
