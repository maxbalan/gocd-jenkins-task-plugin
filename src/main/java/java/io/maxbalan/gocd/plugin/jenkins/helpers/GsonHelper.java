package java.io.maxbalan.gocd.plugin.jenkins.helpers;


/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
public class GsonHelper {

    public static  <T> T fromGson(String json, Class<T> classOfT) {
       return GsonSingleton.gson().fromJson(json, classOfT);
    }

    public static  <T> String toGson(T obj) {
        return GsonSingleton.gson().toJson(obj);
    }

}
