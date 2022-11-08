plugins {
  id("io.snyk.gradle.plugin.snykplugin")
}

snyk {
  setApi(findProperty("snykToken") as String?)
  setSeverity("low")
  setArguments("--all-sub-projects --sarif-file-output=snyk.sarif")
  setAutoDownload(true)
  setAutoUpdate(true)
}
