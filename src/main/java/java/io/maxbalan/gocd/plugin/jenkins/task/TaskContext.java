package java.io.maxbalan.gocd.plugin.jenkins.task;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
public class TaskContext {
    private final Map environmentVariables;
    private final String workingDir;

    public TaskContext(Map context) {
        this.environmentVariables = new HashMap((Map) context.getOrDefault("environmentVariables", Collections.EMPTY_MAP));
        this.workingDir = (String) context.getOrDefault("workingDirectory", "");
    }

    public Map getEnvironmentVariables() {
        return new HashMap(this.environmentVariables);
    }

    public String getWorkingDir() {
        return this.workingDir;
    }

}
