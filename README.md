# omni-crosswords-android
An Android app that fetches crosswords and allows users to complete them

## Development
Omni Crosswords uses Jetpack Compose

Use Android Studio to open this project

### Firebase
Currently, it is required to build this project with a firebase instance - 
if there are ideas for how to enable a "local mode" that would enable 
development without a linked firebase impl, please share!

Set up Firebase Cloud Firestore, and add the google-services.json file to the app directory.
Make sure anonymous authentication is enabled. Ensure there is a collection called "crosswords"
