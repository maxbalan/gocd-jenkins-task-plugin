package java.io.maxbalan.gocd.plugin.jenkins.helpers;

import static java.io.maxbalan.gocd.plugin.jenkins.JenkinsPlugin.LOG;

import java.net.URI;
import java.net.URISyntaxException;

import com.offbytwo.jenkins.JenkinsServer;

/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 */
public class JenkinsServerFactory {
    private static JenkinsServerFactory instance = new JenkinsServerFactory();

    public static JenkinsServerFactory getFactory() {
        return instance;
    }

    private JenkinsServerFactory() {
    }

    public JenkinsServer getJenkinsServer(URI uri, String username, String password) {
        return new JenkinsServer(uri, username, password);
    }

    public JenkinsServer getJenkinsServer(String url) throws URISyntaxException {
        LOG.info("[Jenkins Plugin] using URI [ {} ]", url);
        return new JenkinsServer(new URI(url));
    }
}
