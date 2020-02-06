package java.io.maxbalan.gocd.plugin.jenkins.helpers;


/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
public enum ConfigParams {
    JenkinsServerUrl("jenkinsUrl"),
    JenkinsJobName("jobName"),
    JenkinsJobAuthenticationToken("jobToken"),
    JenkinsAuthenticationUser("username"),
    JenkinsAuthenticationPassword("password"),
    JobParameters("jobParams"),
    LogPrint("shouldPrintLog");

    private String descriptor;

    private ConfigParams(String descriptor) {
        this.descriptor = descriptor;
    }

    public String getDescriptor() {
        return this.descriptor;
    }

}
