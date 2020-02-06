

/*
 * Copyright 2020 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.io.maxbalan.gocd.plugin.jenkins.helpers;

import com.offbytwo.jenkins.JenkinsServer;
import test.io.maxbalan.gocd.plugin.jenkins.JenkinsPlugin;

import java.net.URI;
import java.net.URISyntaxException;

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
        JenkinsPlugin.LOG.info("[Jenkins Plugin] using URI [ {} ]", url);
        return new JenkinsServer(new URI(url));
    }
}
