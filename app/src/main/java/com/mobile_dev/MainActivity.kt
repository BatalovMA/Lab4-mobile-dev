package com.mobile_dev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

// Dark theme colors matching globals.css
private val DarkBackground = Color(0xFF212121)
private val LightForeground = Color(0xFFFFFFFF)
private val PrimaryColor = Color(0xFF90CAF9)
private val SecondaryColor = Color(0xFFCE93D8)
private val BorderColor = Color(0xFFFFFFFF)

private val AppColorScheme = darkColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.Black,
    primaryContainer = PrimaryColor.copy(alpha = 0.3f),
    onPrimaryContainer = LightForeground,
    secondary = SecondaryColor,
    onSecondary = Color.Black,
    secondaryContainer = SecondaryColor.copy(alpha = 0.3f),
    onSecondaryContainer = LightForeground,
    background = DarkBackground,
    onBackground = LightForeground,
    surface = DarkBackground,
    onSurface = LightForeground,
    surfaceVariant = Color(0xFF424242),
    onSurfaceVariant = LightForeground,
    outline = BorderColor
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = AppColorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShortCircuitCalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun ShortCircuitCalculatorScreen() {
    var selectedTask by remember { mutableStateOf(0) }

    // Task 1 states
    var ikVal by remember { mutableStateOf("2500") }
    var tPhiVal by remember { mutableStateOf("2.5") }
    var smVal by remember { mutableStateOf("1300") }

    // Task 2 states
    var kzPower by remember { mutableStateOf("200") }

    // Task 3 has no user inputs - all hardcoded

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Веб калькулятор для розрахунку струму трифазного КЗ, струму однофазного КЗ, та перевірки на термічну та динамічну стійкість",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Task Selector
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Вибір завдання:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                TaskSelectorButton(
                    text = "[1] Вибрати кабелі з напругою 10 кВ",
                    isSelected = selectedTask == 1,
                    onClick = { selectedTask = 1 }
                )

                TaskSelectorButton(
                    text = "[2] Визначити струми КЗ на шинах 10 кВ ГПП",
                    isSelected = selectedTask == 2,
                    onClick = { selectedTask = 2 }
                )

                TaskSelectorButton(
                    text = "[3] Визначити струми КЗ для підстанції",
                    isSelected = selectedTask == 3,
                    onClick = { selectedTask = 3 }
                )
            }
        }

        // Input Section
        when (selectedTask) {
            1 -> Task1Input(
                ikVal = ikVal,
                tPhiVal = tPhiVal,
                smVal = smVal,
                onIkChange = { ikVal = it },
                onTPhiChange = { tPhiVal = it },
                onSmChange = { smVal = it }
            )
            2 -> Task2Input(
                kzPower = kzPower,
                onKzPowerChange = { kzPower = it }
            )
            3 -> Task3Input()
        }

        // Results Section
        when (selectedTask) {
            1 -> Task1Results(
                ikVal = ikVal.toDoubleOrNull() ?: 0.0,
                tPhiVal = tPhiVal.toDoubleOrNull() ?: 0.0,
                smVal = smVal.toDoubleOrNull() ?: 0.0
            )
            2 -> Task2Results(
                kzPower = kzPower.toDoubleOrNull() ?: 0.0
            )
            3 -> Task3Results()
        }
    }
}

@Composable
fun TaskSelectorButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(text, textAlign = TextAlign.Start)
    }
}

@Composable
fun Task1Input(
    ikVal: String,
    tPhiVal: String,
    smVal: String,
    onIkChange: (String) -> Unit,
    onTPhiChange: (String) -> Unit,
    onSmChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Вхідні дані:", fontWeight = FontWeight.Bold)

            InputFieldWithUnit(
                label = "Iк (струм КЗ)",
                value = ikVal,
                onValueChange = onIkChange,
                unit = "А"
            )

            InputFieldWithUnit(
                label = "tф (фіктивний час)",
                value = tPhiVal,
                onValueChange = onTPhiChange,
                unit = "с"
            )

            InputFieldWithUnit(
                label = "Sм (потужність ТП)",
                value = smVal,
                onValueChange = onSmChange,
                unit = "кВ·А"
            )

            Divider()

            Text("Константи:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("• Потужність ТП: 2×1000 кВ·А", fontSize = 12.sp)
            Text("• Tм (годин використання): 4000 год", fontSize = 12.sp)
            Text("• jек (економічна густина струму): 1.4 А/мм²", fontSize = 12.sp)
            Text("• Cт (коефіцієнт): 92 с⁰·⁵/мм²", fontSize = 12.sp)
        }
    }
}

@Composable
fun Task2Input(kzPower: String, onKzPowerChange: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Вхідні дані:", fontWeight = FontWeight.Bold)

            InputFieldWithUnit(
                label = "Nкз (потужність КЗ)",
                value = kzPower,
                onValueChange = onKzPowerChange,
                unit = "МВ·А"
            )

            Divider()

            Text("Константи:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("• Uс.н: 10.5 кВ", fontSize = 12.sp)
            Text("• Uк%: 10.5 кВ", fontSize = 12.sp)
            Text("• Sном.т: 6.3 А/мм²", fontSize = 12.sp)
        }
    }
}

@Composable
fun Task3Input() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Константи (всі значення задано):", fontWeight = FontWeight.Bold)
            Text("• Uк.max: 11.1 %", fontSize = 12.sp)
            Text("• Uв.н: 115 В", fontSize = 12.sp)
            Text("• Sном.т: 6.3 МВ·А", fontSize = 12.sp)
            Text("• Rш: 10.65 Ом", fontSize = 12.sp)
            Text("• Rс.н: 10.65 Ом", fontSize = 12.sp)
            Text("• Xс.н: 24.02 Ом", fontSize = 12.sp)
            Text("• Rш.min: 34.88 Ом", fontSize = 12.sp)
            Text("• Rс.min: 34.88 Ом", fontSize = 12.sp)
            Text("• Xс.min: 65.68 Ом", fontSize = 12.sp)
        }
    }
}

@Composable
fun Task1Results(ikVal: Double, tPhiVal: Double, smVal: Double) {
    val nominalTension = 10.0
    val currentDensity = 1.4
    val ctVal = 92.0

    val im = calcIm(smVal, nominalTension)
    val imPa = calcImPa(im)
    val sek = calcSek(im, currentDensity)
    val sMin = calcSMin(ikVal, tPhiVal, ctVal)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Результати [1]:", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            ResultItem(
                "Розрахунковий струм для нормального режиму:",
                "${String.format("%.2f", im)} А"
            )

            ResultItem(
                "Розрахунковий струм для післяаварійного режиму:",
                "${String.format("%.2f", imPa)} А"
            )

            ResultItem(
                "Економічний переріз:",
                "${String.format("%.2f", sek)} мм²"
            )

            ResultItem(
                "Рекомендований кабель:",
                "ААБ 10 3×25"
            )

            ResultItem(
                "Термічна стійкість кабелю до дії струмів КЗ:",
                "${String.format("%.2f", sMin)} мм²"
            )
        }
    }
}

@Composable
fun Task2Results(kzPower: Double) {
    val usnVal = 10.5
    val ukPercVal = 10.5
    val snomtVal = 6.3

    val sumX = calcSumX(kzPower, usnVal, ukPercVal, snomtVal)
    val ip0 = calcIp0(usnVal, sumX)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Результати [2]:", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            ResultItem(
                "Сумарний опір для точки К1:",
                "${String.format("%.2f", sumX)} Ом"
            )

            ResultItem(
                "Початкове діюче значення струму трифазного КЗ:",
                "${String.format("%.2f", ip0)} кА"
            )
        }
    }
}

@Composable
fun Task3Results() {
    val ukmaxVal = 11.1
    val uvnVal = 115.0
    val snomtVal = 6.3
    val rshVal = 10.65
    val xcnVal = 24.02
    val rshMinVal = 34.88
    val xcMinVal = 65.68

    val xt = calcXt(ukmaxVal, uvnVal, snomtVal)
    val xsh = calcXsh(xcnVal, xt)
    val zsh = calcZsh(rshVal, xsh)
    val xshMin = calcXshMin(xcMinVal, xt)
    val zshMin = calcZshMin(rshMinVal, xshMin)
    val ish3 = calcIsh3(uvnVal, zsh)
    val ish2 = calcIsh2(ish3)
    val ish3Min = calcIsh3Min(uvnVal, zshMin)
    val ish2Min = calcIsh2Min(ish3Min)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Результати [3]:", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            ResultItem(
                "Реактивний опір силового трансформатора:",
                "${String.format("%.2f", xt)} Ом"
            )

            Divider()

            Text("Опори в нормальному режимі:", fontWeight = FontWeight.SemiBold)
            ResultItem("Z =", "${String.format("%.2f", zsh)} Ом")
            ResultItem("X =", "${String.format("%.2f", xsh)} Ом")

            Divider()

            Text("Опори в мінімальному режимі:", fontWeight = FontWeight.SemiBold)
            ResultItem("Z =", "${String.format("%.2f", zshMin)} Ом")
            ResultItem("X =", "${String.format("%.2f", xshMin)} Ом")

            Divider()

            Text("Струми в нормальному режимі:", fontWeight = FontWeight.SemiBold)
            ResultItem("I(3) =", "${String.format("%.2f", ish3)} А")
            ResultItem("I(2) =", "${String.format("%.2f", ish2)} А")

            Divider()

            Text("Струми в мінімальному режимі:", fontWeight = FontWeight.SemiBold)
            ResultItem("I(3) =", "${String.format("%.2f", ish3Min)} А")
            ResultItem("I(2) =", "${String.format("%.2f", ish2Min)} А")
        }
    }
}

@Composable
fun InputFieldWithUnit(label: String, value: String, onValueChange: (String) -> Unit, unit: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, modifier = Modifier.weight(2f), fontSize = 14.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1.5f),
            singleLine = true
        )
        Text(text = unit, modifier = Modifier.weight(1f).padding(start = 8.dp), fontSize = 14.sp)
    }
}

@Composable
fun ResultItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp)
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// Task 1 calculations
fun calcIm(sm: Double, nominalTension: Double): Double {
    return (sm / 2.0) / (sqrt(3.0) * nominalTension)
}

fun calcImPa(im: Double): Double {
    return 2.0 * im
}

fun calcSek(im: Double, currentDensity: Double): Double {
    return im / currentDensity
}

fun calcSMin(ik: Double, tPhi: Double, ct: Double): Double {
    return (ik * sqrt(tPhi)) / ct
}

// Task 2 calculations
fun calcSumX(kzPower: Double, usn: Double, ukPerc: Double, snomt: Double): Double {
    return (usn.pow(2) / kzPower) + ((ukPerc / 100.0) * (usn.pow(2) / snomt))
}

fun calcIp0(usn: Double, sumX: Double): Double {
    return usn / (sqrt(3.0) * sumX)
}

// Task 3 calculations
fun calcXt(ukmax: Double, uvn: Double, snomt: Double): Double {
    return (ukmax * uvn.pow(2)) / (100.0 * snomt)
}

fun calcXsh(xcn: Double, xt: Double): Double {
    return xcn + xt
}

fun calcZsh(rsh: Double, xsh: Double): Double {
    return sqrt(rsh.pow(2) + xsh.pow(2))
}

fun calcXshMin(xcMin: Double, xt: Double): Double {
    return xcMin + xt
}

fun calcZshMin(rshMin: Double, xshMin: Double): Double {
    return sqrt(rshMin.pow(2) + xshMin.pow(2))
}

fun calcIsh3(uvn: Double, zsh: Double): Double {
    return (uvn * 1000.0) / (sqrt(3.0) * zsh)
}

fun calcIsh2(ish3: Double): Double {
    return ish3 * (sqrt(3.0) / 2.0)
}

fun calcIsh3Min(uvn: Double, zshMin: Double): Double {
    return (uvn * 1000.0) / (sqrt(3.0) * zshMin)
}

fun calcIsh2Min(ish3Min: Double): Double {
    return ish3Min * (sqrt(3.0) / 2.0)
}
