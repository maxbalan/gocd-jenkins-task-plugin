package java.io.maxbalan.gocd.plugin.jenkins.helpers

import com.google.gson.Gson
import com.google.gson.GsonBuilder


/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
class GsonSingleton {
    private static final Gson instance = new GsonBuilder().serializeNulls().create()

    public static Gson gson() {
        instance
    }

}
