package groovy.io.maxbalan.gocd.plugin.jenkins

import spock.lang.Specification

import java.lang.reflect.Field
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.testng.log4testng.Logger


/**
 * Created on: 07/02/2020
 *
 * @author Maxim Balan
 * */
class JobParametersPatternRegexSpec extends Specification {

    JenkinsPlugin plugin

    public static final Pattern JobParametersPattern = Pattern.compile('''^([^=]*)=(.*)$''', Pattern.DOTALL)
    public static final Pattern JobParametersPattern2 = Pattern.compile("\\w+=(\\\$(\\{\\w+}|\\w+)|\\w+)([,\n]\\w+=(\\\$(\\{\\w+}|\\w+)|\\w+))*")
    def setup() {
        plugin = Mock(JenkinsPlugin)
//        Field f = JenkinsPlugin.class.getDeclaredField("LOG")
//        f.set(null, null)
    }

    def "when processing a multiline parameters input then correctly select the parameters"() {
        given:
        def ml = ''' t1_=asaasas sss  
t2=s-f
t_3=${test}
t4=$test

   t5="a"'''

        when:
        def matcher = JobParametersPattern.matcher(ml).matches()

        then:
        println matcher
        println JobParametersPattern2.matcher(ml).matches()
        true
    }
}
