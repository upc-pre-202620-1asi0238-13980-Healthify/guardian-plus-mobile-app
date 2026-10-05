# Health Monitoring — 1/3 Domain Layer

Connecting the **Health Monitoring** bounded context of `guardian-plus-platform` to the app, layer by layer,
following the easyvet architecture (the same one `emergencyalerting` already uses).

> Base package: `com.example.guardian_plus_mobile_app`
> Feature root: `features/healthmonitoring/`
> Each step has a **Try it** hint first. The answer key is folded in `<details>` — open it only after you tried.

---

## 0. The big picture (read once, 3 min)

### What the Home screen needs from the backend

| On screen | Backend source |
|---|---|
| Ritmo cardíaco **78 lpm** · "En rango normal" · "Hace 1 min" | `GET /vital-signs/live/{careRecipientProfileId}` → item with `vitalSignType = HR` |
| Presión arterial **118/76** | same endpoint → **two** items: `BP_SYS` and `BP_DIA` |
| Saturación **98 %**, Temperatura **36.7 °C**, Respiración **16 rpm** | same endpoint → `SPO2`, `TEMP`, `RESP_RATE` |
| "Elena está bien" / "Lecturas estables" | every item has `classification = WITHIN_RANGE` |
| "Pulsera conectada" | `GET /wearable-devices/care-recipient/{careRecipientProfileId}` → a `WRISTBAND` |
| Battery **68 %** | ⚠️ **not in the backend**. Hide it or leave it hard-coded in presentation. |

So for the connection we only need **2 endpoints**. Health reports (`/health-reports`) and history
(`/vital-signs/history`) belong to the "Salud" tab — they can come later.

### Response shapes (from `interfaces/rest/resources` in the backend)

```jsonc
// GET /api/v1/vital-signs/live/{careRecipientProfileId}   (LiveVitalSignsResource)
{
  "careRecipientProfileId": "6b0b...",
  "retrievedAt": "2026-10-05T14:31:00Z",
  "vitalSigns": [
    {
      "vitalSignId": "…uuid…",
      "vitalSignType": "HR",            // HR | BP_SYS | BP_DIA | SPO2 | TEMP | RESP_RATE
      "vitalSignTypeName": "Heart rate", // English, from the backend enum
      "unit": "bpm",
      "value": 78,
      "measuredAt": "2026-10-05T14:30:00Z",
      "normalMinimum": 60,
      "normalMaximum": 100,
      "classification": "WITHIN_RANGE", // BELOW_RANGE | WITHIN_RANGE | ABOVE_RANGE
      "liveSignal": true                // false if the reading is older than 60 s
    }
  ]
}

// GET /api/v1/wearable-devices/care-recipient/{careRecipientProfileId}   (List<WearableDeviceResource>)
[
  { "id": "…", "careRecipientProfileId": "…", "serialNumber": "GP-ESP32-S3-0001",
    "deviceType": "WRISTBAND", "linkedAt": "2026-10-01T10:00:00Z" }
]
```

### Final structure after the 3 guides

```
features/healthmonitoring/
├── domain/                              ← this guide
│   ├── VitalSignType.kt
│   ├── ReadingClassification.kt
│   ├── LiveVitalSign.kt
│   ├── LiveVitalSigns.kt
│   ├── DeviceType.kt
│   ├── WearableDevice.kt
│   ├── VitalSignRepository.kt
│   └── WearableDeviceRepository.kt
├── infrastructure/                      ← guide 2
│   ├── remote/   (DTOs, Retrofit services, mappers)
│   ├── repositories/   (…RepositoryImpl)
│   └── di/   (HealthMonitoringApiModule, HealthMonitoringRepositoryModule)
└── application/                         ← guide 3
    ├── GetLiveVitalSignsUseCase.kt
    └── GetWearableDevicesUseCase.kt
```

### Commit plan & time budget (~1 h)

| # | Guide | Commit | Time |
|---|---|---|---|
| 1 | Domain | `feat(health-monitoring): add domain models and repository contracts` | 10 min |
| 2 | Infra 0 | `refactor: move apiCall and errorMessage to core/network` | 5 min |
| 3 | Infra 1–3 | `feat(health-monitoring): add remote dtos, services and mappers` | 15 min |
| 4 | Infra 4–5 | `feat(health-monitoring): add repository implementations and hilt modules` | 10 min |
| 5 | Application | `feat(health-monitoring): add live vital signs and wearable device use cases` | 5 min |
| — | Verify against the backend | (no commit) | 15 min |

---

## The domain rule

The domain is **pure Kotlin**: no Retrofit, no Gson, no Android, no Hilt. It describes *what* the app
knows about health, not *how* it gets it. That's why it holds `Instant` and enums, never raw `String`s
from JSON — converting is the infrastructure's job.

Compare with `features/emergencyalerting/domain/` — it's the same idea (`Alert`, `Severity`, `AlertRepository`).

---

## Step 1 — Enums: `VitalSignType`, `ReadingClassification`, `DeviceType`

**Try it:** create the three enums in `features/healthmonitoring/domain/`. Copy the constant names
*exactly* from the backend (`VitalSignType.java`, `ReadingClassification.java`, `DeviceType.java`) — later
the mapper will use `valueOf(...)`, which fails if a single letter differs.

Bonus: give `ReadingClassification` a computed property `isOutOfRange` (look at how `Severity.allowsEscalation` is written).

> 🤔 Should `VitalSignType` also hold the normal ranges (60–100 for HR, etc.)?
> No — the backend already sends `normalMinimum`/`normalMaximum` with every reading. Duplicating them
> means two sources of truth that can drift apart.

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain

enum class VitalSignType {
    HR,
    BP_SYS,
    BP_DIA,
    SPO2,
    TEMP,
    RESP_RATE
}
```

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain

enum class ReadingClassification {
    BELOW_RANGE,
    WITHIN_RANGE,
    ABOVE_RANGE;

    val isOutOfRange: Boolean get() = this != WITHIN_RANGE
}
```

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain

enum class DeviceType {
    SMARTWATCH,
    WRISTBAND,
    PATCH
}
```
</details>

---

## Step 2 — `LiveVitalSign` (one reading)

**Try it:** a `data class` with one property per field of `LiveVitalSignResource`, but with *domain* types:

- ids → `String`
- `vitalSignType` → `VitalSignType` (rename it to just `type`)
- `value`, `normalMinimum`, `normalMaximum` → `Double`
- `measuredAt` → `java.time.Instant` (desugaring is already enabled in `build.gradle.kts`)
- `classification` → `ReadingClassification`

Add a one-line comment on `liveSignal` explaining what `false` means (it's not obvious).

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain

import java.time.Instant

data class LiveVitalSign(
    val id: String,
    val type: VitalSignType,
    val typeName: String,
    val unit: String,
    val value: Double,
    val measuredAt: Instant,
    val normalMinimum: Double,
    val normalMaximum: Double,
    val classification: ReadingClassification,
    // false when the reading is older than the live window (60 s): it's the last known value, not a live one
    val liveSignal: Boolean
)
```
</details>

---

## Step 3 — `LiveVitalSigns` (the whole panel)

This is the aggregate the Home screen will consume. Besides the 3 fields of `LiveVitalSignsResource`, give it
behavior so presentation doesn't have to search lists:

**Try it:**
1. `operator fun get(type: VitalSignType): LiveVitalSign?` → lets the UI write `vitals[VitalSignType.HR]`.
2. `val allWithinRange: Boolean` → drives "Elena está bien".
3. `val hasLiveSignal: Boolean` → `true` if *any* reading is live.

> 🤔 What does `allWithinRange` return for an **empty** list? With `none { … }` it's `true` — meaning
> "Elena está bien" with zero readings. Keep it in mind for the presentation layer (show "Sin lecturas" when the list is empty).

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain

import java.time.Instant

data class LiveVitalSigns(
    val careRecipientProfileId: String,
    val retrievedAt: Instant,
    val vitalSigns: List<LiveVitalSign>
) {
    /** Latest reading of [type], or null when the wearable never sent one. */
    operator fun get(type: VitalSignType): LiveVitalSign? = vitalSigns.firstOrNull { it.type == type }

    /** Every reading is inside the normal range of its type. */
    val allWithinRange: Boolean get() = vitalSigns.none { it.classification.isOutOfRange }

    val hasLiveSignal: Boolean get() = vitalSigns.any { it.liveSignal }
}
```
</details>

---

## Step 4 — `WearableDevice`

**Try it:** a `data class` mirroring `WearableDeviceResource`, using `DeviceType` and `Instant`.

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain

import java.time.Instant

data class WearableDevice(
    val id: String,
    val careRecipientProfileId: String,
    val serialNumber: String,
    val deviceType: DeviceType,
    val linkedAt: Instant
)
```
</details>

---

## Step 5 — Repository contracts (the "ports")

The domain **declares** what it needs; infrastructure will **implement** it. This is why the use cases and
ViewModels never know Retrofit exists (and why you can swap in a fake repository for previews/tests).

**Try it:** two interfaces, one `suspend` function each, returning `Result<…>` — same style as
`EmergencyContactRepository`.

- `VitalSignRepository.getLiveVitalSigns(careRecipientProfileId)`
- `WearableDeviceRepository.getWearableDevices(careRecipientProfileId)`

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain

interface VitalSignRepository {
    suspend fun getLiveVitalSigns(careRecipientProfileId: String): Result<LiveVitalSigns>
}
```

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain

interface WearableDeviceRepository {
    suspend fun getWearableDevices(careRecipientProfileId: String): Result<List<WearableDevice>>
}
```
</details>

---

## ✅ Check & commit

```bash
./gradlew :app:compileDebugKotlin
git add app/src/main/java/com/example/guardian_plus_mobile_app/features/healthmonitoring/domain
git commit -m "feat(health-monitoring): add domain models and repository contracts"
```

### Exam questions to ask yourself
- Why does the domain use `Instant` and enums instead of `String`?
- Why is the repository an `interface` in the domain and not a class?
- Why `Result<T>` instead of throwing exceptions?

➡️ Next: `02-INFRASTRUCTURE_GUIDE.md`
