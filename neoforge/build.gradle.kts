plugins {
    id("idea")
    id("net.neoforged.moddev") version "2.0.28-beta"
    id("java-library")
}

val MINECRAFT_VERSION: String by rootProject.extra
val PARCHMENT_VERSION: String? by rootProject.extra
val NEOFORGE_VERSION: String by rootProject.extra
val MOD_VERSION: String by rootProject.extra

base {
    archivesName = "iris-neoforge"
}

sourceSets {
}

repositories {
    maven("https://maven.su5ed.dev/releases")
    maven("https://maven.neoforged.net/releases/")

    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

tasks.jar {

    from(rootDir.resolve("LICENSE.md"))

    filesMatching("neoforge.mods.toml") {
        expand(mapOf("version" to MOD_VERSION))
    }

    manifest.attributes["Main-Class"] = "net.irisshaders.iris.LaunchWarn"
}

// NeoGradle compiles the game, but we don't want to add our common code to the game's code
val notNeoTask: (Task) -> Boolean = { it: Task -> !it.name.startsWith("neo") && !it.name.startsWith("compileService") }

tasks.withType<JavaCompile>().matching(notNeoTask).configureEach {
    source(project(":common").sourceSets.main.get().allSource)
    source(project(":common").sourceSets.getByName("vendored").allSource)
    source(project(":common").sourceSets.getByName("api").allSource)
    source(project(":common").sourceSets.getByName("desktop").allSource)
}

tasks.withType<Javadoc>().matching(notNeoTask).configureEach {
    source(project(":common").sourceSets.main.get().allJava)
}

tasks.withType<ProcessResources>().matching(notNeoTask).configureEach {
    from(project(":common").sourceSets.main.get().resources)
}

tasks.jar.get().destinationDirectory = rootDir.resolve("build").resolve("libs")

neoForge {
    // Specify the version of NeoForge to use.
    version = NEOFORGE_VERSION

    if (PARCHMENT_VERSION != null) {
        parchment {
            minecraftVersion = MINECRAFT_VERSION
            mappingsVersion = PARCHMENT_VERSION
        }
    }

    runs {
        create("client") {
            client()
        }
    }

    mods {
        create("sodium") {
            sourceSet(sourceSets.main.get())
        }
    }
}

fun includeDep(dependency: String, closure: Action<ExternalModuleDependency>) {
    dependencies.implementation(dependency, closure)
    dependencies.jarJar(dependency, closure)
}

fun includeDep(dependency: String) {
    dependencies.implementation(dependency)
    dependencies.jarJar(dependency)
}

fun includeAdditional(dependency: String) {
    includeDep(dependency)
    dependencies.additionalRuntimeClasspath(dependency)
}

tasks.named("compileTestJava").configure {
    enabled = false
}

dependencies {
    compileOnly(files(rootDir.resolve("DHApi.jar")))

    compileOnly(project.project(":common").sourceSets.main.get().output)
    compileOnly(project.project(":common").sourceSets.getByName("vendored").output)
    compileOnly(project.project(":common").sourceSets.getByName("headers").output)
    compileOnly(project.project(":common").sourceSets.getByName("api").output)
    includeDep("org.sinytra.forgified-fabric-api:fabric-api-base:0.4.42+d1308ded19")
    includeDep("org.sinytra.forgified-fabric-api:fabric-renderer-api-v1:3.4.0+acb05a3919")
    includeDep("org.sinytra.forgified-fabric-api:fabric-rendering-data-attachment-v1:0.3.48+73761d2e19")
    includeDep("org.sinytra.forgified-fabric-api:fabric-block-view-api-v2:1.0.10+9afaaf8c19")

    implementation("maven.modrinth", "sodium", "mc1.21.1-0.6.0-neoforge")
    includeAdditional("io.github.douira:glsl-transformer:2.0.1")
    includeAdditional("org.anarres:jcpp:1.4.14")
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)
