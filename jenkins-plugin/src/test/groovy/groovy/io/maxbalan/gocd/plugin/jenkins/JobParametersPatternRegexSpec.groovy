package groovy.io.maxbalan.gocd.plugin.jenkins

import spock.lang.Specification

/**
 * Created on: 07/02/2020
 *
 * @author Maxim Balan
 * */
class JobParametersPatternRegexSpec extends Specification {

    JenkinsPlugin plugin

    def setup() {
        plugin = Mock(JenkinsPlugin)
    }

    def "when processing a multiline parameters input then pattern should find a match"() {
        given:
        def ml = "  t1_=asaasas sss  \n" +
                "t2=s-f\n" +
                "t_3=\${test}\n" +
                "t4=\$test\n" +
                "\n" +
                "   t5=\"a\"" +
                "t6=asd-sds"

        when:
        def matcher = JenkinsPlugin.JobParametersPattern.matcher(ml).matches()

        then:
        matcher
    }

}
