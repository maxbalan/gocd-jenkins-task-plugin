package java.io.maxbalan.gocd.plugin.jenkins.helpers;


import java.util.Arrays;

/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 */
public enum RequestType {
    TASK_CONFIGURATION("configuration"),
    TASK_VIEW("view"),
    TASK_VALIDATE("validate"),
    TASK_EXECUTE("execute"),
    UNKNOWN("unknown");

    private String descriptor;

    private RequestType(String descriptor) {
        this.descriptor = descriptor;
    }

    public static RequestType fromString(final String value) {
        RequestType r = UNKNOWN;

        r = Arrays.stream(values())
                  .filter(t -> t.descriptor.equalsIgnoreCase(value))
                  .findFirst()
                  .orElse(UNKNOWN);

        return r;
    }

    public static boolean has(final String value) {
        RequestType r = UNKNOWN;

        r = Arrays.stream(values())
                  .filter(t -> t.descriptor.equalsIgnoreCase(value))
                  .findFirst()
                  .orElse(UNKNOWN);

        return r != UNKNOWN;
    }

    public String getDescriptor() {
        return this.descriptor;
    }

}
