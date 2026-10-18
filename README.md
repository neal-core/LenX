# LenX
A native Android client application built with Kotlin and Jetpack Compose that pairs with a desktop host to deliver real-time system monitoring, power alerts, and security notifications over a local TCP network. 
## Tech Stack
- **Language**: Kotlin (Coroutines, Flow)
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Persistence**: Jetpack Datastore (Preferences)
- **Networking**: Java Sockets / TCP Client implementation
## Key Features
- **Live Connection Management**: Clean IP address input configuration with instant connection validation and status feedback.
- **Modular Settings Control**: Toggle specific monitoring metrics (Power State, Session Locks, RAM Warnings) using Reactive Material 3 switches that instantly sync configuration payloads to the PC server.
- **Real-time Telemetry Cards**: Dynamic UI components that updater asynchronously as push packets arrive over the local socket bridge.
- **Material You Dark Theme**: Designed natively for OLED dark mode using deep charcoal surfaces and high-contrast accent colours.
## Prerequisites
- Android Studio (Koala or newer recommended).
- Android SDK 24+ (Nougat or newer)
- A local instance of the **LenX** server running on the same Wi-Fi network
## License
&copy; Laenel. All rights reserved
