package groovy.io.maxbalan.gocd.plugin.jenkins

import groovy.io.maxbalan.gocd.plugin.jenkins.helpers.TaskExecutorFactory
import spock.lang.Specification


/**
 * Created on: 07/02/2020
 *
 * @author Maxim Balan
 * */
class JenkinsPluginSpec extends Specification {

    JenkinsPlugin plugin

    def setup() {
        plugin = new JenkinsPlugin(TaskExecutorFactory.getFactory())
    }

    def "when setting job parameters then check those are processed as expected"() {
        given:
        def config = [
                jobParams: [
                        secure: false,
                        required: false,
                        value : "t1=asd," +
                                " t2=qwe," +
                                "t3=rty ," +
                                "t4=\$test," +
                                "t5=\${test}"
                ]
        ]

        def env = [
                test: "testENV_1"
        ]

        when:
        def r = plugin.createTaskConfig(config, env)

        then:
        println "Test result: $r"

        verifyAll {
            r.params.t1 == "asd"
            r.params.t2 == "qwe"
            r.params.t3 == "rty"
            r.params.t4 == "testENV_1"
            r.params.t5 == "testENV_1"
        }
    }

    def "when setting job multi-lines parameter then check those are processed as expected"() {
        given:
        def config = [
                jobParams: [
                        secure: false,
                        required: false,
                        value : "t1=asd\n" +
                                "ffff"
                ]
        ]

        def env = [
                test: "testENV_1"
        ]

        when:
        def r = plugin.createTaskConfig(config, env)

        then:
        println "Test result: $r"
        r.params.t1 == "asd\nffff"
    }

}
