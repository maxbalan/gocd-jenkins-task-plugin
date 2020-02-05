package groovy.io.maxbalan.gocd.plugin.jenkins.task


/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
class ExecutionResult {
    private boolean success
    private String message
    private Throwable exception

    public ExecutionResult(boolean success, String message) {
        this.success = success
        this.message = message
    }

    public ExecutionResult(boolean success, String message, Throwable exception) {
        this(success, message)
        this.exception = exception
    }

    public Map toMap() {
        final Map<String, Object> result = new HashMap<>()
        result.put("success", success)
        result.put("message", message)
        result.put("exception", exception)
        return result
    }

    public boolean isSuccess() {
        return success
    }
}
