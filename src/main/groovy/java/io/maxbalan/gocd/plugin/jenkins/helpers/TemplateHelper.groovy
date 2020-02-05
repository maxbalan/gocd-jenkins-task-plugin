package java.io.maxbalan.gocd.plugin.jenkins.helpers

import groovy.text.SimpleTemplateEngine


/**
 * Created on: 05/02/2020
 *
 * @author Maxim Balan
 * */
trait TemplateHelper {
    private final def engine = new SimpleTemplateEngine()

    public String processTemplate(String templateString, Map<String,Object> bindings) {
        this.engine.createTemplate(templateString).make(bindings)
    }

}
