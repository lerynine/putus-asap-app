package com.example.putusasap.com.example.putusasap

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.putusasap.ui.theme.PutusAsapTheme
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.putusasap.HasilDeteksiActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

@OptIn(ExperimentalMaterial3Api::class)
class FormDeteksiActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PutusAsapTheme {
                FormDeteksiScreen(
                    onBack = { finish() },
                    onSubmit = { dataMap ->
                        // Ambil hasil dari dataMap kalau diperlukan
                        val intent = Intent(this, com.example.putusasap.HasilDeteksiActivity::class.java)
                        intent.putExtra("resiko_lung", dataMap["resiko_lung"].toString())
                        intent.putExtra("resiko_asthma", dataMap["resiko_asthma"].toString())
                        intent.putExtra("resiko_cardio", dataMap["resiko_cardio"].toString())
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDeteksiScreen(onBack: () -> Unit, onSubmit: (Map<String, Any?>) -> Unit) {
    val context = LocalContext.current

    // --- IDENTITAS
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    // --- TEKANAN DARAH & JANTUNG
    var apHi by remember { mutableStateOf("") }
    var apLo by remember { mutableStateOf("") }
    var cholesterol by remember { mutableStateOf("") }
    var glucose by remember { mutableStateOf("") }
    var physicalActivity by remember { mutableStateOf(false) }

    // --- ASMA & PERNAPASAN
    var smokingStatus by remember { mutableStateOf("") }
    var medication by remember { mutableStateOf("") }
    var peakFlow by remember { mutableStateOf("") }

    // --- FAKTOR RISIKO
    var airPollution by remember { mutableStateOf(5f) }
    var riskAlcoholScale by remember { mutableStateOf(5f) }
    var dustAllergyPresent by remember { mutableStateOf(false) }
    var dustAllergyIntensity by remember { mutableStateOf(5f) }
    var occupationalHazards by remember { mutableStateOf(5f) }
    var geneticRisk by remember { mutableStateOf(5f) }
    var chronicLungDisease by remember { mutableStateOf(false) }
    var balancedDiet by remember { mutableStateOf(5f) }
    var obesityScale by remember { mutableStateOf(5f) }
    var smokingHabitual by remember { mutableStateOf(false) }
    var passiveSmoker by remember { mutableStateOf(false) }

    // --- GEJALA KLINIS
    var chestPain by remember { mutableStateOf(false) }
    var coughingBlood by remember { mutableStateOf(false) }
    var fatigue by remember { mutableStateOf(false) }
    var weightLoss by remember { mutableStateOf(false) }
    var shortnessOfBreath by remember { mutableStateOf(false) }
    var wheezing by remember { mutableStateOf(false) }
    var swallowingDifficulty by remember { mutableStateOf(false) }
    var clubbing by remember { mutableStateOf(false) }
    var frequentCold by remember { mutableStateOf(false) }
    var dryCough by remember { mutableStateOf(false) }
    var snoring by remember { mutableStateOf(false) }

    // Mandatory check
    val mandatoryFilled =
        age.isNotBlank() &&
                gender.isNotBlank() &&
                height.isNotBlank() &&
                weight.isNotBlank() &&
                smokingStatus.isNotBlank() &&
                medication.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Form Deteksi", color = Color.Black)
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // --- IDENTITAS
            SectionTitle("Identitas Pasien")
            OutlinedTextField(
                value = age,
                onValueChange = { age = it.filter(Char::isDigit) },
                label = { RequiredLabel("Usia (tahun)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = height,
                onValueChange = { height = it.filter(Char::isDigit) },
                label = { RequiredLabel("Tinggi Badan (cm)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it.filter(Char::isDigit) },
                label = { RequiredLabel("Berat Badan (kg)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            GenderRadioRow(selected = gender, onSelect = { gender = it })

            Spacer(Modifier.height(16.dp))

            // --- TEKANAN DARAH & JANTUNG
            SectionTitle("Data Tekanan Darah & Jantung")
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = apHi,
                    onValueChange = { apHi = it.filter(Char::isDigit) },
                    label = { Text("Tekanan Sistolik") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = apLo,
                    onValueChange = { apLo = it.filter(Char::isDigit) },
                    label = { Text("Tekanan Diastolik") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = cholesterol,
                    onValueChange = { cholesterol = it.filter(Char::isDigit) },
                    label = { Text("Kadar Kolesterol") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = glucose,
                    onValueChange = { glucose = it.filter(Char::isDigit) },
                    label = { Text("Kadar Glukosa") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            ToggleRow(
                "Aktivitas Fisik (aktif/tidak)",
                physicalActivity
            ) { physicalActivity = it }

            Spacer(Modifier.height(16.dp))

            // --- ASMA & PERNAPASAN
            SectionTitle("Data Pernapasan")
            SmokingStatusDropdown(selected = smokingStatus, onSelect = { smokingStatus = it })
            MedicationDropdown(selected = medication, onSelect = { medication = it })
            OutlinedTextField(
                value = peakFlow,
                onValueChange = { peakFlow = it.filter(Char::isDigit) },
                label = { Text("Arus Puncak Pernapasan (opsional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(16.dp))

            // --- FAKTOR RISIKO
            SectionTitle("Faktor Risiko Paru & Gaya Hidup")
            SliderWithLabel("Paparan Polusi Udara", airPollution) { airPollution = it }
            SliderWithLabel("Konsumsi Alkohol (kebiasaan)", riskAlcoholScale) { riskAlcoholScale = it }
            SliderWithLabel("Bahaya Pekerjaan (lingkungan kerja)", occupationalHazards) { occupationalHazards = it }
            SliderWithLabel("Risiko Genetik (riwayat keluarga)", geneticRisk) { geneticRisk = it }
            SliderWithLabel("Pola Makan Seimbang", balancedDiet) { balancedDiet = it }
            SliderWithLabel("Tingkat Obesitas", obesityScale) { obesityScale = it }

            ToggleRow("Alergi Debu (ada/tidak)", dustAllergyPresent) { dustAllergyPresent = it }

            if (dustAllergyPresent) {
                SliderWithLabel("Tingkat Keparahan Alergi Debu", dustAllergyIntensity) { dustAllergyIntensity = it }
            }

            ToggleRow("Penyakit Paru Kronis (laporan diri)", chronicLungDisease) { chronicLungDisease = it }
            ToggleRow("Perokok Aktif (kebiasaan)", smokingHabitual) { smokingHabitual = it }
            ToggleRow("Perokok Pasif", passiveSmoker) { passiveSmoker = it }

            Spacer(Modifier.height(16.dp))

            // --- GEJALA KLINIS
            SectionTitle("Gejala Klinis")
            SymptomSwitch("Nyeri Dada", chestPain) { chestPain = it }
            SymptomSwitch("Batuk Berdarah", coughingBlood) { coughingBlood = it }
            SymptomSwitch("Kelelahan", fatigue) { fatigue = it }
            SymptomSwitch("Penurunan Berat Badan", weightLoss) { weightLoss = it }
            SymptomSwitch("Sesak Napas", shortnessOfBreath) { shortnessOfBreath = it }
            SymptomSwitch("Mengi (napas berbunyi)", wheezing) { wheezing = it }
            SymptomSwitch("Kesulitan Menelan", swallowingDifficulty) { swallowingDifficulty = it }
            SymptomSwitch("Perubahan Bentuk Kuku (clubbing)", clubbing) { clubbing = it }
            SymptomSwitch("Sering Pilek", frequentCold) { frequentCold = it }
            SymptomSwitch("Batuk Kering", dryCough) { dryCough = it }
            SymptomSwitch("Mendengkur", snoring) { snoring = it }


            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (!mandatoryFilled) {
                        Toast.makeText(context, "Lengkapi semua field wajib (*)", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid ?: "unknown_user"

                    // 🔹 Variabel hasil model
                    var probAsthma = 0f
                    var kategoriAsthma = "N/A"

                    var probCardio = 0f
                    var kategoriCardio = "N/A"

                    var kategoriLung = "N/A"

                    // 🔹 Model Asthma
                    if (!peakFlow.isNullOrEmpty()) {
                        try {
                            val interpreter = Interpreter(loadModelFile(context, "asthma_model.tflite"))

                            val input = FloatArray(1 + 1 + 3 + 2 + 1)
                            var idx = 0
                            input[idx++] = age.toFloatOrNull() ?: 0f
                            input[idx++] = encodeGender(gender)
                            encodeSmoking(smokingStatus).forEach { input[idx++] = it }
                            encodeMedication(medication).forEach { input[idx++] = it }
                            input[idx++] = peakFlow.toFloatOrNull() ?: 0f

                            val inputBuffer = arrayOf(input)
                            val outputBuffer = Array(1) { FloatArray(1) }
                            interpreter.run(inputBuffer, outputBuffer)

                            probAsthma = outputBuffer[0][0]
                            kategoriAsthma = categorizeRisk(probAsthma)

                        } catch (e: Exception) {
                            Toast.makeText(context, "Error ML (Asthma): ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }

                    // 🔹 Model Cardio
                    if (!apHi.isNullOrEmpty() && !apLo.isNullOrEmpty() &&
                        !cholesterol.isNullOrEmpty() && !glucose.isNullOrEmpty()
                    ) {
                        try {
                            val interpreter = Interpreter(loadModelFile(context, "cardio_model.tflite"))

                            val input = FloatArray(11)
                            var idx = 0
                            input[idx++] = age.toFloatOrNull() ?: 0f
                            input[idx++] = if (gender == "Male") 2f else 1f
                            input[idx++] = height.toFloatOrNull() ?: 0f
                            input[idx++] = weight.toFloatOrNull() ?: 0f
                            input[idx++] = apHi.toFloatOrNull() ?: 0f
                            input[idx++] = apLo.toFloatOrNull() ?: 0f
                            input[idx++] = cholesterol.toFloatOrNull() ?: 0f
                            input[idx++] = glucose.toFloatOrNull() ?: 0f
                            input[idx++] = if (smokingHabitual) 1f else 0f
                            input[idx++] = if (riskAlcoholScale > 5) 1f else 0f
                            input[idx++] = if (physicalActivity) 1f else 0f

                            val inputBuffer = arrayOf(input)
                            val outputBuffer = Array(1) { FloatArray(1) }
                            interpreter.run(inputBuffer, outputBuffer)

                            probCardio = outputBuffer[0][0]
                            kategoriCardio =
                                if (probCardio > 0.5f) "Tinggi" else "Risiko Rendah Cardio"

                        } catch (e: Exception) {
                            Toast.makeText(context, "Error ML (Cardio): ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }

                    // 🔹 Model Lung Disease
                    try {
                        val interpreter = Interpreter(loadModelFile(context, "lung_disease_model.tflite"))

                        val input = FloatArray(23)
                        var idx = 0
                        input[idx++] = age.toFloatOrNull() ?: 0f
                        input[idx++] = if (gender == "Male") 2f else 1f
                        input[idx++] = airPollution.toFloat()
                        input[idx++] = riskAlcoholScale.toFloat()
                        input[idx++] = if (dustAllergyPresent) 1f else 0f
                        input[idx++] = occupationalHazards.toFloat()
                        input[idx++] = geneticRisk.toFloat()
                        input[idx++] = if (chronicLungDisease) 1f else 0f
                        input[idx++] = balancedDiet.toFloat()
                        input[idx++] = obesityScale.toFloat()
                        input[idx++] = if (smokingHabitual) 1f else 0f
                        input[idx++] = if (passiveSmoker) 1f else 0f
                        input[idx++] = if (chestPain) 1f else 0f
                        input[idx++] = if (coughingBlood) 1f else 0f
                        input[idx++] = if (fatigue) 1f else 0f
                        input[idx++] = if (weightLoss) 1f else 0f
                        input[idx++] = if (shortnessOfBreath) 1f else 0f
                        input[idx++] = if (wheezing) 1f else 0f
                        input[idx++] = if (swallowingDifficulty) 1f else 0f
                        input[idx++] = if (clubbing) 1f else 0f
                        input[idx++] = if (frequentCold) 1f else 0f
                        input[idx++] = if (dryCough) 1f else 0f
                        input[idx++] = if (snoring) 1f else 0f

                        val inputBuffer = arrayOf(input)
                        val outputBuffer = Array(1) { FloatArray(3) }
                        interpreter.run(inputBuffer, outputBuffer)

                        val result = outputBuffer[0]
                        val maxIdx = result.indices.maxByOrNull { result[it] } ?: 0
                        kategoriLung = when (maxIdx) {
                            0 -> "Rendah"
                            1 -> "Sedang"
                            else -> "Tinggi"
                        }

                    } catch (e: Exception) {
                        Toast.makeText(context, "Error ML (Lung Disease): ${e.message}", Toast.LENGTH_LONG).show()
                    }

                    // --- Simpan data ---
                    val data = mapOf(
                        "user_id" to userId,
                        "age" to age.toIntOrNull(),
                        "gender" to if (gender == "Male") 2 else 1,
                        "height" to height.toIntOrNull(),
                        "weight" to weight.toFloatOrNull(),
                        "ap_hi" to apHi.toIntOrNull(),
                        "ap_lo" to apLo.toIntOrNull(),
                        "cholesterol" to cholesterol.toIntOrNull(),
                        "glucose" to glucose.toIntOrNull(),
                        "smoke" to if (smokingHabitual) 1 else 0,
                        "alco" to if (riskAlcoholScale > 5) 1 else 0,
                        "active" to if (physicalActivity) 1 else 0,
                        "smoking_status" to smokingStatus,
                        "medication" to medication,
                        "peak_flow" to peakFlow.toIntOrNull(),
                        "created_at" to Timestamp.now(),

                        // hasil model terpisah
                        "probabilitas_asthma" to String.format("%.2f", probAsthma),
                        "resiko_asthma" to kategoriAsthma,

                        "probabilitas_cardio" to String.format("%.2f", probCardio),
                        "resiko_cardio" to kategoriCardio,

                        "resiko_lung" to kategoriLung
                    )

                    val db = FirebaseFirestore.getInstance()
                    db.collection("riwayat_deteksi")
                        .add(data)
                        .addOnSuccessListener { docRef ->
                            val docId = docRef.id

                            Toast.makeText(context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()

                            onSubmit(data)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Gagal simpan: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = mandatoryFilled,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC15F56))
            ) {
                Text(
                    text = "Lihat Hasil",
                    color = Color.White // ✅ teks putih
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

/* ---------- Reusable Composables ---------- */

@Composable
fun SectionTitle(title: String) {
    Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFC15F56), modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
fun RequiredLabel(label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label)
        Text(" *", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GenderRadioRow(selected: String, onSelect: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Jenis Kelamin:", modifier = Modifier.width(120.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected == "Male", onClick = { onSelect("Male") })
            Text("Laki-laki", modifier = Modifier.padding(end = 8.dp))
            RadioButton(selected == "Female", onClick = { onSelect("Female") })
            Text("Wanita")
        }
    }
}


@Composable
fun SmokingStatusRadio(selected: String, onSelect: (String) -> Unit) {
    Column {
        Text("Smoking Status *", fontWeight = FontWeight.Medium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
            RadioButton(selected == "Non-Smoker", onClick = { onSelect("Non-Smoker") })
            Text("Non-Smoker")
            RadioButton(selected == "Ex-Smoker", onClick = { onSelect("Ex-Smoker") })
            Text("Ex-Smoker")
            RadioButton(selected == "Current Smoker", onClick = { onSelect("Current Smoker") })
            Text("Current Smoker")
        }
    }
}

@Composable
fun SliderWithLabel(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label)
            Text(value.toInt().toString(), fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 1f..10f,
            steps = 8,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFC15F56),         // Warna pegangan slider
                activeTrackColor = Color(0xFFC15F56),   // Warna track aktif
                inactiveTrackColor = Color(0xFFEEEEEE)  // Warna track tidak aktif (opsional)
            )
        )
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(checked, onValueChange = onCheckedChange),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,              // warna bulatan saat ON
                checkedTrackColor = Color(0xFFC15F56),        // warna track saat ON
                uncheckedThumbColor = Color.White,            // bulatan saat OFF
                uncheckedTrackColor = Color(0xFFBDBDBD)       // track saat OFF (abu)
            )
        )
        Spacer(Modifier.width(8.dp))
        Text(label, color = Color(0xFFC15F56)) // teks warna merah
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
fun SymptomSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmokingStatusDropdown(
    selected: String,
    onSelect: (String) -> Unit
) {
    val options = listOf(
        "Non-Smoker" to "Tidak Merokok",
        "Ex-Smoker" to "Mantan Perokok",
        "Current Smoker" to "Perokok Aktif"
    )
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = options.find { it.first == selected }?.second ?: "",
            onValueChange = { },
            readOnly = true,
            label = { Text("Status Merokok *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (eng, ind) ->
                DropdownMenuItem(
                    text = { Text(ind) }, // tampilkan versi Indonesia
                    onClick = {
                        onSelect(eng) // simpan versi Inggris ke Firestore
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationDropdown(
    selected: String,
    onSelect: (String) -> Unit
) {
    val options = listOf(
        "None" to "Tidak Ada",
        "Inhaler" to "Inhaler",
        "Controller Medication" to "Obat Pengontrol"
    )
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = options.find { it.first == selected }?.second ?: "",
            onValueChange = { },
            readOnly = true,
            label = { Text("Pengobatan *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (eng, ind) ->
                DropdownMenuItem(
                    text = { Text(ind) }, // tampilkan versi Indonesia
                    onClick = {
                        onSelect(eng) // simpan versi Inggris
                        expanded = false
                    }
                )
            }
        }
    }
}

// Load model TFLite
fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
    val fileDescriptor = context.assets.openFd(modelName)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    return fileChannel.map(
        FileChannel.MapMode.READ_ONLY,
        fileDescriptor.startOffset,
        fileDescriptor.declaredLength
    )
}

fun categorizeRisk(prob: Float): String =
    when {
        prob < 0.33f -> "Rendah"
        prob < 0.66f -> "Sedang"
        else -> "Tinggi"
    }

// Encode helper
fun encodeGender(gender: String): Float = if (gender == "Male") 1f else 0f

fun encodeSmoking(smokingStatus: String): FloatArray =
    when (smokingStatus) {
        "Ex-Smoker" -> floatArrayOf(0f, 1f, 0f)
        "Current" -> floatArrayOf(0f, 0f, 1f)
        else -> floatArrayOf(1f, 0f, 0f) // Non-Smoker
    }

fun encodeMedication(med: String): FloatArray =
    when (med) {
        "Inhaler" -> floatArrayOf(0f, 1f)
        else -> floatArrayOf(1f, 0f) // None/default
    }