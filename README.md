# Portal

Portal is a KMP library that helps manage navigating and architecting Compose UI apps. It works with both Jetpack and Jetbrains Compose (and theoretically any other future flavors).

It provides a simple API to manage displaying Composable functions with enter and exit transitions. There is a backstack, and saving/restoring state is a WIP.

### Setup

```
repositories {
  mavenCentral()
}

dependencies {
  implementation("com.eygraber:portal:0.9.5")
}
```

Snapshots can be found [here](https://s01.oss.sonatype.org/#nexus-search;gav~com.eygraber~portal~~~).

### Usage

```kotlin
enum class MyPortalKey {
  FirstScreen,
  SecondScreen
}

fun main() {
  val portals = PortalManager<MyPortalKey>()

  thread {
    Thread.sleep(1_000)

    portals.withTransaction {
      add(MyPortalKey.FirstScreen) {
        Text(text = "Hello, Portal!")
      }
    }
  }
  
  singleWindowApplication(title = "Portals") {
    Surface(
      modifier = Modifier.fillMaxSize()
    ) {
      portals.Render()
    }
  }
}
```

More to come here. Check out the samples for now.
