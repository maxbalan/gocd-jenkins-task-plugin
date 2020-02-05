package groovy.io.maxbalan.gocd.plugin.jenkins.helpers


/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
enum RequestType {
    TASK_CONFIGURATION("configuration"),
    TASK_VIEW("view"),
    TASK_VALIDATE("validate"),
    TASK_EXECUTE("execute"),
    UNKNOWN("unknown")

    private def descriptor

    private RequestType(String descriptor) {
        this.descriptor = descriptor
    }

    public static RequestType fromString(final String value) {
        def r = UNKNOWN

        def s = values().find { it.descriptor == value }
        r = s != null ? s : r

        return r
    }

    public static boolean has(final String value) {
        def r = UNKNOWN

        def s = values().find { it.descriptor == value }
        r = s != null ? s : r

        return r != UNKNOWN
    }

    def getDescriptor() {
        this.descriptor
    }

}
