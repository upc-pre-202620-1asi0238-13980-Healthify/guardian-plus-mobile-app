# Guardian+ Mobile App

Android app for the Guardian+ Care Circle: a family member follows the vital signs, alerts and location of the person under their care, measured by a wearable.

Built with Kotlin, Jetpack Compose, Hilt and Retrofit. It talks to the [Guardian+ platform](https://github.com/upc-pre-202620-1asi0238-13980-Healthify/guardian-plus-platform) REST API.

## Prerequisites

- Android SDK with the **API 37** platform (`compileSdk` and `targetSdk` are 37)
- An emulator or phone running Android 7.0 (API 24) or newer
- Any JDK 17+ to launch the Gradle wrapper; Gradle downloads the JDK 25 it builds with on its own
- `adb` on your `PATH` (`$ANDROID_HOME/platform-tools`)

## Run against the deployed backend

With no extra configuration the app uses the platform deployed on Azure (`https://guardian-plus-api.chilecentral.cloudapp.azure.com/api/v1/`):

```bash
./gradlew installDebug
adb shell am start -n com.example.guardian_plus_mobile_app/.MainActivity
```

## Run against a local backend

Use this to see the readings of the IoT simulator move in real time while you develop.

### 1. Start the platform stack

In a checkout of [guardian-plus-platform](https://github.com/upc-pre-202620-1asi0238-13980-Healthify/guardian-plus-platform), with [guardian-plus-iot-simulator](https://github.com/upc-pre-202620-1asi0238-13980-Healthify/guardian-plus-iot-simulator) cloned next to it, start PostgreSQL, the MQTT broker, the backend and the simulator:

```bash
docker compose -f docker-compose.dev.yml --profile full up -d --build
```

| Service | URL |
|---|---|
| Backend | `http://localhost:8080` (Swagger UI at `/swagger-ui/index.html`) |
| IoT simulator | `http://localhost:5055` |

See the platform's README for the details and for running the backend outside Docker.

### 2. Point the app to it

The base URL comes from `api.base.url` in your own `local.properties` (git-ignored), read at build time into `BuildConfig.API_BASE_URL`:

```properties
# Emulator: 10.0.2.2 is the emulator's alias for your machine
api.base.url=http://10.0.2.2:8080/api/v1/
```

On a physical phone connected over USB, use the loopback address and forward the port instead (again every time the phone reconnects):

```properties
api.base.url=http://127.0.0.1:8080/api/v1/
```

```bash
adb reverse tcp:8080 tcp:8080
```

Plain HTTP is only allowed towards these hosts (`res/xml/network_security_config.xml`). The value is compiled in, so **rebuild after changing it**.

### 3. Give the demo person a wristband

There is no login yet: the app always shows the person in `core/session/DemoSession.kt` (Elena Rojas, `6b0b1a3e-5f5e-4d6e-9a59-3c1c1f6c2b10`). The simulator only emits readings for linked wearables, so on a fresh database link one to her:

```bash
curl -X POST http://localhost:8080/api/v1/wearable-devices \
  -H 'Content-Type: application/json' \
  -d '{"careRecipientProfileId":"6b0b1a3e-5f5e-4d6e-9a59-3c1c1f6c2b10","serialNumber":"GP-ESP32-S3-0100","deviceType":"WRISTBAND"}'
```

A `409` means it is already linked. The simulator loads the devices only at startup, so make it reload them:

```bash
curl -X POST http://localhost:5055/update
```

The data lives in a Docker volume, so this is needed only once.

### 4. Install and open the app

```bash
./gradlew installDebug
adb shell am start -n com.example.guardian_plus_mobile_app/.MainActivity
```

The home and health screens refresh the live readings on their own; the simulator publishes a new reading every 10 seconds.

## Troubleshooting

| Symptom | Cause and fix |
|---|---|
| "No se pudo conectar con el servidor" on the emulator | The backend is not running (`curl http://localhost:8080/api/v1/vital-sign-types`), or `api.base.url` changed and the app was not rebuilt. |
| Same message on a physical phone | `adb reverse tcp:8080 tcp:8080` is missing: it is lost every time the phone disconnects. |
| The app loads but the values never change | The demo person has no wristband (step 3), or the simulator is not publishing: `curl http://localhost:5055/health` should show `"mqttConnected": true` and a `deviceCount` above 0. |
| `10.0.2.2` unreachable after installing by other means | From `targetSdk` 37, Android 17 blocks the local network unless the app holds `ACCESS_LOCAL_NETWORK`. Debug builds declare it (`src/debug/AndroidManifest.xml`) and `./gradlew installDebug` grants it (`installOptions "-g"`). An APK installed another way needs `adb shell pm grant com.example.guardian_plus_mobile_app android.permission.ACCESS_LOCAL_NETWORK`. |

## Tests

```bash
./gradlew test                  # unit tests
./gradlew connectedAndroidTest  # instrumented tests, needs a running emulator or phone
```
