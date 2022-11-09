plugins {
  id("java")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}

tasks.withType<JavaCompile>().configureEach {
  options.release.set(11)
}

tasks.withType<Javadoc>().configureEach {
  (options as StandardJavadocDocletOptions).tags(
    "apiNote:a:API Note:",
    "implSpec:a:Implementation Requirements:",
    "implNote:a:Implementation Note:"
  )
}