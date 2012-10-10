package cc.catalysts.gradle.plugins.grails

import org.gradle.api.DefaultTask
import org.gradle.api.IllegalOperationAtExecutionTimeException
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.TaskAction

import java.util.regex.Pattern
/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class WarTask extends DefaultTask {

    public WarTask() {
        project.afterEvaluate {
            if (project.grails.application == null) {
                throw new InvalidUserDataException("Please specify the main grails application in your build.gradle, e.g. grails { application = project(':demo-web') }")
            }

            dependsOn project.grails.application.tasks.'grails-war'
        }
    }

    @TaskAction
    void rename() {
        Pattern warFile = ~/.+\.war/
        File warSourceDir = new File(project.grails.application.projectDir, 'target')

        File warDestination = project.grails.warFile ?: new File(warDir, 'ROOT.war')
        warDestination.mkdirs()
        warDestination.delete() // delete old renamed WAR


        int found = 0
        warSourceDir.eachFileMatch(warFile) {
            found++
        }

        if (found == 0) {
            throw new IllegalOperationAtExecutionTimeException("Can't find .war file generated by grails in " + warSourceDir)
        }
        else if (found > 1) {
            throw new IllegalOperationAtExecutionTimeException("Found multiple .war files, please check " + warSourceDir + " and delete all .war files.")
        }
        else {
            // only one .war file in directory, generated by grails
            warSourceDir.eachFileMatch(warFile) {
                if (!it.renameTo(warDestination)) {
                    throw new IllegalOperationAtExecutionTimeException("Can't move " + it + " to " + warDestination + ".")
                }
            }
        }
    }
}
