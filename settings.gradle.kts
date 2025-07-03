import java.net.URI

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = URI("https://artifact.bytedance.com/repository/Volcengine/") }
        maven { url = URI("https://artifact.byteplus.com/repository/public/") }
        maven {
            url = URI("https://artifact.bytedance.com/repository/thrall_base/")
            credentials {
                username = "veVOS"
                password = "KUC9TpKrqbryrxHz"
            }

            authentication {
                create<BasicAuthentication>("basic")
            }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "VideoOneCompose"
include(":app")

include(":components:base", ":components:login")

include(":solutions:interactivelive")
