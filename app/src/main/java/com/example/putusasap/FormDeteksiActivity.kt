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
import android.util.Log
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

                    // 🔹 Model Asthma (PPOK)
                    if (!peakFlow.isNullOrEmpty()) {
                        try {
                            val interpreter = Interpreter(loadModelFile(context, "asthma_model_improved.tflite"))

                            // === 1️⃣ Susun input dalam urutan yang SAMA dengan model Python ===
                            val input = FloatArray(8)
                            var idx = 0
                            input[idx++] = age.toFloatOrNull() ?: 0f
                            input[idx++] = encodeGender(gender)
                            input[idx++] = peakFlow.toFloatOrNull() ?: 0f
                            encodeSmoking(smokingStatus).forEach { input[idx++] = it }
                            encodeMedication(medication).forEach { input[idx++] = it }

                            // Log mentah sebelum scaling
                            android.util.Log.d("AsthmaModelRawInput", "Input mentah: " +
                                    input.joinToString(", ", prefix = "[", postfix = "]"))

                            // === 2️⃣ Scaling: (x - mean) / std ===
                            val mean = floatArrayOf(
                                39.1916667f,  // Age
                                0.47083333f,  // Gender
                                279.7125f,    // Peak_Flow
                                0.32916667f,  // Smoking_Current
                                0.27916667f,  // Smoking_Ex-Smoker
                                0.39166667f,  // Smoking_Non-Smoker
                                0.28333333f,  // Medication_Controller Medication
                                0.24166667f   // Medication_Inhaler
                            )

                            val scale = floatArrayOf(
                                14.20199272f, // Age
                                0.49914858f,  // Gender
                                73.31226485f, // Peak_Flow
                                0.4699106f,   // Smoking_Current
                                0.44858961f,  // Smoking_Ex-Smoker
                                0.48812282f,  // Smoking_Non-Smoker
                                0.45061686f,  // Medication_Controller Medication
                                0.42809332f   // Medication_Inhaler
                            )

                            for (i in input.indices) {
                                input[i] = (input[i] - mean[i]) / scale[i]
                            }

                            // Log setelah scaling
                            android.util.Log.d("AsthmaModelScaledInput", "Input setelah scaling: " +
                                    input.joinToString(", ", prefix = "[", postfix = "]"))

                            // === 3️⃣ Jalankan inferensi ===
                            val inputBuffer = arrayOf(input)
                            val outputBuffer = Array(1) { FloatArray(1) }
                            interpreter.run(inputBuffer, outputBuffer)

                            probAsthma = outputBuffer[0][0]
                            kategoriAsthma = when {
                                probAsthma < 0.33f -> "Rendah"
                                probAsthma < 0.66f -> "Sedang"
                                else -> "Tinggi"
                            }

                            // === 4️⃣ Log hasil inferensi ===
                            android.util.Log.d("AsthmaModelOutput",
                                "Probabilitas Asthma = ${"%.6f".format(probAsthma)} ($kategoriAsthma)")

                            // Jika kamu ingin lihat output mentah model (jaga-jaga kalau output multi-dimensi)
                            android.util.Log.d("AsthmaModelOutputRaw",
                                "Output mentah: " + outputBuffer[0].joinToString(", "))

                        } catch (e: Exception) {
                            Toast.makeText(context, "Error ML (Asthma): ${e.message}", Toast.LENGTH_LONG).show()
                            android.util.Log.e("AsthmaModelError", "Gagal inferensi Asthma: ${e.message}", e)
                        }
                    }

                    // 🔹 Model Cardio
                    if (!apHi.isNullOrEmpty() && !apLo.isNullOrEmpty() &&
                        !cholesterol.isNullOrEmpty() && !glucose.isNullOrEmpty()
                    ) {
                        try {
                            val interpreter = Interpreter(loadModelFile(context, "cardio_model_improved.tflite"))

                            // 🔧 Persiapan input (harus urut dan sesuai preprocessing Python)
                            val ageDays = age.toFloatOrNull() ?: 0f
                            val ageYears = ageDays / 365f // sesuai preprocessing di Python
                            val apHiVal = apHi.toFloatOrNull()?.coerceIn(80f, 200f) ?: 0f
                            val apLoVal = apLo.toFloatOrNull()?.coerceIn(50f, 150f) ?: 0f

                            val input = FloatArray(11)
                            var idx = 0
                            input[idx++] = ageYears
                            input[idx++] = if (gender == "Male") 2f else 1f
                            input[idx++] = height.toFloatOrNull() ?: 0f
                            input[idx++] = weight.toFloatOrNull() ?: 0f
                            input[idx++] = apHiVal
                            input[idx++] = apLoVal
                            input[idx++] = cholesterol.toFloatOrNull() ?: 0f
                            input[idx++] = glucose.toFloatOrNull() ?: 0f
                            input[idx++] = if (smokingHabitual) 1f else 0f
                            input[idx++] = if (riskAlcoholScale > 5) 1f else 0f
                            input[idx++] = if (physicalActivity) 1f else 0f

                            // 🧠 Log sebelum standardisasi (debug)
                            android.util.Log.d("CardioModelInputRaw", """
            === INPUT CARDIO MODEL (RAW) ===
            Age (tahun)        : ${"%.2f".format(ageYears)}
            Gender (1=F,2=M)   : ${if (gender == "Male") 2f else 1f}
            Height (cm)        : ${height}
            Weight (kg)        : ${weight}
            AP_hi (Sistolik)   : $apHiVal
            AP_lo (Diastolik)  : $apLoVal
            Cholesterol        : ${cholesterol}
            Glucose            : ${glucose}
            Smoking (1/0)      : ${if (smokingHabitual) 1 else 0}
            Alcohol>5 (1/0)    : ${if (riskAlcoholScale > 5) 1 else 0}
            Physical Activity  : ${if (physicalActivity) 1 else 0}
        """.trimIndent())

                            // 🔹 Standardisasi sesuai scaler dari Python
                            val mean = floatArrayOf(
                                52.8349643f, 1.34910714f, 164.344089f, 74.2220586f,
                                126.876929f, 82.2636786f, 1.36675f, 1.22667857f,
                                0.0878392857f, 0.0530178571f, 0.804946429f
                            )

                            val scale = floatArrayOf(
                                6.75208531f, 0.47668789f, 8.22690571f, 14.38961719f,
                                17.22651392f, 12.44542983f, 0.67991187f, 0.57231582f,
                                0.28306103f, 0.22406911f, 0.39624194f
                            )

                            for (i in input.indices) {
                                input[i] = (input[i] - mean[i]) / scale[i]
                            }

                            // 🧩 Log hasil setelah standardisasi
                            android.util.Log.d(
                                "CardioModelScaledInput",
                                input.joinToString(prefix = "Scaled input: [", postfix = "]") { "%.3f".format(it) }
                            )

                            // 🔮 Jalankan model
                            val inputBuffer = arrayOf(input)
                            val outputBuffer = Array(1) { FloatArray(1) }
                            interpreter.run(inputBuffer, outputBuffer)

                            probCardio = outputBuffer[0][0]
                            android.util.Log.d("CardioModelOutput", "Probabilitas cardio = ${"%.3f".format(probCardio)}")

                            // 🩺 Penentuan kategori risiko mengikuti versi Python
                            kategoriCardio = when {
                                probCardio < 0.33f -> "Rendah"
                                probCardio < 0.66f -> "Sedang"
                                else -> "Tinggi"
                            }

                            android.util.Log.d("CardioModelOutput", "Kategori risiko: $kategoriCardio")

                        } catch (e: Exception) {
                            Toast.makeText(context, "Error ML (Cardio): ${e.message}", Toast.LENGTH_LONG).show()
                            android.util.Log.e("CardioModelError", "Gagal inferensi Cardio: ${e.message}", e)
                        }
                    }

                    // 🔹 Model Lung Disease (sesuai Python)
                    try {
                        val interpreter = Interpreter(loadModelFile(context, "lung_disease_model_improved.tflite"))

                        // === 1️⃣ Urutan fitur HARUS sama dengan CSV & Python ===
                        // === 1️⃣ Urutan fitur HARUS sama dengan CSV & Python ===
                        val input = FloatArray(23)
                        var idx = 0

                        fun Float?.safe() = this ?: 0f
                        fun Boolean.toScale(): Float = if (this) 10f else 1f // konversi boolean ke skala 1–10

                        input[idx++] = age.toFloat().safe()                          // Age
                        input[idx++] = if (gender == "Male") 2f else 1f              // Gender
                        input[idx++] = airPollution.toFloat().safe()                 // Air Pollution
                        input[idx++] = riskAlcoholScale.toFloat().safe()             // Alcohol use
                        input[idx++] = if (dustAllergyPresent) 10f else 1f           // Dust Allergy
                        input[idx++] = occupationalHazards.toFloat().safe()          // OccuPational Hazards
                        input[idx++] = geneticRisk.toFloat().safe()                  // Genetic Risk
                        input[idx++] = if (chronicLungDisease) 10f else 1f           // chronic Lung Disease
                        input[idx++] = balancedDiet.toFloat().safe()                 // Balanced Diet
                        input[idx++] = obesityScale.toFloat().safe()                 // Obesity
                        input[idx++] = if (smokingHabitual) 10f else 1f              // Smoking
                        input[idx++] = if (passiveSmoker) 10f else 1f                // Passive Smoker
                        input[idx++] = if (chestPain) 10f else 1f                    // Chest Pain
                        input[idx++] = if (coughingBlood) 10f else 1f                // Coughing of Blood
                        input[idx++] = if (fatigue) 10f else 1f                      // Fatigue
                        input[idx++] = if (weightLoss) 10f else 1f                   // Weight Loss
                        input[idx++] = if (shortnessOfBreath) 10f else 1f            // Shortness of Breath
                        input[idx++] = if (wheezing) 10f else 1f                     // Wheezing
                        input[idx++] = if (swallowingDifficulty) 10f else 1f         // Swallowing Difficulty
                        input[idx++] = if (clubbing) 10f else 1f                     // Clubbing of Finger Nails
                        input[idx++] = if (frequentCold) 10f else 1f                 // Frequent Cold
                        input[idx++] = if (dryCough) 10f else 1f                     // Dry Cough
                        input[idx++] = if (snoring) 10f else 1f                      // Snoring

                        android.util.Log.d("LungModelRawInput", "Input mentah: " +
                                input.joinToString(", ", prefix = "[", postfix = "]"))

                        // === 2️⃣ Scaling (x - mean) / std sesuai Python ===
                        val mean = floatArrayOf(
                            37.07375f, 1.40875f, 3.83875f, 4.5875f, 5.1775f, 4.875f, 4.62f, 4.4075f,
                            4.51125f, 4.4775f, 3.935f, 4.2125f, 4.46625f, 4.84f, 3.82375f, 3.84875f,
                            4.24625f, 3.82125f, 3.74f, 3.99625f, 3.56125f, 3.86f, 2.94f
                        )

                        val scale = floatArrayOf(
                            11.86637312f, 0.49160293f, 2.03414563f, 2.61196167f, 1.96748412f, 2.08851023f,
                            2.12499412f, 1.82248285f, 2.11538967f, 2.11411772f, 2.47856309f, 2.3156735f,
                            2.2552962f, 2.43195395f, 2.23331277f, 2.20587249f, 2.27444739f, 2.02096473f,
                            2.27484065f, 2.41065052f, 1.81762164f, 2.01318156f, 1.49378713f
                        )

                        for (i in input.indices) {
                            input[i] = (input[i] - mean[i]) / scale[i]
                        }

                        android.util.Log.d("LungModelScaledInput", "Input setelah scaling: " +
                                input.joinToString(", ", prefix = "[", postfix = "]"))

                        // === 3️⃣ Jalankan inferensi ===
                        val inputBuffer = arrayOf(input)
                        // Buat buffer output untuk 3 kelas: Low, Medium, High
                        val outputBuffer = Array(1) { FloatArray(3) }

// Jalankan model
                        interpreter.run(arrayOf(input), outputBuffer)

// Ambil hasil prediksi
                        val result = outputBuffer[0]

// Kalau kamu mau normalisasi (opsional, kalau model belum softmax)
                        val sum = result.sum()
                        val normalized = if (sum > 0) result.map { it / sum }.toFloatArray() else result

                        val maxIdx = normalized.indices.maxByOrNull { normalized[it] } ?: 0

                        kategoriLung = when (maxIdx) {
                            0 -> "Tinggi"   // High
                            1 -> "Rendah"   // Low
                            2 -> "Sedang"   // Medium
                            else -> "Tidak diketahui"
                        }

                        Log.d("LungModelOutput", "Probabilitas: High=${normalized[0]}, Low=${normalized[1]}, Medium=${normalized[2]}")
                        Log.d("LungModelKategori", "Prediksi: $kategoriLung")

                    } catch (e: Exception) {
                        Toast.makeText(context, "Error ML (Lung Disease): ${e.message}", Toast.LENGTH_LONG).show()
                        android.util.Log.e("LungModelError", "Gagal inferensi: ${e.message}", e)
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

private fun encodeGender(gender: String?): Float {
    return if (gender.equals("Male", true)) 1f else 0f
}

private fun encodeSmoking(status: String?): FloatArray {
    return when (status) {
        "Current" -> floatArrayOf(1f, 0f, 0f)
        "Ex-Smoker" -> floatArrayOf(0f, 1f, 0f)
        "Non-Smoker" -> floatArrayOf(0f, 0f, 1f)
        else -> floatArrayOf(0f, 0f, 0f)
    }
}

fun encodeMedication(medication: String): FloatArray {
    return when (medication) {
        "Controller" -> floatArrayOf(1f, 0f)
        "Inhaler" -> floatArrayOf(0f, 1f)
        else -> floatArrayOf(0f, 0f)
    }
}
