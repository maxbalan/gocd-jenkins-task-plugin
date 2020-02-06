package java.io.maxbalan.gocd.plugin.jenkins.task;


import java.util.HashMap;
import java.util.Map;

/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
public class ExecutionResult {
    private boolean success;
    private String message;
    private Throwable exception;

    public ExecutionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ExecutionResult(boolean success, String message, Throwable exception) {
        this(success, message);
        this.exception = exception;
    }

    public Map toMap() {
        final Map<String, Object> result = new HashMap<>();
        result.put("success", this.success);
        result.put("message", this.message);
        result.put("exception", this.exception);
        return result;
    }

    public boolean isSuccess() {
        return this.success;
    }
}
