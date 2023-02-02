plugins {
    id("com.android.application") version "7.4.0" apply false
    id("com.android.library") version "7.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("dev.rikka.tools.autoresconfig") version "1.2.2" apply false
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
}
