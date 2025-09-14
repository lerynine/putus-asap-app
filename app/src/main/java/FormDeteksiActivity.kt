package com.example.putusasap

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

@OptIn(ExperimentalMaterial3Api::class)
class FormDeteksiActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PutusAsapTheme {
                FormDeteksiScreen(
                    onBack = { finish() },
                    onSubmit = { dataMap ->
                        Toast.makeText(this, "Hasil siap — data terisi", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
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
                label = { RequiredLabel("Age (tahun)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = height,
                onValueChange = { height = it.filter(Char::isDigit) },
                label = { RequiredLabel("Height (cm)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it.filter(Char::isDigit) },
                label = { RequiredLabel("Weight (kg)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            GenderRadioRow(selected = gender, onSelect = { gender = it })

            Spacer(Modifier.height(16.dp))

            // --- TEKANAN DARAH & JANTUNG
            SectionTitle("Data Tekanan Darah & Jantung")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = apHi,
                    onValueChange = { apHi = it.filter(Char::isDigit) },
                    label = { Text("Systolic") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = apLo,
                    onValueChange = { apLo = it.filter(Char::isDigit) },
                    label = { Text("Diastolic") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = cholesterol,
                    onValueChange = { cholesterol = it.filter(Char::isDigit) },
                    label = { Text("Cholesterol") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = glucose,
                    onValueChange = { glucose = it.filter(Char::isDigit) },
                    label = { Text("Glucose") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            ToggleRow("Physical activity (aktif/tidak)", physicalActivity) { physicalActivity = it }

            Spacer(Modifier.height(16.dp))

            // --- ASMA & PERNAPASAN
            SectionTitle("Data Pernapasan")
            SmokingStatusDropdown(selected = smokingStatus, onSelect = { smokingStatus = it })
            OutlinedTextField(
                value = medication,
                onValueChange = { medication = it },
                label = { RequiredLabel("Medication") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = peakFlow,
                onValueChange = { peakFlow = it.filter(Char::isDigit) },
                label = { Text("Peak Flow (opsional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(16.dp))

            // --- FAKTOR RISIKO
            SectionTitle("Faktor Risiko Paru & Gaya Hidup")
            SliderWithLabel("Air Pollution Exposure", airPollution) { airPollution = it }
            SliderWithLabel("Alcohol Use (kebiasaan)", riskAlcoholScale) { riskAlcoholScale = it }
            SliderWithLabel("Occupational Hazards", occupationalHazards) { occupationalHazards = it }
            SliderWithLabel("Genetic Risk (riwayat keluarga)", geneticRisk) { geneticRisk = it }
            SliderWithLabel("Balanced Diet", balancedDiet) { balancedDiet = it }
            SliderWithLabel("Obesity", obesityScale) { obesityScale = it }
            ToggleRow("Dust Allergy (ada/tidak)", dustAllergyPresent) { dustAllergyPresent = it }
            if (dustAllergyPresent) {
                SliderWithLabel("Dust Allergy Intensity", dustAllergyIntensity) { dustAllergyIntensity = it }
            }
            ToggleRow("Chronic Lung Disease (self-report)", chronicLungDisease) { chronicLungDisease = it }
            ToggleRow("Smoking (habitual)", smokingHabitual) { smokingHabitual = it }
            ToggleRow("Passive Smoker", passiveSmoker) { passiveSmoker = it }

            Spacer(Modifier.height(16.dp))

            // --- GEJALA KLINIS
            SectionTitle("Gejala Klinis")
            SymptomSwitch("Chest Pain", chestPain) { chestPain = it }
            SymptomSwitch("Coughing of Blood", coughingBlood) { coughingBlood = it }
            SymptomSwitch("Fatigue", fatigue) { fatigue = it }
            SymptomSwitch("Weight Loss", weightLoss) { weightLoss = it }
            SymptomSwitch("Shortness of Breath", shortnessOfBreath) { shortnessOfBreath = it }
            SymptomSwitch("Wheezing", wheezing) { wheezing = it }
            SymptomSwitch("Swallowing Difficulty", swallowingDifficulty) { swallowingDifficulty = it }
            SymptomSwitch("Clubbing of Nails", clubbing) { clubbing = it }
            SymptomSwitch("Frequent Cold", frequentCold) { frequentCold = it }
            SymptomSwitch("Dry Cough", dryCough) { dryCough = it }
            SymptomSwitch("Snoring", snoring) { snoring = it }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (!mandatoryFilled) {
                        Toast.makeText(context, "Lengkapi semua field wajib (*)", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid ?: "unknown_user"

                    val data = mapOf(
                        // --- Basic Info ---
                        "user_id" to userId,
                        "age" to age.toIntOrNull(),
                        "gender" to if (gender == "Male") 2 else 1,   // 1=Female, 2=Male

                        // --- Cardio Dataset Fields ---
                        "height" to height.toIntOrNull(),
                        "weight" to weight.toFloatOrNull(),
                        "ap_hi" to apHi.toIntOrNull(),
                        "ap_lo" to apLo.toIntOrNull(),
                        "cholesterol" to cholesterol.toIntOrNull(),
                        "glucose" to glucose.toIntOrNull(),
                        "smoke" to if (smokingHabitual) 1 else 0,
                        "alco" to if (riskAlcoholScale > 5) 1 else 0, // contoh konversi slider ke boolean
                        "active" to if (physicalActivity) 1 else 0,

                        // --- Asthma Dataset Fields ---
                        "smoking_status" to smokingStatus,
                        "medication" to medication,
                        "peak_flow" to peakFlow.toIntOrNull(),

                        // --- Lung Cancer Dataset Fields ---
                        "air_pollution" to airPollution.toInt(),
                        "alcohol_use_scale" to riskAlcoholScale.toInt(),
                        "dust_allergy" to if (dustAllergyPresent) 1 else 0,
                        "dust_allergy_intensity" to if (dustAllergyPresent) dustAllergyIntensity.toInt() else null,
                        "occupational_hazards" to occupationalHazards.toInt(),
                        "genetic_risk" to geneticRisk.toInt(),
                        "chronic_lung_disease" to if (chronicLungDisease) 1 else 0,
                        "balanced_diet" to balancedDiet.toInt(),
                        "obesity_scale" to obesityScale.toInt(),
                        "passive_smoker" to if (passiveSmoker) 1 else 0,

                        // --- Symptoms ---
                        "symptoms.chest_pain" to if (chestPain) 1 else 0,
                        "symptoms.coughing_blood" to if (coughingBlood) 1 else 0,
                        "symptoms.fatigue" to if (fatigue) 1 else 0,
                        "symptoms.weight_loss" to if (weightLoss) 1 else 0,
                        "symptoms.shortness_breath" to if (shortnessOfBreath) 1 else 0,
                        "symptoms.wheezing" to if (wheezing) 1 else 0,
                        "symptoms.swallowing_difficulty" to if (swallowingDifficulty) 1 else 0,
                        "symptoms.clubbing" to if (clubbing) 1 else 0,
                        "symptoms.frequent_cold" to if (frequentCold) 1 else 0,
                        "symptoms.dry_cough" to if (dryCough) 1 else 0,
                        "symptoms.snoring" to if (snoring) 1 else 0,

                        "created_at" to com.google.firebase.Timestamp.now()
                    )

                    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    db.collection("riwayat_deteksi")
                        .add(data)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                            onSubmit(data) // lanjut ke MainActivity
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Gagal simpan: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = mandatoryFilled,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC15F56))
            ) {
                Text("Lihat Hasil", color = Color.White)
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
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Gender:", modifier = Modifier.width(80.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected == "Male", onClick = { onSelect("Male") })
            Text("Male", modifier = Modifier.padding(end = 8.dp))
            RadioButton(selected == "Female", onClick = { onSelect("Female") })
            Text("Female")
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
    val options = listOf("Non-Smoker", "Ex-Smoker", "Current Smoker")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = { },
            readOnly = true,
            label = { Text("Smoking Status *") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


