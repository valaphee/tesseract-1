/*
 * MIT License
 *
 * Copyright (c) 2021, Valaphee.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.palantir.git-version") version "0.12.3"
    kotlin("jvm") version "1.5.31"
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
    maven("https://repo.codemc.org/repository/maven-public")
    mavenLocal()
}

group = "com.valaphee"
val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
val details = versionDetails()
version = "${details.lastTag}.${details.commitDistance}"

dependencies {
    api("com.esotericsoftware:kryo:5.2.0")
    api("com.fasterxml.jackson.module:jackson-module-afterburner:2.13.0")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    api("com.google.code.gson:gson:2.8.9")
    api("com.google.inject:guice:5.0.1")
    api("com.hazelcast:hazelcast-all:4.2.2")
    api("com.valaphee:foundry-databind:1.3.0.0")
    api("com.valaphee:foundry-math:1.3.0.0")
    api("commons-cli:commons-cli:1.4")
    api("io.github.classgraph:classgraph:4.8.129")
    api("io.netty:netty-all:4.1.69.Final")
    api("it.unimi.dsi:fastutil:8.5.6")
    api("jline:jline:2.14.6")
    api("network.ycc:netty-raknet-client:0.8-SNAPSHOT")
    api("network.ycc:netty-raknet-server:0.8-SNAPSHOT")
    api("org.apache.logging.log4j:log4j-core:2.14.1")
    api("org.apache.logging.log4j:log4j-iostreams:2.14.1")
    api("org.apache.logging.log4j:log4j-jul:2.14.1")
    api("org.apache.logging.log4j:log4j-slf4j-impl:2.14.1")
    api("org.bitbucket.b_c:jose4j:0.7.9")
    api("org.fusesource.leveldbjni:leveldbjni-all:1.8")
    api("org.jetbrains.kotlin:kotlin-reflect:1.5.30")
    api("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.4")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.2-native-mt")
    testApi("org.junit.jupiter:junit-jupiter:5.8.1")
    api("org.lz4:lz4-java:1.8.0")
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "16"
        targetCompatibility = "16"
    }

    withType<KotlinCompile> { kotlinOptions { jvmTarget = "16" } }

    withType<Test> { useJUnitPlatform() }

}

signing {
    useGpgCmd()
    sign(publishing.publications)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom.apply {
                name.set("Tesseract")
                description.set("Experience Minecraft in a different way.")
                url.set("https://valaphee.com")
                scm {
                    connection.set("https://github.com/valaphee/tesseract.git")
                    developerConnection.set("https://github.com/valaphee/tesseract.git")
                    url.set("https://github.com/valaphee/tesseract")
                }
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://raw.githubusercontent.com/valaphee/tesseract/master/LICENSE.txt")
                    }
                }
                developers {
                    developer {
                        id.set("valaphee")
                        name.set("Valaphee")
                        email.set("iam@valaphee.com")
                        roles.add("owner")
                    }
                }
            }

            from(components["java"])
        }
    }
}
