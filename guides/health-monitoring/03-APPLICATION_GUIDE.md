# Health Monitoring — 3/3 Application Layer

The application layer holds the **use cases**: one class per thing the user can do / see. The ViewModel
(presentation, next session) will call these — never the repository directly.

```
Presentation (ViewModel) ──► Application (UseCase) ──► Domain (Repository interface)
                                                              ▲
                                       Infrastructure (RepositoryImpl) implements it
```

The use case depends only on the **domain interface**. Hilt injects `VitalSignRepositoryImpl` behind it
because of the `@Binds` you wrote in guide 2.

> 🤔 "But the use case just forwards the call, why does it exist?"
> Today, yes. It's the place where logic goes when it appears (e.g. "only show the wristband",
> "combine BP_SYS + BP_DIA", "retry once"), without touching the ViewModel or the repository.
> It also makes the ViewModel depend on *one* action instead of a whole repository.

---

## Step 1 — `GetLiveVitalSignsUseCase`

**Try it:** in `features/healthmonitoring/application/`, copy the shape of
`emergencyalerting/application/GetEmergencyContactsUseCase.kt`:
- `@Inject constructor(private val repository: VitalSignRepository)`
- `suspend operator fun invoke(careRecipientProfileId: String): Result<LiveVitalSigns>`

`operator fun invoke` lets the ViewModel call it like a function: `getLiveVitalSigns(id)`.

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.application

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSigns
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignRepository
import javax.inject.Inject

class GetLiveVitalSignsUseCase @Inject constructor(
    private val repository: VitalSignRepository
) {
    suspend operator fun invoke(careRecipientProfileId: String): Result<LiveVitalSigns> =
        repository.getLiveVitalSigns(careRecipientProfileId)
}
```
</details>

---

## Step 2 — `GetWearableDevicesUseCase`

**Try it:** same shape, for `WearableDeviceRepository`, returning `Result<List<WearableDevice>>`.

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.application

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.WearableDevice
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.WearableDeviceRepository
import javax.inject.Inject

class GetWearableDevicesUseCase @Inject constructor(
    private val repository: WearableDeviceRepository
) {
    suspend operator fun invoke(careRecipientProfileId: String): Result<List<WearableDevice>> =
        repository.getWearableDevices(careRecipientProfileId)
}
```
</details>

```bash
./gradlew :app:compileDebugKotlin
git add app/src/main/java/com/example/guardian_plus_mobile_app/features/healthmonitoring/application
git commit -m "feat(health-monitoring): add live vital signs and wearable device use cases"
```

---

## Step 3 — Verify against a real backend (no commit, ~15 min)

The deployed API doesn't have health monitoring yet (404), so run it locally.

### 3.1 Start the platform
Run `guardian-plus-platform` from IntelliJ (or `./mvnw spring-boot:run`) → listens on `:8080`.

### 3.2 Seed data for Elena (`DemoSession.CARE_RECIPIENT_PROFILE_ID`)

```bash
API=http://localhost:8080/api/v1
CR=6b0b1a3e-5f5e-4d6e-9a59-3c1c1f6c2b10

# 1. Link the wristband — copy the "id" from the response
curl -s -X POST $API/wearable-devices -H 'Content-Type: application/json' \
  -d "{\"careRecipientProfileId\":\"$CR\",\"serialNumber\":\"GP-ESP32-S3-0001\",\"deviceType\":\"WRISTBAND\"}"

DEV=<paste-the-device-id-here>
NOW=$(date -u +%Y-%m-%dT%H:%M:%SZ)

# 2. One reading per type (the same values as the Figma)
for pair in HR:78 BP_SYS:118 BP_DIA:76 SPO2:98 TEMP:36.7 RESP_RATE:16; do
  curl -s -o /dev/null -w "${pair%%:*} %{http_code}\n" -X POST $API/vital-signs -H 'Content-Type: application/json' \
    -d "{\"wearableDeviceId\":\"$DEV\",\"careRecipientProfileId\":\"$CR\",\"vitalSignType\":\"${pair%%:*}\",\"value\":${pair##*:},\"measuredAt\":\"$NOW\"}"
done

# 3. What the app will receive
curl -s $API/vital-signs/live/$CR
curl -s $API/wearable-devices/care-recipient/$CR
```
Every reading should print `201`. After 60 s `liveSignal` flips to `false` — that's expected (no live wristband).

### 3.3 Point the app at your machine
In `local.properties` (git-ignored, already supported by `build.gradle.kts`):
```properties
api.base.url=http://127.0.0.1:8080/api/v1/
```
And with the emulator/phone connected:
```bash
adb reverse tcp:8080 tcp:8080
```

### 3.4 Prove the chain works before building any UI
There's no screen yet, so do a throw-away check: temporarily inject `GetLiveVitalSignsUseCase` in any
existing `@HiltViewModel` (e.g. `ActiveAlertsViewModel`), call it in `init { viewModelScope.launch { … } }`
and `Log.d("HM", result.toString())`. Open Logcat, filter `HM`, and you should see `Success(LiveVitalSigns(…))`.
**Don't commit this** — `git restore` that file afterwards.

| You see | Meaning |
|---|---|
| `Success(LiveVitalSigns(... vitalSigns=[...6 items]))` | 🎉 connected |
| `Success(... vitalSigns=[])` | backend up, no readings → re-run 3.2 |
| `No se pudo conectar con el servidor` | backend down, wrong `api.base.url`, or missing `adb reverse` |
| `El servidor respondió con el código 404` | wrong path (leading slash?) or hitting the deployed API |
| `El servidor devolvió datos que la app no reconoce` | DTO field name / enum value mismatch |

---

## Done with the connection ✅

What presentation (next session) will use:

```kotlin
val vitals: LiveVitalSigns            // from GetLiveVitalSignsUseCase
vitals[VitalSignType.HR]?.value        // 78.0  → "78 lpm"
vitals[VitalSignType.BP_SYS]?.value    // 118.0 ┐
vitals[VitalSignType.BP_DIA]?.value    //  76.0 ┘→ "118/76 mmHg"
vitals.allWithinRange                  // → "Elena está bien" / "En rango normal"
devices.any { it.deviceType == DeviceType.WRISTBAND }  // → "Pulsera conectada"
```

Notes for later: labels must be Spanish ("Ritmo cardíaco"), so map `VitalSignType` → label in
presentation instead of using `typeName` (it's English). Battery % isn't in the backend.

### Exam questions to ask yourself
- Why does the ViewModel call a use case instead of the repository?
- What does `operator fun invoke` give you?
- Where would you add "only consider readings with live signal"? (domain? use case? ViewModel?)
