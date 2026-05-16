import groovy.json.JsonOutput
import groovy.json.JsonSlurper

version = 1

val displayName = "Personal Video"

cloudstream {
    description = "Video catalog delivered via HTTP request"
    authors = listOf("Emal Sa")
    status = 1
    tvTypes = listOf("Movie")
    language = "it"
    iconUrl = "https://raw.githubusercontent.com/emal-sa/EmalSa-Cloud-Stream-Extension/main/icon/PersonalVideo/home-video.png"
    requiresResources = false
}

val pluginEntryFile = layout.buildDirectory.file("plugin-entry.json")

val setDisplayNameInPluginEntry = tasks.register("setDisplayNameInPluginEntry") {
    group = "cloudstream"
    dependsOn("writeCacheEntry")
    inputs.file(pluginEntryFile)
    outputs.file(pluginEntryFile)
    outputs.upToDateWhen { false }

    doLast {
        val file = pluginEntryFile.get().asFile
        val parsed = JsonSlurper().parse(file)
        require(parsed is Map<*, *>) { "Expected plugin entry JSON object at ${file.path}" }

        val updated = linkedMapOf<String, Any?>()
        parsed.forEach { (key, value) ->
            updated[key.toString()] = value
        }
        updated["name"] = displayName

        file.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(updated)) + "\n")
    }
}

tasks.named("writeCacheEntry") {
    finalizedBy(setDisplayNameInPluginEntry)
}

rootProject.tasks.named("makePluginsJson") {
    dependsOn(setDisplayNameInPluginEntry)
}
