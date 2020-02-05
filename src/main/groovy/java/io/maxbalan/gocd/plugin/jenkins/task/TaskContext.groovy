package java.io.maxbalan.gocd.plugin.jenkins.task


/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
class TaskContext {
    private final Map environmentVariables
    private final String workingDir

    public TaskContext(Map context) {
        this.environmentVariables = new HashMap(context.environmentVariables as Map)
        this.workingDir = context.workingDirectory as String
    }

    public Map getEnvironmentVariables() {
        return new HashMap(this.environmentVariables)
    }

    public String getWorkingDir() {
        return workingDir
    }

}
