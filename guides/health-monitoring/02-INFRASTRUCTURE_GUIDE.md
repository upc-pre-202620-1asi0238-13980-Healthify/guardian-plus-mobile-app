# Health Monitoring — 2/3 Infrastructure Layer

Infrastructure is the **only** layer that knows the backend exists. It has three jobs:

1. **remote/** — talk HTTP: DTOs that match the JSON *exactly*, Retrofit services, mappers DTO → domain.
2. **repositories/** — implement the domain interfaces using the services.
3. **di/** — tell Hilt how to build all of it.

> Reference: `features/emergencyalerting/infrastructure/` (same pattern, already working).
> `Retrofit` itself is already provided by `core/di/NetworkModule.kt` — you just reuse it.

---

## Step 0 — Move `apiCall` to `core/network` (refactor, own commit)

`apiCall(...)` and `Response.errorMessage()` currently live in
`features/emergencyalerting/infrastructure/remote/`. If healthmonitoring imports them from there, one
feature depends on another feature's internals — exactly what bounded contexts are supposed to avoid.

**Try it (Android Studio does the work):**
1. Create package `core/network`.
2. Select `ApiCall.kt` and `ErrorResponseDto.kt` → right-click → **Refactor → Move…** (F6) → target `…core.network`.
3. Android Studio rewrites the imports in the 5 emergencyalerting `…RepositoryImpl` files. Check with:
   ```bash
   grep -rn "emergencyalerting.infrastructure.remote.apiCall" app/src/main/java   # must print nothing
   ```

```bash
./gradlew :app:compileDebugKotlin
# only these two folders, so your uncommitted Color.kt change stays out of this commit
git add -A app/src/main/java/com/example/guardian_plus_mobile_app/core/network \
           app/src/main/java/com/example/guardian_plus_mobile_app/features/emergencyalerting
git commit -m "refactor: move apiCall and errorMessage to core/network"
```

> Short on time? You *can* skip this and import from emergencyalerting — but be ready to defend it if
> the teacher asks. Moving it takes 2 minutes.

---

## Step 1 — DTOs (`infrastructure/remote/`)

DTO = **D**ata **T**ransfer **O**bject: a dumb copy of the JSON. Gson fills it by **property name**, so
names must match the backend's `…Resource` records letter by letter.

Rules (same as `AlertSummaryDto`):
- uuids and dates → `String` (the mapper parses them; Gson doesn't know `Instant`)
- enums → `String` (the mapper converts them)
- numbers (`BigDecimal` in Java) → `Double`

**Try it:** create 3 files from the JSON in guide 1:
- `LiveVitalSignDto` (from `LiveVitalSignResource`) — careful, the id is `vitalSignId`, not `id`
- `LiveVitalSignsDto` (from `LiveVitalSignsResource`) — contains `List<LiveVitalSignDto>`
- `WearableDeviceDto` (from `WearableDeviceResource`)

Put a KDoc line on top saying which endpoint each one comes from (like `AlertSummaryDto` does).

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote

/** Item of GET /vital-signs/live/{careRecipientProfileId} (LiveVitalSignResource). */
data class LiveVitalSignDto(
    val vitalSignId: String,
    val vitalSignType: String,
    val vitalSignTypeName: String,
    val unit: String,
    val value: Double,
    val measuredAt: String,
    val normalMinimum: Double,
    val normalMaximum: Double,
    val classification: String,
    val liveSignal: Boolean
)
```

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote

/** Body of GET /vital-signs/live/{careRecipientProfileId} (LiveVitalSignsResource). */
data class LiveVitalSignsDto(
    val careRecipientProfileId: String,
    val retrievedAt: String,
    val vitalSigns: List<LiveVitalSignDto>
)
```

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote

/** Item of GET /wearable-devices/care-recipient/{careRecipientProfileId} (WearableDeviceResource). */
data class WearableDeviceDto(
    val id: String,
    val careRecipientProfileId: String,
    val serialNumber: String,
    val deviceType: String,
    val linkedAt: String
)
```
</details>

---

## Step 2 — Retrofit services (`infrastructure/remote/`)

**Try it:** two interfaces, one `@GET` each, `suspend` functions returning `Response<…Dto>`
(look at `EmergencyContactService.getEmergencyContacts`).

- `VitalSignService.getLiveVitalSigns` → `vital-signs/live/{careRecipientProfileId}`
- `WearableDeviceService.getWearableDevices` → `wearable-devices/care-recipient/{careRecipientProfileId}`

> ⚠️ **No leading slash** in the path. The base URL is `…/api/v1/`; with `"/vital-signs/…"` Retrofit
> resolves from the host root and you lose `/api/v1` → 404. Classic exam trap.

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface VitalSignService {

    @GET("vital-signs/live/{careRecipientProfileId}")
    suspend fun getLiveVitalSigns(
        @Path("careRecipientProfileId") careRecipientProfileId: String
    ): Response<LiveVitalSignsDto>
}
```

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WearableDeviceService {

    @GET("wearable-devices/care-recipient/{careRecipientProfileId}")
    suspend fun getWearableDevices(
        @Path("careRecipientProfileId") careRecipientProfileId: String
    ): Response<List<WearableDeviceDto>>
}
```
</details>

---

## Step 3 — Mappers (`infrastructure/remote/`)

Extension functions `Dto.toDomain()` — see `AlertMappers.kt` / `EmergencyContactMappers.kt`.

**Try it:** `VitalSignMappers.kt` (2 functions) and `WearableDeviceMappers.kt` (1 function).
- `String` → enum: `VitalSignType.valueOf(vitalSignType)`
- `String` → `Instant`: `Instant.parse(measuredAt)`
- list: `vitalSigns.map { it.toDomain() }`

> 🤔 What if the backend adds a 7th vital sign type tomorrow? `valueOf` throws
> `IllegalArgumentException` → `apiCall` catches it and returns
> *"El servidor devolvió datos que la app no reconoce…"*. The app doesn't crash. That's why every call goes through `apiCall`.

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSign
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSigns
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.ReadingClassification
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import java.time.Instant

fun LiveVitalSignDto.toDomain(): LiveVitalSign = LiveVitalSign(
    id = vitalSignId,
    type = VitalSignType.valueOf(vitalSignType),
    typeName = vitalSignTypeName,
    unit = unit,
    value = value,
    measuredAt = Instant.parse(measuredAt),
    normalMinimum = normalMinimum,
    normalMaximum = normalMaximum,
    classification = ReadingClassification.valueOf(classification),
    liveSignal = liveSignal
)

fun LiveVitalSignsDto.toDomain(): LiveVitalSigns = LiveVitalSigns(
    careRecipientProfileId = careRecipientProfileId,
    retrievedAt = Instant.parse(retrievedAt),
    vitalSigns = vitalSigns.map { it.toDomain() }
)
```

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.DeviceType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.WearableDevice
import java.time.Instant

fun WearableDeviceDto.toDomain(): WearableDevice = WearableDevice(
    id = id,
    careRecipientProfileId = careRecipientProfileId,
    serialNumber = serialNumber,
    deviceType = DeviceType.valueOf(deviceType),
    linkedAt = Instant.parse(linkedAt)
)
```
</details>

```bash
./gradlew :app:compileDebugKotlin
git add app/src/main/java/com/example/guardian_plus_mobile_app/features/healthmonitoring/infrastructure/remote
git commit -m "feat(health-monitoring): add remote dtos, services and mappers"
```

---

## Step 4 — Repository implementations (`infrastructure/repositories/`)

**Try it:** `VitalSignRepositoryImpl` and `WearableDeviceRepositoryImpl`.
- `@Inject constructor(private val service: …Service)` — Hilt passes the service in.
- Implement the domain interface.
- Body = one `apiCall({ service.x(id) }) { dto -> dto.toDomain() }` (one-liner, like `getEmergencyContacts`).

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSigns
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignRepository
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.VitalSignService
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.toDomain
import javax.inject.Inject

class VitalSignRepositoryImpl @Inject constructor(
    private val service: VitalSignService
) : VitalSignRepository {

    override suspend fun getLiveVitalSigns(careRecipientProfileId: String): Result<LiveVitalSigns> =
        apiCall({ service.getLiveVitalSigns(careRecipientProfileId) }) { dto -> dto.toDomain() }
}
```

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.WearableDevice
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.WearableDeviceRepository
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.WearableDeviceService
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.toDomain
import javax.inject.Inject

class WearableDeviceRepositoryImpl @Inject constructor(
    private val service: WearableDeviceService
) : WearableDeviceRepository {

    override suspend fun getWearableDevices(careRecipientProfileId: String): Result<List<WearableDevice>> =
        apiCall({ service.getWearableDevices(careRecipientProfileId) }) { dtos -> dtos.map { it.toDomain() } }
}
```
(If you skipped Step 0, the import is `…emergencyalerting.infrastructure.remote.apiCall`.)
</details>

---

## Step 5 — Hilt modules (`infrastructure/di/`)

Two modules, two different mechanisms — know the difference for the exam:

| | `@Provides` (in an `object`) | `@Binds` (in an `interface`) |
|---|---|---|
| Use when | you must **call code** to build it (`retrofit.create(...)`) | you just say "interface X → class Y" |
| Example | `HealthMonitoringApiModule` | `HealthMonitoringRepositoryModule` |

**Try it:** copy the shape of `EmergencyAlertingApiModule` and `EmergencyAlertingRepositoryModule`.

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.di

import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.VitalSignService
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.WearableDeviceService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object HealthMonitoringApiModule {

    @Provides
    @Singleton
    fun provideVitalSignService(retrofit: Retrofit): VitalSignService {
        return retrofit.create(VitalSignService::class.java)
    }

    @Provides
    @Singleton
    fun provideWearableDeviceService(retrofit: Retrofit): WearableDeviceService {
        return retrofit.create(WearableDeviceService::class.java)
    }
}
```

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.di

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignRepository
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.WearableDeviceRepository
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.repositories.VitalSignRepositoryImpl
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.repositories.WearableDeviceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface HealthMonitoringRepositoryModule {

    @Binds
    fun bindVitalSignRepository(impl: VitalSignRepositoryImpl): VitalSignRepository

    @Binds
    fun bindWearableDeviceRepository(impl: WearableDeviceRepositoryImpl): WearableDeviceRepository
}
```
</details>

```bash
./gradlew :app:compileDebugKotlin     # Hilt errors (missing binding) show up here, at compile time
git add app/src/main/java/com/example/guardian_plus_mobile_app/features/healthmonitoring/infrastructure
git commit -m "feat(health-monitoring): add repository implementations and hilt modules"
```

---

## ⚠️ Which backend do you test against?

On **2026-10-05** the deployed API (`guardian-plus-api.chilecentral.cloudapp.azure.com`) answers
**404 "endpoint does not exist"** for `/vital-signs/live/…` and `/wearable-devices/…` — health monitoring
isn't deployed yet. Until it is, run `guardian-plus-platform` locally (see the end of guide 3).

### Exam questions to ask yourself
- Why do DTOs use `String` for dates when the domain uses `Instant`?
- What's the difference between `@Provides` and `@Binds`?
- Why `@Singleton` on the services?
- What happens if you write `@GET("/vital-signs/…")` with a leading slash?

➡️ Next: `03-APPLICATION_GUIDE.md`
