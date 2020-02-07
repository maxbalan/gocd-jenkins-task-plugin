package groovy.io.maxbalan.gocd.plugin.jenkins.task

import spock.lang.Specification


/**
 * Created on: 07/02/2020
 *
 * @author Maxim Balan
 * */
class TaskConfigSpec extends Specification {

    def "when setting task configuration then check correct parameters are returned"() {
        given:
        def expected = [
                url     : "url",
                jobName : "jobName",
                jobToken: "jobToken",
                username: "username",
                password: "password",
                printLog: true,
                params  : [
                        param1: "xx",
                        param2: "yy"
                ]
        ]

        when:
        def r = new TaskConfig(expected.url,
                               expected.jobName,
                               expected.jobToken,
                               expected.username,
                               expected.password,
                               expected.printLog,
                               expected.params)

        then:
        expected.url == r.getUrl()
        expected.jobName == r.getJobName()
        expected.jobToken == r.getJobToken()
        expected.username == r.getUsername()
        expected.password == r.getPassword()
        expected.printLog == r.isPrintLog()
        expected.params.param1 == r.getParams().param1
        expected.params.param2 == r.getParams().param2
    }
}
