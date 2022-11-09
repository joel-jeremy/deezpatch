import net.ltgt.gradle.errorprone.CheckSeverity
// Kotlin extension methods.
import net.ltgt.gradle.errorprone.errorprone
import net.ltgt.gradle.nullaway.nullaway

plugins {
  id("deezpatch.java-conventions")
  id("jacoco")
  id("net.ltgt.errorprone")
  id("net.ltgt.nullaway")
  id("com.diffplug.spotless")
}

dependencies {
  errorprone(libs.errorprone)
  errorprone(libs.nullaway)
  compileOnly(libs.checkerframework)
}

jacoco {
  toolVersion = libs.versions.jacoco.get()
}

tasks.withType<JacocoCoverageVerification>().configureEach {
  val execFiles = javaProjects()
      .flatMap { it.tasks.withType<JacocoReport>() }
      .flatMap { it.executionData.files }

  executionData.from(execFiles)

  violationRules { 
    rule { 
      limit { 
        minimum = "1".toBigDecimal()
      } 
    } 
  } 
}
tasks.named("check").configure {
  finalizedBy(tasks.withType<JacocoCoverageVerification>())
}

tasks.withType<JavaCompile>().configureEach {
  options.errorprone {
    // Only apply to main source set (not test,jmh)
    isEnabled.set(name == "compileJava")

    val enabledChecks = listOf(
      "AssertFalse", "BuilderReturnThis", "CheckedExceptionNotThrown", "ClassName", 
      "ComparisonContractViolated", "DepAnn", "EmptyIf", "EqualsBrokenForNull",
      "FieldCanBeFinal", "FieldCanBeLocal", "FieldCanBeStatic", "ForEachIterable",
      "FuzzyEqualsShouldNotBeUsedInEqualsMethod", "FunctionalInterfaceClash",
      "IterablePathParameter", "LongLiteralLowerCaseSuffix", "MissingBraces",
      "MissingDefault", "MixedArrayDimensions", "NoAllocation", "PackageLocation", 
      "PreferredInterfaceType", "RedundantThrows", "RemoveUnusedImports", 
      "ReturnsNullCollection", "SelfAlwaysReturnsThis", "StronglyTypeByteString", 
      "StronglyTypeTime", "SwitchDefault", "TimeUnitMismatch", "TransientMisuse", 
      "UnnecessarilyVisible", "UnnecessaryAnonymousClass", "UnnecessaryOptionalGet", 
      "UnsafeLocaleUsage", "UnusedTypeParameter", "UsingJsr305CheckReturnValue"
    )
    enabledChecks.forEach { check -> enable(check) }

    val disabledChecks = listOf(
      "CanIgnoreReturnValueSuggester", "CatchingUnchecked"
    )
    disabledChecks.forEach { check -> disable(check) }
    
    nullaway {
      severity.set(CheckSeverity.ERROR)
      annotatedPackages.add("io.github.joeljeremy.deezpatch")
      checkOptionalEmptiness.set(true)
      suggestSuppressions.set(true)
    }
  }
}

spotless {
  isEnforceCheck = !hasProperty("skipSpotlessCheck")
  java {
    // Generated code should not be subjected to spotless.
    target("src/*/java/**/*.java")
    // Only format files which have changed since origin/main.
    // ratchetFrom "origin/main"
    toggleOffOn()
    googleJavaFormat().reflowLongStrings()
    formatAnnotations()
  }
}
