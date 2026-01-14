plugins {
  id("io.snyk.gradle.plugin.snykplugin")
}

snyk {
  setSeverity("low")
  setArguments("--all-sub-projects --sarif-file-output=snyk.sarif")
  setAutoDownload(true)
  setAutoUpdate(true)
}
