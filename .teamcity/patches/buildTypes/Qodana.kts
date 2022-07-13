package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.Qodana
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.qodana
import jetbrains.buildServer.configs.kotlin.v2019_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'Qodana'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("Qodana")) {
    expectSteps {
        qodana {
            name = "Qodana"
            linter = jvm {
                version = Qodana.JVMVersion.LATEST
            }
            param("clonefinder-enable", "true")
            param("clonefinder-languages", "Java")
            param("clonefinder-languages-container", "Java Kotlin")
            param("clonefinder-mode", "")
            param("clonefinder-queried-project", "src")
            param("clonefinder-reference-projects", "src")
            param("licenseaudit-enable", "true")
            param("namesAndTagsCustom", "repo.labs.intellij.net/static-analyser/qodana")
            param("report-version", "")
            param("yaml-configuration", "")
        }
    }
    steps {
        insert(0) {
            gradle {
                name = "Generate grammar"
                tasks = "generateGrammarSource"
                param("org.jfrog.artifactory.selectedDeployableServer.defaultModuleVersionConfiguration", "GLOBAL")
            }
        }
        update<Qodana>(1) {
            clearConditions()
            reportAsTests = true
            argumentsEntryPointDocker = "--baseline qodana.sarif.json"
            param("clonefinder-languages", "")
            param("collect-anonymous-statistics", "")
            param("licenseaudit-enable", "")
            param("clonefinder-languages-container", "")
            param("clonefinder-queried-project", "")
            param("clonefinder-enable", "")
            param("clonefinder-reference-projects", "")
        }
    }
}
