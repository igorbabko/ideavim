package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.golang
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'Build'
in the root project, and delete the patch script.
*/
create(DslContext.projectId, BuildType({
    id("Build")
    name = "IdeaVim compatibility with external plugins"

    steps {
        script {
            scriptContent = "go run test.go"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        golang {
            testFormat = "json"
        }
    }
}))

