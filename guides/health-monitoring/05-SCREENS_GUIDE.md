# Health Monitoring — 5 · Presentation: the Screens (final)

Guide 4 done. ViewModel think. Now screen draw.
Two prototypes: **Inicio** (Home) and **Salud** (Ahora · Historial).

> Rule same as before: **Try it** first. Answer key folded. Peek only after trying.
> Architecture = **easyvet-mobile**. Same shape everywhere. Section 0 show it.

---

## 0. Big picture (read once)

### 0.1 Easyvet shape — copy this

Open these in `easyvet-mobile`. Read slow. Every screen here copy them.

| Easyvet file | Lesson |
|---|---|
| `features/catalog/presentation/home/HomeScreen.kt` | Screen gets `viewModel = hiltViewModel()` as default param. Reads `uiState.collectAsStateWithLifecycle()`. `when { content / loading / error }`. |
| `features/catalog/presentation/home/component/ProductCard.kt` | Small piece = own file in `component/`. `modifier` first param. Click = lambda param. |
| `features/catalog/presentation/navigation/CatalogNavGraph.kt` | `@Serializable` route objects. `fun NavGraphBuilder.xxxNavGraph(...)`. Screen never touch `navController`. Screen get lambdas. |
| `navigation/AppNavHost.kt` | App shell just call each feature graph. |
| Any `@Preview` | `EasyVetTheme(dynamicColor = false) { ... }` |

Guardian adds one trick on top (see `emergencyalerting/presentation/activealerts/ActiveAlertsScreen.kt`):

```
XxxScreen(viewModel)      ← gets ViewModel, collects state, wires callbacks
   └─ XxxContent(uiState) ← pure drawing. No ViewModel. Preview-able.
```

Why? `@Preview` cannot build Hilt ViewModel. `Content` takes plain state → preview any state you want (loading, error, out of range).

### 0.2 Final folder

Today you also **tidy** guide 4 files. They sit in `presentation/` root. Easyvet put each screen in own folder.

```
features/healthmonitoring/
├── domain/
│   ├── VitalSignReading.kt                  ← NEW (Part A)
│   └── repositories/VitalSignRepository.kt  ← +1 function
├── application/
│   └── GetVitalSignHistoryUseCase.kt        ← NEW
├── infrastructure/
│   ├── remote/dto/VitalSignDto.kt           ← NEW
│   ├── remote/services/VitalSignService.kt  ← +1 endpoint
│   └── repositories/VitalSignRepositoryImpl.kt ← +1 function
└── presentation/
    ├── common/
    │   ├── VitalSignLabels.kt     ← MOVED from presentation/ + more labels
    │   └── ReadingBadge.kt        ← "Normal" / "En observación"
    ├── livevitals/
    │   ├── LiveVitalsUiState.kt   ← MOVED + `now`
    │   ├── LiveVitalsViewModel.kt ← MOVED + ServerClock
    │   ├── LiveVitalsScreen.kt    ← Salud › Ahora
    │   └── component/VitalTile.kt
    ├── home/
    │   ├── HomeScreen.kt
    │   └── component/ (HomeHeader, CareRecipientCard, StatusHeroCard,
    │                  QuickActionsRow, VitalsSummaryCard, UpcomingCard)
    ├── vitalhistory/
    │   ├── VitalHistoryUiState.kt
    │   ├── VitalHistoryViewModel.kt
    │   ├── VitalHistoryScreen.kt  ← Salud › Historial
    │   └── component/ (VitalTypeFilterRow, WeeklyAverageCard, WeeklyLineChart, ReadingItem)
    ├── health/
    │   ├── HealthScreen.kt        ← header + Ahora/Historial tabs
    │   └── component/ (HealthHeader, HealthTabRow)
    └── navigation/
        └── HealthMonitoringNavGraph.kt
```

Big, yes. But every file small. One job each.

### 0.3 Map prototype → data

What backend give. What we fake. Be honest in exam.

| Prototype piece | Source |
|---|---|
| "Buenos días, María", "MR" | `DemoSession.CURRENT_USER_NAME` + clock hour |
| "Estás cuidando a Elena Rojas" | `DemoSession.CARE_RECIPIENT_NAME` |
| "Elena está bien" / warning | `uiState.allWithinRange` ✅ real |
| "EN LÍNEA" | `uiState.hasLiveSignal` ✅ real |
| "Actualizado hace 2 min" | `vitals.retrievedAt` + `uiState.now` ✅ real |
| "Pulsera conectada" | `uiState.hasWristband` ✅ real |
| "68 %" battery | ❌ not in backend → **hide** |
| Bell badge "2" | ❌ other context → hide (bonus later) |
| Llamar | `context.dial(...)` (already in `emergencyalerting/presentation/common/Dialer.kt`) |
| Videollamada | ❌ stub (does nothing yet) |
| Ubicación | callback → Location tab |
| Vital numbers + "En rango normal" | `vitals[type]` + `classification` ✅ real |
| HR mini bars | ❌ decorative (live endpoint give 1 value, not series) |
| "Lo próximo" / "Última alerta" | ❌ other contexts → static card (bonus) |
| Historial chart, avg, min, max | `GET /vital-signs/history/{id}?from&to` ✅ real (Part A) |
| Historial "Normal"/"En Observación" | history rows have **no** classification → compare with ranges from live endpoint |
| Exportar PDF / Reporte semanal | ❌ stub (health-reports = another session) |

### 0.4 Strings first

Project uses `strings.xml` + `stringResource(...)` (easyvet uses raw text; we already chose strings). Add these once to `res/values/strings.xml`. Then forget.

```xml
<!-- Health monitoring: home -->
<string name="home_greeting_morning">Buenos días, %1$s</string>
<string name="home_greeting_afternoon">Buenas tardes, %1$s</string>
<string name="home_greeting_evening">Buenas noches, %1$s</string>
<string name="home_title_ok">Todo bajo control</string>
<string name="home_title_attention">Revisa a %1$s</string>
<string name="home_caring_for">Estás cuidando a</string>
<string name="home_change">Cambiar</string>
<string name="home_status_online">ESTADO ACTUAL · EN LÍNEA</string>
<string name="home_status_offline">ESTADO ACTUAL · SIN SEÑAL</string>
<string name="home_status_ok">%1$s está bien</string>
<string name="home_status_attention">%1$s necesita atención</string>
<string name="home_status_stable">Lecturas estables · Actualizado %1$s</string>
<string name="home_status_unstable">Lecturas fuera de rango · Actualizado %1$s</string>
<string name="home_wristband_connected">Pulsera conectada</string>
<string name="home_wristband_missing">Sin pulsera vinculada</string>
<string name="home_action_call">Llamar</string>
<string name="home_action_video">Videollamada</string>
<string name="home_action_location">Ubicación</string>
<string name="home_vitals_title">Signos vitales</string>
<string name="home_see_detail">Ver detalle</string>
<string name="home_upcoming_title">Lo próximo</string>
<string name="home_see_agenda">Ver agenda</string>
<!-- Health monitoring: badges -->
<string name="reading_in_range">En rango normal</string>
<string name="reading_normal">Normal</string>
<string name="reading_observation">En observación</string>
<!-- Health monitoring: health tab -->
<string name="health_title">Salud</string>
<string name="health_subtitle_online">%1$s · En línea</string>
<string name="health_subtitle_offline">%1$s · Sin señal</string>
<string name="health_tab_now">Ahora</string>
<string name="health_tab_history">Historial</string>
<string name="health_all_vitals">Todos los signos vitales</string>
<string name="health_weekly_average">PROMEDIO SEMANAL</string>
<string name="health_min">Mín</string>
<string name="health_max">Máx</string>
<string name="health_no_readings">Sin lecturas esta semana</string>
<string name="health_export_pdf">Exportar PDF</string>
<string name="health_weekly_report">Reporte semanal</string>
<string name="health_today">Hoy</string>
<string name="health_yesterday">Ayer</string>
<string name="placeholder_soon">Próximamente</string>
```

---

# Part A — Small data slice for "Historial"

Guides 1–3 built only **live**. Historial need **history**. Same four layers, small. You know this road. Go fast.

Backend (already exists, `VitalSignsController.java`):

```
GET /api/v1/vital-signs/history/{careRecipientProfileId}?from=2026-09-30&to=2026-10-06
→ [ { "id", "wearableDeviceId", "careRecipientProfileId",
      "vitalSignType": "HR", "value": 78.0, "measuredAt": "2026-10-06T14:32:00Z", ... } ]
```

Oldest first. **No** classification. **No** range. Remember that.

## Step A1 — Domain: `VitalSignReading`

**Try it:** `domain/VitalSignReading.kt`. Four fields: `id`, `type: VitalSignType`, `value: Double`, `measuredAt: Instant`.
Then in `VitalSignRepository` add:
`suspend fun getVitalSignHistory(careRecipientProfileId: String, from: LocalDate, to: LocalDate): Result<List<VitalSignReading>>`

> 🤔 Why new class, not reuse `LiveVitalSign`? Live has `classification`, `liveSignal`, ranges. History has none. Reuse = lie with fake values.

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.domain

import java.time.Instant

// One past reading. Unlike LiveVitalSign it carries no classification: the history endpoint does not send it
data class VitalSignReading(
    val id: String,
    val type: VitalSignType,
    val value: Double,
    val measuredAt: Instant
)
```

```kotlin
// VitalSignRepository.kt — add
suspend fun getVitalSignHistory(
    careRecipientProfileId: String,
    from: LocalDate,
    to: LocalDate
): Result<List<VitalSignReading>>
```
</details>

## Step A2 — Infrastructure: DTO + service + impl

**Try it:**
1. `remote/dto/VitalSignDto.kt` — only fields you use: `id`, `vitalSignType`, `value`, `measuredAt` (String). Gson ignore extra JSON fields.
2. `VitalSignService` — new `@GET("vital-signs/history/{careRecipientProfileId}")`. `from`/`to` are `@Query` Strings (`"2026-10-06"`).
3. `VitalSignRepositoryImpl` — `apiCall({ ... }) { dtos -> dtos.map { ... } }`. `LocalDate.toString()` already gives ISO `2026-10-06`.

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.dto

data class VitalSignDto(
    val id: String,
    val vitalSignType: String,
    val value: Double,
    val measuredAt: String
)
```

```kotlin
// VitalSignService.kt — add (import retrofit2.http.Query)
@GET("vital-signs/history/{careRecipientProfileId}")
suspend fun getVitalSignHistory(
    @Path("careRecipientProfileId") careRecipientProfileId: String,
    @Query("from") from: String,
    @Query("to") to: String
): Response<List<VitalSignDto>>
```

```kotlin
// VitalSignRepositoryImpl.kt — add
override suspend fun getVitalSignHistory(
    careRecipientProfileId: String,
    from: LocalDate,
    to: LocalDate
): Result<List<VitalSignReading>> =
    apiCall({ service.getVitalSignHistory(careRecipientProfileId, from.toString(), to.toString()) }) { dtos ->
        dtos.map { dto ->
            VitalSignReading(
                id = dto.id,
                type = VitalSignType.valueOf(dto.vitalSignType),
                value = dto.value,
                measuredAt = Instant.parse(dto.measuredAt)
            )
        }
    }
```
</details>

## Step A3 — Application: `GetVitalSignHistoryUseCase`

**Try it:** copy `GetLiveVitalSignsUseCase`. Params `(careRecipientProfileId, from, to)`.

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.application

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignReading
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories.VitalSignRepository
import java.time.LocalDate
import javax.inject.Inject

class GetVitalSignHistoryUseCase @Inject constructor(
    private val repository: VitalSignRepository
) {
    suspend operator fun invoke(
        careRecipientProfileId: String,
        from: LocalDate,
        to: LocalDate
    ): Result<List<VitalSignReading>> = repository.getVitalSignHistory(careRecipientProfileId, from, to)
}
```
</details>

No Hilt change. `@Binds` already there. Ask yourself why. (Exam question 1.)

```bash
./gradlew :app:compileDebugKotlin
git commit -am "feat(health-monitoring): add vital sign history slice"   # + git add new files first
```

---

# Part B — Tidy + shared pieces

## Step B1 — Move guide 4 files

Android Studio: right-click file → **Refactor › Move**. It fix `package` + imports for you.

| File | To |
|---|---|
| `LiveVitalsUiState.kt`, `LiveVitalsViewModel.kt` | `presentation/livevitals/` |
| `VitalSignsLabels.kt` | `presentation/common/` (rename `VitalSignLabels.kt`, typo `Signs`) |

Compile. Green? Go.

## Step B2 — More labels

Prototype need three more things per type:
- Short chip text: Ritmo · Presión · SpO₂ · Temp · Respir
- Icon (heart, activity, thermometer…)
- Value text: HR `78`, TEMP `36.6`, SPO2 `98`. Temp has decimal. Others not.

Icons: project has `ic_activity`, `ic_watch`, `ic_phone`, `ic_video`, `ic_map_pin`, `ic_pill`, `ic_battery_low`, `ic_check`, `ic_chevron_right`. **No** heart, thermometer, lungs, droplet.
Get them from Lucide (same set team used): `heart`, `thermometer`, `wind`, `droplet`, `shield-check`, `search`, `download`, `bar-chart-2`. Android Studio: **File › New › Vector Asset › Local file (SVG)**. Name `ic_heart`, `ic_thermometer`, etc.

**Try it:** in `common/VitalSignLabels.kt` add:
- `val VitalSignType.shortLabel: String` (when, no else)
- `@get:DrawableRes val VitalSignType.iconRes: Int`
- `fun VitalSignType.format(value: Double): String`

<details><summary>Answer key</summary>

```kotlin
// Chip text of the "Historial" filter row
val VitalSignType.shortLabel: String
    get() = when (this) {
        VitalSignType.HR -> "Ritmo"
        VitalSignType.BP_SYS, VitalSignType.BP_DIA -> "Presión"
        VitalSignType.SPO2 -> "SpO₂"
        VitalSignType.TEMP -> "Temp"
        VitalSignType.RESP_RATE -> "Respir"
    }

@get:DrawableRes
val VitalSignType.iconRes: Int
    get() = when (this) {
        VitalSignType.HR -> R.drawable.ic_heart
        VitalSignType.BP_SYS, VitalSignType.BP_DIA -> R.drawable.ic_bar_chart_2
        VitalSignType.SPO2 -> R.drawable.ic_activity
        VitalSignType.TEMP -> R.drawable.ic_thermometer
        VitalSignType.RESP_RATE -> R.drawable.ic_wind
    }

// Temperature is the only one read with a decimal (36.6 °C); the rest are whole numbers
fun VitalSignType.format(value: Double): String = when (this) {
    VitalSignType.TEMP -> "%.1f".format(value)
    else -> value.roundToInt().toString()
}
```
</details>

## Step B3 — `ReadingBadge`

Two looks:
- in range → mint (`primaryContainer` / `onPrimaryContainer`) → "Normal" / "En rango normal"
- out → yellow (`noticeContainer` / `onNoticeContainer`) → "En observación"

Shape already exists: `emergencyalerting/presentation/common/StatusChip.kt`. **Reuse**. Don't copy.

**Try it:** `common/ReadingBadge.kt`. Params: `withinRange: Boolean`, `long: Boolean = false` (long = "En rango normal").

<details><summary>Answer key</summary>

```kotlin
@Composable
fun ReadingBadge(modifier: Modifier = Modifier, withinRange: Boolean, long: Boolean = false) {
    val scheme = MaterialTheme.colorScheme
    val text = when {
        !withinRange -> stringResource(R.string.reading_observation)
        long -> stringResource(R.string.reading_in_range)
        else -> stringResource(R.string.reading_normal)
    }
    StatusChip(
        modifier = modifier,
        text = text,
        container = if (withinRange) scheme.primaryContainer else scheme.noticeContainer,
        content = if (withinRange) scheme.onPrimaryContainer else scheme.onNoticeContainer
    )
}

@Preview
@Composable
private fun ReadingBadgePreview() {
    GuardianTheme(dynamicColor = false) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ReadingBadge(withinRange = true)
            ReadingBadge(withinRange = false)
        }
    }
}
```
</details>

## Step B4 — Teach LiveVitals about time

"Actualizado hace 2 min" / "Hace 1 min" need **now**. Phone clock lie. Server clock no lie → `core/time/ServerClock.kt`.
Same trick `ActiveAlertsUiState.now` uses. Go look.

**Try it:**
1. `LiveVitalsUiState` + `val now: Instant = Instant.EPOCH`.
2. Also derived `val bloodPressureText: String?` → `"118/76"` or null if one half missing.
3. `LiveVitalsViewModel` inject `private val serverClock: ServerClock`. In `refresh()` success + failure: `now = serverClock.now()`.

Then time text = `relativeTime(from, now)` from `emergencyalerting/presentation/common/AlertLabels.kt`. Reuse.

<details><summary>Answer key</summary>

```kotlin
// LiveVitalsUiState — new field + derived
val now: Instant = Instant.EPOCH,
...
// The backend stores systolic and diastolic as two readings; the screen shows them as one
val bloodPressureText: String?
    get() {
        val systolic = vitals?.get(VitalSignType.BP_SYS) ?: return null
        val diastolic = vitals?.get(VitalSignType.BP_DIA) ?: return null
        return "${systolic.value.roundToInt()}/${diastolic.value.roundToInt()}"
    }
```

```kotlin
// LiveVitalsViewModel
class LiveVitalsViewModel @Inject constructor(
    private val getLiveVitalSigns: GetLiveVitalSignsUseCase,
    private val getWearableDevices: GetWearableDevicesUseCase,
    private val serverClock: ServerClock
) : ViewModel() {
    ...
    .onSuccess { vitals ->
        _uiState.update {
            it.copy(isLoading = false, errorMessage = null, vitals = vitals,
                    hasWristband = hasWristband, now = serverClock.now())
        }
    }
    // same `now = serverClock.now()` in onFailure
```
</details>

> 🤔 Why `now` in state, not `Instant.now()` inside composable? Composable must be pure. Same state → same picture. Also preview can fake time.

---

# Part C — Home screen (Inicio)

Uses `LiveVitalsViewModel`. **No new ViewModel.** Home only read live vitals.

Layout top → bottom (prototype 1):

```
LazyColumn
 ├─ HomeHeader          MR · Buenos días, María · Todo bajo control · 🔔
 ├─ CareRecipientCard   E · Estás cuidando a Elena Rojas · Cambiar >
 ├─ StatusHeroCard      green card · Elena está bien · Pulsera conectada
 ├─ QuickActionsRow     Llamar | Videollamada | Ubicación
 ├─ SectionTitle        Signos vitales ········ Ver detalle
 ├─ VitalsSummaryCard   HR big + grid 2×2
 └─ UpcomingCard        Lo próximo (static, bonus)
```

`LazyColumn` = scroll. Bottom bar already float over (app shell). Add `contentPadding` bottom so last card not hide.

## Step C1 — Components (one file each, `home/component/`)

Do in order. Each with `@Preview`. Check preview before next.

### `HomeHeader`

Params: `userInitials`, `userFirstName`, `allWithinRange`, `careRecipientFirstName`.
- Avatar: `Box(Modifier.size(48.dp).background(primary, shapes.medium))` + white `Text`.
- Greeting by hour: `LocalTime.now().hour` → `< 12` morning, `< 19` afternoon, else evening.
- Title: `allWithinRange` → "Todo bajo control", else "Revisa a Elena".
- Bell: `OutlinedIconButton` or `Surface(border=...)` + `Icon(ic_bell)`. No badge (no data).

### `CareRecipientCard`

`Surface(shape = shapes.medium, border = BorderStroke(1.dp, outline))` → `Row`: initial box (mint) · two texts · `Spacer(Modifier.weight(1f))` · "Cambiar" + `ic_chevron_right`. Click → `onChangeClick` (stub).

### `StatusHeroCard` ⭐ main one

Params: `careRecipientFirstName`, `allWithinRange`, `hasLiveSignal`, `updatedText: String`, `hasWristband`.

- Background `primary` when all OK. Out of range → `tertiary` (orange, theme already has). Color = meaning.
- Small caps label: online / offline string.
- Big title `headlineSmall`, `onPrimary`.
- Subtitle stable / unstable + `updatedText`.
- Wristband pill: `Row` with `background(onPrimary.copy(alpha = 0.12f), shapes.small)` + `ic_watch` + text. Battery → **skip**.
- Top-right shield icon in translucent box.

<details><summary>Answer key — StatusHeroCard</summary>

```kotlin
/** Green summary of the prototype: one glance tells if the care recipient is fine. */
@Composable
fun StatusHeroCard(
    modifier: Modifier = Modifier,
    careRecipientFirstName: String,
    allWithinRange: Boolean,
    hasLiveSignal: Boolean,
    updatedText: String,
    hasWristband: Boolean
) {
    val scheme = MaterialTheme.colorScheme
    // Orange instead of green as soon as one reading leaves its range
    val container = if (allWithinRange) scheme.primary else scheme.tertiary
    val onContainer = scheme.onPrimary
    val translucent = onContainer.copy(alpha = 0.12f)

    Surface(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, color = container) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(if (hasLiveSignal) R.string.home_status_online else R.string.home_status_offline),
                        style = MaterialTheme.typography.labelMedium,
                        color = onContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text = stringResource(
                            if (allWithinRange) R.string.home_status_ok else R.string.home_status_attention,
                            careRecipientFirstName
                        ),
                        style = MaterialTheme.typography.headlineSmall,
                        color = onContainer,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = stringResource(
                            if (allWithinRange) R.string.home_status_stable else R.string.home_status_unstable,
                            updatedText
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = onContainer.copy(alpha = 0.8f)
                    )
                }
                Box(
                    modifier = Modifier.size(48.dp).background(translucent, MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painterResource(R.drawable.ic_shield_check), contentDescription = null, tint = onContainer)
                }
            }
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .background(translucent, MaterialTheme.shapes.small)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(painterResource(R.drawable.ic_watch), null, tint = onContainer, modifier = Modifier.size(16.dp))
                // Battery level is not reported by the platform, so the prototype's "68 %" is left out
                Text(
                    text = stringResource(if (hasWristband) R.string.home_wristband_connected else R.string.home_wristband_missing),
                    style = MaterialTheme.typography.bodySmall,
                    color = onContainer
                )
            }
        }
    }
}
```
</details>

### `QuickActionsRow`

`Surface` with border → `Row(Modifier.height(IntrinsicSize.Min))` → 3 × `QuickAction(icon, label, onClick)` with `Modifier.weight(1f)`, `VerticalDivider` between.
`QuickAction` = `Column(clickable)` → mint `Box(56.dp)` with icon → label.
Params: `onCallClick`, `onVideoClick`, `onLocationClick`.

> 🤔 Why `IntrinsicSize.Min`? `VerticalDivider` has no height alone. Row take height of tallest child → divider fill it.

### `VitalsSummaryCard` ⭐ second main one

Params: `vitals: LiveVitalSigns`, `bloodPressureText: String?`, `now: Instant`.

Two parts in one bordered `Surface`:

**Top (HR):**
```
[♥ box]  Ritmo cardíaco          ▂▃▅▃▆█▃▅▆▄   ← decorative bars
         78 lpm
─────────────────────────────────
[En rango normal]           Hace 1 min
```
- `val hr = vitals[VitalSignType.HR]` → may be null → show `"--"`.
- Number style: `MaterialTheme.typography.dataMetric` (mono, already in `Type.kt`). Bigger: `.copy(fontSize = 36.sp)`.
- Bars: `Row(verticalAlignment = Bottom)` of `Box(width 4.dp, height h.dp, background primary.copy(alpha=0.6f))`. Heights = fixed list. Comment why fake.
- Chip: `ReadingBadge(withinRange = !hr.classification.isOutRange, long = true)`.
- Time: `relativeTime(hr.measuredAt, now)`.

**Bottom (grid 2×2):** `Row { Cell; Cell }` twice, with `HorizontalDivider` / `VerticalDivider`.
- Presión arterial → `bloodPressureText` + "mmHg"
- Saturación → SPO2
- Temperatura → TEMP
- Respiración → RESP_RATE

Make private `VitalCell(type: VitalSignType, valueText: String?, outOfRange: Boolean)`. Icon + `type.label` small, value `dataMetric` + `type.displayUnit` small. Out of range → value color `tertiary`.

> 🤔 Why not `LazyVerticalGrid`? Inside `LazyColumn` nested lazy scroll = crash ("infinite height"). 4 fixed cells → plain Rows.

<details><summary>Answer key — VitalCell + grid</summary>

```kotlin
@Composable
private fun VitalCell(
    modifier: Modifier = Modifier,
    type: VitalSignType,
    valueText: String?,
    outOfRange: Boolean
) {
    Column(modifier = modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
                painter = painterResource(type.iconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Text(type.label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 6.dp)) {
            Text(
                text = valueText ?: "--",
                style = MaterialTheme.typography.dataMetric,
                color = if (outOfRange) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = " " + type.displayUnit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Inside VitalsSummaryCard, below the heart rate block
HorizontalDivider(color = MaterialTheme.colorScheme.outline)
Row(modifier = Modifier.height(IntrinsicSize.Min)) {
    val systolic = vitals[VitalSignType.BP_SYS]
    VitalCell(
        modifier = Modifier.weight(1f),
        type = VitalSignType.BP_SYS,
        valueText = bloodPressureText,
        outOfRange = systolic?.classification?.isOutRange == true ||
            vitals[VitalSignType.BP_DIA]?.classification?.isOutRange == true
    )
    VerticalDivider(color = MaterialTheme.colorScheme.outline)
    vitals[VitalSignType.SPO2].let { spo2 ->
        VitalCell(
            modifier = Modifier.weight(1f),
            type = VitalSignType.SPO2,
            valueText = spo2?.let { VitalSignType.SPO2.format(it.value) },
            outOfRange = spo2?.classification?.isOutRange == true
        )
    }
}
HorizontalDivider(color = MaterialTheme.colorScheme.outline)
Row(modifier = Modifier.height(IntrinsicSize.Min)) {
    // same for TEMP and RESP_RATE
}
```
</details>

### `UpcomingCard` (static)

Routines context not in app yet. Hard-code "14:00 · Losartán 50 mg · 1 tableta" and "3 de 4 rutinas completadas · 75%". Comment: `// Static until Care Routines is built in the app`. Keep tiny.
Skip "Última alerta" now. Bonus at end.

## Step C2 — `HomeScreen` + `HomeContent`

Copy `ActiveAlertsScreen` / easyvet `HomeScreen` shape:

```kotlin
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: LiveVitalsViewModel = hiltViewModel(),
    onSeeVitalsClick: () -> Unit,
    onLocationClick: () -> Unit
)
```

Screen job:
- `val uiState by viewModel.uiState.collectAsStateWithLifecycle()`
- `val context = LocalContext.current` → call = `context.dial(DemoSession.CARE_RECIPIENT_PHONE)`
- Pass all to `HomeContent(uiState, ...)`.

Content job — `when`:
1. `uiState.vitals == null && uiState.isLoading` → spinner center
2. `uiState.vitals == null && uiState.errorMessage != null` → error + "Reintentar" → `onRetryClick` (= `viewModel::load`)
3. else → `LazyColumn` with cards.

> 🤔 Why check `vitals == null` first, not only `isLoading`? Auto-refresh every 10 s. If old numbers exist, keep them on screen. No flicker spinner. Guide 4 made failure keep old `vitals` — this is where it pays.

**Try it:** write both. `LazyColumn(contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(16.dp))`. One `item { }` per card.

<details><summary>Answer key — HomeContent</summary>

```kotlin
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: LiveVitalsUiState,
    onRetryClick: () -> Unit,
    onCallClick: () -> Unit,
    onLocationClick: () -> Unit,
    onSeeVitalsClick: () -> Unit
) {
    val vitals = uiState.vitals
    when {
        // Old readings stay visible during refreshes; the spinner is only for the very first load
        vitals == null && uiState.isLoading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        vitals == null -> ErrorState(
            modifier = modifier,
            message = uiState.errorMessage ?: stringResource(R.string.health_no_readings),
            onRetryClick = onRetryClick
        )

        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HomeHeader(
                    userInitials = DemoSession.CURRENT_USER_NAME.initials(),
                    userFirstName = DemoSession.CURRENT_USER_NAME.substringBefore(" "),
                    careRecipientFirstName = uiState.careRecipientFirstName,
                    allWithinRange = uiState.allWithinRange
                )
            }
            item { CareRecipientCard(name = DemoSession.CARE_RECIPIENT_NAME, onChangeClick = {}) }
            item {
                StatusHeroCard(
                    careRecipientFirstName = uiState.careRecipientFirstName,
                    allWithinRange = uiState.allWithinRange,
                    hasLiveSignal = uiState.hasLiveSignal,
                    updatedText = relativeTime(vitals.retrievedAt, uiState.now),
                    hasWristband = uiState.hasWristband
                )
            }
            item {
                QuickActionsRow(onCallClick = onCallClick, onVideoClick = {}, onLocationClick = onLocationClick)
            }
            item {
                SectionTitle(
                    title = stringResource(R.string.home_vitals_title),
                    action = stringResource(R.string.home_see_detail),
                    onActionClick = onSeeVitalsClick
                )
            }
            item {
                VitalsSummaryCard(vitals = vitals, bloodPressureText = uiState.bloodPressureText, now = uiState.now)
            }
            item {
                SectionTitle(title = stringResource(R.string.home_upcoming_title), action = stringResource(R.string.home_see_agenda), onActionClick = {})
            }
            item { UpcomingCard() }
        }
    }
}
```

`ErrorState`: copy the private one in `ActiveAlertsScreen.kt` (or move it to `core/designsystem/component/` and share — better).
`initials()`: private in `AlertsScreen.kt` → move to `core/` as `fun String.initials()` and reuse. Two copies = smell.
</details>

**Previews:** 3 at least. All OK · one out of range (`classification = ABOVE_RANGE` → card turn orange) · loading. Build fake `LiveVitalSigns` like `previewState` in `ActiveAlertsScreen.kt`.

---

# Part D — Salud screen (Ahora · Historial)

Same pattern as `emergencyalerting/presentation/alerts/AlertsScreen.kt`. Open it. Copy structure:

```
HealthScreen
 ├─ HealthHeader        MR · Elena Rojas · En línea · Salud · [Normal]
 ├─ HealthTabRow        Ahora | Historial      ← copy AlertsTabRow
 └─ Box(weight 1f)
      ├─ NOW     → LiveVitalsScreen
      └─ HISTORY → VitalHistoryScreen
```

## Step D1 — `HealthTabRow` + `HealthHeader`

- `enum class HealthTab { NOW, HISTORY }`
- `HealthTabRow` = `AlertsTabRow` with other strings. Yes, near-copy. Later can make generic `SegmentedTabRow<T>` in `core/designsystem`. Good exam talk, not needed today.
- `HealthHeader(userInitials, careRecipientName, hasLiveSignal, allWithinRange)`: avatar (mint bg this time) · subtitle "Elena Rojas · En línea" · title "Salud" · right `ReadingBadge(withinRange = allWithinRange)`. `HorizontalDivider` under.

## Step D2 — `HealthScreen`

```kotlin
@Composable
fun HealthScreen(modifier: Modifier = Modifier) {
    // Header needs live state too; same instance LiveVitalsScreen gets (same nav entry)
    val liveVitalsViewModel: LiveVitalsViewModel = hiltViewModel()
    val liveState by liveVitalsViewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf(HealthTab.NOW) }
    ...
}
```

> 🤔 Why `rememberSaveable` for tab, not ViewModel? Tab = pure UI choice. Survive rotation with saveable. ViewModel for data. `AlertsScreen` does same.
>
> 🤔 Header + "Ahora" both call `hiltViewModel<LiveVitalsViewModel>()`. Two requests? No. Same `NavBackStackEntry` → same instance. Pass it down anyway (`viewModel = liveVitalsViewModel`), clear to reader.

**Try it:** write `HealthScreen` + `HealthContent(tabContent: @Composable () -> Unit)` exactly like `AlertsScreen`/`AlertsContent`.

## Step D3 — "Ahora" tab: `LiveVitalsScreen`

Not in prototype image, but tab exists. Keep simple: `LazyColumn` of `VitalTile` — one per type (HR, BP, SPO2, TEMP, RESP).

`livevitals/component/VitalTile.kt`: bordered card, icon box, label, big value + unit, `ReadingBadge`, "Hace X". Basically `VitalCell` bigger + badge.
Show range under value: `"Normal: ${min}–${max} ${unit}"` from `normalMinimum`/`normalMaximum`. Caregiver love this.

Same `when` as Home (spinner only if `vitals == null`).

## Step D4 — "Historial": `VitalHistoryUiState` ⭐ brain

What screen need (prototype 2):
- `isLoading`, `errorMessage`
- `readings: List<VitalSignReading>` — 7 days, all types
- `ranges: Map<VitalSignType, ClosedFloatingPointRange<Double>>` — from live endpoint (history no send them)
- `selectedType: VitalSignType = VitalSignType.HR`
- `today: LocalDate`
- `zone: ZoneId = ZoneId.systemDefault()`

Derived (`get()`), like guide 4:
- `selectedReadings` → filter by `selectedType`
- `weeklyAverage: Double?` → `selectedReadings.map { it.value }.average()` (null if empty!)
- `weeklyMin`, `weeklyMax`
- `days: List<LocalDate>` → `(6 downTo 0).map { today.minusDays(it.toLong()) }`
- `dailyAverages: List<Double?>` → per day, avg of that day's readings, null if none
- `recentReadings: List<VitalSignReading>` → last 3 of **all** types, newest first, **skip `BP_DIA`** (merged into BP_SYS row)
- `fun isWithinRange(reading): Boolean` → `ranges[reading.type]?.contains(reading.value) ?: true`
- `fun diastolicFor(systolic): VitalSignReading?` → `BP_DIA` with same `measuredAt`

> 🤔 Why stats computed in UiState, not backend? Backend has no "weekly avg" endpoint. Data small (7 days). Pure function = easy test.
>
> 🤔 `average()` on empty list = `NaN`, not crash. NaN on screen = "NaN lpm". Ugly. So `takeIf { it.isNotEmpty() }?.average()`.
>
> 🤔 Why skip `BP_DIA`? Prototype show "122/80 mmHg" one row. Backend store two. Simulator send both same `measuredAt`. Pair by time.

<details><summary>Answer key — VitalHistoryUiState</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory

data class VitalHistoryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Oldest first, as the platform sends them
    val readings: List<VitalSignReading> = emptyList(),
    // History readings carry no range, so it is taken from the live endpoint
    val ranges: Map<VitalSignType, ClosedFloatingPointRange<Double>> = emptyMap(),
    val selectedType: VitalSignType = VitalSignType.HR,
    val today: LocalDate = LocalDate.now(),
    val zone: ZoneId = ZoneId.systemDefault()
) {
    val selectedReadings: List<VitalSignReading>
        get() = readings.filter { it.type == selectedType }

    val weeklyAverage: Double?
        get() = selectedReadings.takeIf { it.isNotEmpty() }?.map { it.value }?.average()

    val weeklyMin: Double? get() = selectedReadings.minOfOrNull { it.value }

    val weeklyMax: Double? get() = selectedReadings.maxOfOrNull { it.value }

    val days: List<LocalDate>
        get() = (6 downTo 0).map { today.minusDays(it.toLong()) }

    // One point per day of the chart; null where the wearable sent nothing that day
    val dailyAverages: List<Double?>
        get() {
            val byDay = selectedReadings.groupBy { it.measuredAt.atZone(zone).toLocalDate() }
            return days.map { day -> byDay[day]?.map { it.value }?.average() }
        }

    // Diastolic readings are shown inside their systolic row ("122/80"), never alone
    val recentReadings: List<VitalSignReading>
        get() = readings.filter { it.type != VitalSignType.BP_DIA }.takeLast(RECENT_COUNT).reversed()

    fun diastolicFor(systolic: VitalSignReading): VitalSignReading? =
        readings.firstOrNull { it.type == VitalSignType.BP_DIA && it.measuredAt == systolic.measuredAt }

    // Without a known range there is nothing to warn about
    fun isWithinRange(reading: VitalSignReading): Boolean = ranges[reading.type]?.contains(reading.value) ?: true

    private companion object {
        const val RECENT_COUNT = 3
    }
}
```
</details>

## Step D5 — `VitalHistoryViewModel`

Inject: `GetVitalSignHistoryUseCase`, `GetLiveVitalSignsUseCase`, `ServerClock`.

Functions:
- `init { load() }`
- `load()` → loading on → `today = serverClock.now().atZone(zone).toLocalDate()` → history(`today - 6`, `today`) → live (for ranges, failure ignored: `getOrNull()`) → update.
- `selectType(type)` → only `_uiState.update { it.copy(selectedType = type) }`. **No network.** Already have 7 days of all types.

No auto-refresh. History not change every 10 s. Pull again when tab reopen = fine.

**Try it.** Then compare.

<details><summary>Answer key — VitalHistoryViewModel</summary>

```kotlin
@HiltViewModel
class VitalHistoryViewModel @Inject constructor(
    private val getVitalSignHistory: GetVitalSignHistoryUseCase,
    private val getLiveVitalSigns: GetLiveVitalSignsUseCase,
    private val serverClock: ServerClock
) : ViewModel() {

    private val _uiState = MutableStateFlow(VitalHistoryUiState())
    val uiState: StateFlow<VitalHistoryUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val today = serverClock.now().atZone(_uiState.value.zone).toLocalDate()

            // Ranges only colour the badges; without them the history still makes sense
            val ranges = getLiveVitalSigns(DemoSession.CARE_RECIPIENT_PROFILE_ID)
                .getOrNull()
                ?.vitalSigns
                ?.associate { it.type to it.normalMinimum..it.normalMaximum }
                .orEmpty()

            getVitalSignHistory(DemoSession.CARE_RECIPIENT_PROFILE_ID, today.minusDays(6), today)
                .onSuccess { readings ->
                    _uiState.update {
                        it.copy(isLoading = false, readings = readings, ranges = ranges, today = today)
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "No se pudo cargar el historial")
                    }
                }
        }
    }

    // All types of the week are already loaded, so switching is local
    fun selectType(type: VitalSignType) {
        _uiState.update { it.copy(selectedType = type) }
    }
}
```
</details>

> ⚠️ Timezone trap: backend `from`/`to` = UTC dates. Peru = UTC−5. Reading at 21:00 Lima = 02:00 UTC next day. Edge rows may land on "wrong" day. For demo OK. Exam: say you know.

## Step D6 — History components (`vitalhistory/component/`)

### `VitalTypeFilterRow`

Copy `alerthistory/component/SeverityFilterRow.kt`. Options = `listOf(HR, BP_SYS, SPO2, TEMP, RESP_RATE)` (no `BP_DIA` — one "Presión" chip). Label = `type.shortLabel`.
Prototype chips: unselected = mint fill (`primaryContainer`), no border. Selected = `primary`. Shape = `shapes.medium` (square-ish, not pill).

### `WeeklyLineChart` ⭐ Canvas

No chart library. `Canvas` enough. Easy steps:

1. `values: List<Double?>` (7). Known = non-null.
2. min/max of known → map value to y. Flip: Canvas y=0 is **top**.
3. x = `index * (width / (size - 1))`.
4. `Path` → `moveTo` first, `lineTo` rest. Skip nulls.
5. Fill path = line path + down to bottom + back + `close()` → `drawPath(fill, Brush.verticalGradient(...))`.
6. Line → `drawPath(line, color, style = Stroke(2.dp.toPx()))`.
7. Dots → small white square/circle with green border.

Pad top/bottom so dots not cut in half.

<details><summary>Answer key — WeeklyLineChart</summary>

```kotlin
/** Seven-day line of the prototype. Days without readings are skipped, the line joins the ones around them. */
@Composable
fun WeeklyLineChart(modifier: Modifier = Modifier, values: List<Double?>) {
    val lineColor = MaterialTheme.colorScheme.primary
    val fillColor = MaterialTheme.colorScheme.primaryContainer
    val dotFill = MaterialTheme.colorScheme.surface

    Canvas(modifier = modifier.fillMaxWidth().height(96.dp)) {
        val known = values.filterNotNull()
        if (known.isEmpty() || values.size < 2) return@Canvas

        val inset = 6.dp.toPx()
        val min = known.min()
        // A flat week would divide by zero; draw it as a line in the middle instead
        val span = (known.max() - min).takeIf { it > 0 } ?: 1.0
        val usableHeight = size.height - 2 * inset
        val stepX = size.width / (values.size - 1)

        val points = values.mapIndexedNotNull { index, value ->
            value?.let {
                val normalized = if (known.max() == min) 0.5 else (it - min) / span
                // Canvas y grows downwards, so higher values go nearer the top
                Offset(index * stepX, inset + usableHeight * (1 - normalized).toFloat())
            }
        }

        val line = Path().apply {
            points.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
        }
        val area = Path().apply {
            addPath(line)
            lineTo(points.last().x, size.height)
            lineTo(points.first().x, size.height)
            close()
        }

        drawPath(area, Brush.verticalGradient(listOf(fillColor, Color.Transparent)))
        drawPath(line, lineColor, style = Stroke(width = 2.dp.toPx()))
        points.forEach { p ->
            drawCircle(dotFill, radius = 3.5.dp.toPx(), center = p)
            drawCircle(lineColor, radius = 3.5.dp.toPx(), center = p, style = Stroke(width = 1.5.dp.toPx()))
        }
    }
}
```
</details>

### `WeeklyAverageCard`

```
PROMEDIO SEMANAL                 Mín │ Máx
76.3 lpm                          72 │ 80
[ WeeklyLineChart ]
L    M    X    J    V    S    D
```
Params: `type`, `average`, `min`, `max`, `dailyAverages`, `days`.
- Average: `"%.1f".format(avg)` (prototype show 76.3 even for HR). Null → "--" + `health_no_readings`.
- Min/Max → `type.format(...)`.
- Day letters: `day.dayOfWeek` → `when`: MONDAY "L", TUESDAY "M", WEDNESDAY "X", THURSDAY "J", FRIDAY "V", SATURDAY "S", SUNDAY "D". `Row(SpaceBetween)`.
- Blood pressure chart → show **systolic** only. Note it in a comment.

### `ReadingItem`

```
Hoy · 14:32                    78 lpm  [Normal]
Ritmo cardíaco
```
Params: `reading`, `diastolic: VitalSignReading?`, `withinRange`, `today`, `zone`.
- Day text: same date as `today` → "Hoy", `today - 1` → "Ayer", else `dd/MM`. Time `HH:mm` → reuse `formatClockTime` from `AlertLabels.kt`.
- Top line style `dataLabel` (mono, prototype is mono).
- Value: BP → `"${sys}/${dia} mmHg"`, else `"${type.format(value)} ${type.displayUnit}"`.
- `ReadingBadge(withinRange)`.

## Step D7 — `VitalHistoryScreen` + `VitalHistoryContent`

```
LazyColumn
 ├─ "Todos los signos vitales  >"   (row, click → onSeeAllClick = switch to "Ahora")
 ├─ VitalTypeFilterRow
 ├─ WeeklyAverageCard
 ├─ items(recentReadings) { ReadingItem }
 └─ Row { OutlinedButton "Exportar PDF" | Button "Reporte semanal" }   (stubs → Toast "Próximamente")
```

Search row is **not** real search in prototype (just a link). Don't build a `TextField`. Cheap honest version: click → go "Ahora" tab, where all vitals show.

`when`: `isLoading && readings.isEmpty()` → spinner. `errorMessage != null && readings.isEmpty()` → `ErrorState`. else list.

Empty week (`readings.isEmpty()`, no error) → still show chips + card with "Sin lecturas esta semana". No blank screen.

**Try it.** Previews: full week · empty week · error.

---

# Part E — Navigation

## Step E1 — `HealthMonitoringNavGraph.kt`

Now `HomeRoute` + `HealthRoute` live in `navigation/TopLevelDestination.kt` (app shell). Wrong owner. Emergency feature owns `AlertsRoute` itself → do same.

**Try it:**
1. Cut `HomeRoute` and `HealthRoute` from `TopLevelDestination.kt`. Paste in new `healthmonitoring/presentation/navigation/HealthMonitoringNavGraph.kt`. Fix import in `TopLevelDestination.kt`.
2. Write `fun NavGraphBuilder.healthMonitoringNavGraph(onOpenHealth: () -> Unit, onOpenLocation: () -> Unit)`.
   - `composable<HomeRoute> { HomeScreen(onSeeVitalsClick = onOpenHealth, onLocationClick = onOpenLocation) }`
   - `composable<HealthRoute> { HealthScreen() }`

> 🤔 Why `onOpenHealth` lambda, not `navController.navigate(HealthRoute)` inside graph? Bottom-tab navigation has special flags (`popUpTo`, `saveState`, `restoreState`). Only shell knows. Shell already has `openTab`. Emergency graph does same with `onOpenLocation`. Look `EmergencyAlertingNavGraph.kt`.

<details><summary>Answer key</summary>

```kotlin
package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.navigation

@Serializable
object HomeRoute

@Serializable
object HealthRoute

/**
 * Routes of Health Monitoring. Switching bottom-bar tabs needs the shell's back stack rules, so the
 * shell passes how to open them instead of this graph navigating there itself.
 */
fun NavGraphBuilder.healthMonitoringNavGraph(
    onOpenHealth: () -> Unit,
    onOpenLocation: () -> Unit
) {
    composable<HomeRoute> {
        HomeScreen(onSeeVitalsClick = onOpenHealth, onLocationClick = onOpenLocation)
    }

    composable<HealthRoute> {
        HealthScreen()
    }
}
```
</details>

## Step E2 — `AppNavHost`

- Delete the two `composable<HomeRoute> { PlaceholderScreen... }` / `HealthRoute` lines.
- Add:
  ```kotlin
  healthMonitoringNavGraph(
      onOpenHealth = { openTab(TopLevelDestination.HEALTH) },
      onOpenLocation = { openTab(TopLevelDestination.LOCATION) }
  )
  ```
- `startDestination = AlertsRoute` → change to `HomeRoute`? Prototype say app open on Inicio. Yes. (Talk with team — they may test alerts first.)

Bottom bar already there. Already highlight "Inicio"/"Salud". Nothing to do. 🎉

---

# Part F — Run, prove, commit

```bash
./gradlew :app:compileDebugKotlin
```

Backend + simulator up (guide 3 step 3). `adb reverse tcp:8080 tcp:8080`. Run app.

| Check | Expect |
|---|---|
| Open app | Inicio. Spinner once. Then cards. |
| Wait 10 s | "Actualizado hace …" change. No spinner flash. |
| Simulator push HR 130 | Hero card go orange. "Elena necesita atención". HR value orange. |
| Stop backend | Numbers **stay**. (No error over old data.) |
| Llamar | Dialer opens with Elena number. |
| Ver detalle | Salud tab, "Ahora". |
| Historial | Chart 7 points (or fewer). Avg/min/max. 3 rows. |
| Tap "Presión" | Chart + avg change. **No** network call (Logcat OkHttp quiet). |
| Rotate on Historial | Same tab, same chip. No new request. |

Commit in small pieces (no co-author line):

```bash
git commit -m "refactor(health-monitoring): move presentation files into screen folders"
git commit -m "feat(health-monitoring): add home screen"
git commit -m "feat(health-monitoring): add vital history state and view model"
git commit -m "feat(health-monitoring): add health screen with now and history tabs"
git commit -m "feat(health-monitoring): wire health monitoring nav graph"
```

---

## Bonus (only if time)

1. **"Última alerta" on Home.** `GetAlertHistoryUseCase(id, size = 1)` → newest alert. But Home ViewModel = `LiveVitalsViewModel` (health only). Make `HomeViewModel` that injects both use cases? Then Home owns two contexts. Think: who should own Home? Write answer before coding.
2. **Bell badge.** `GetActiveAlertsUseCase(...).size`. Same ownership question.
3. **Shared `SegmentedTabRow<T>`** in `core/designsystem/component/` → `AlertsTabRow` + `HealthTabRow` both use it.
4. **Move `relativeTime`, `formatClockTime`, `dial`, `initials`, `ErrorState`, `StatusChip` to `core/`.** Health now import from `emergencyalerting.presentation.common`. Feature → feature import = coupling. Core = shared.

---

## Exam questions

Answer out loud. No peek.

1. New repo function, no new `@Binds`. Why still works?
2. Why `VitalSignReading` separate from `LiveVitalSign`?
3. Why `XxxScreen` + `XxxContent` split? What can `@Preview` not do?
4. Home + Health header + "Ahora" all use `LiveVitalsViewModel`. How many instances? Why? (Hint: per nav entry.)
5. Why spinner only when `vitals == null`? What user see on 3rd auto-refresh fail?
6. Why `now` comes from `ServerClock` in state, not `Instant.now()` in composable?
7. `selectType()` no network call. Why OK? When would it be wrong?
8. Why `rememberSaveable` for tab but `StateFlow` for readings?
9. `average()` on empty list. What return? What did you do about it?
10. Why `LazyVerticalGrid` inside `LazyColumn` crash? What used instead?
11. Why nav graph gets `onOpenHealth` lambda instead of navigating itself?
12. History has no classification. Where does "En Observación" come from? What if live call fail?
13. Why `HomeRoute`/`HealthRoute` moved out of `TopLevelDestination.kt`?
14. Easyvet `HomeScreen` vs your `HomeScreen`: name 3 same things, 2 different.
