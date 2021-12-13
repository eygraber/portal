rootProject.name = "portals"

include(":portal")
include(":portal-android-serialization")
include(":portal-compose")
include(":portal-kodein-di")

include(":samples:kotlin-inject")
include(":samples:portal")
include(":samples:simple-portal")

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
