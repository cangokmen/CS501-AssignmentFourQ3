package com.example.assignmentfourq3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

// Represents a single temperature reading with its timestamp.
data class TemperatureReading(val value: Float, val timestamp: Long = System.currentTimeMillis())

// Define the custom green color
val androidGreen = Color(0xFF3DDC84)

// Holds the current state of the temperature dashboard.
data class TemperatureUiState(
    val readings: List<TemperatureReading> = emptyList(),
    val isPaused: Boolean = false
) {
    // Derived properties for easy access in the UI.
    val currentT: Float? get() = readings.firstOrNull()?.value
    val averageT: Float get() = if (readings.isEmpty()) 0f else readings.sumOf { it.value.toDouble() }.toFloat() / readings.size
    val minT: Float? get() = readings.minOfOrNull { it.value }
    val maxT: Float? get() = readings.maxOfOrNull { it.value }
}

// Manages the state and logic for the temperature dashboard.
class TemperatureViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TemperatureUiState())
    val uiState: StateFlow<TemperatureUiState> = _uiState

    private var dataJob: Job? = null
    private val maxReadings = 20

    init {
        startDataSimulation()
    }

    // Starts the coroutine to generate temperature data.
    private fun startDataSimulation() {
        dataJob?.cancel() // Cancel any existing job.
        dataJob = viewModelScope.launch {
            while (true) {
                if (!_uiState.value.isPaused) {
                    val newTemp = Random.nextFloat() * 20f + 65f
                    val newReading = TemperatureReading(newTemp)

                    _uiState.value = _uiState.value.copy(
                        readings = (_uiState.value.readings + newReading).takeLast(maxReadings)
                    )
                }
                delay(2000) // Wait for 2 seconds.
            }
        }
    }

    // Toggles the paused state of the data simulation.
    fun togglePause() {
        val isCurrentlyPaused = _uiState.value.isPaused
        _uiState.value = _uiState.value.copy(isPaused = !isCurrentlyPaused)
    }

    // Ensures the coroutine is cancelled when the ViewModel is cleared.
    override fun onCleared() {
        super.onCleared()
        dataJob?.cancel()
    }
}

// --- MAIN ACTIVITY ---

class MainActivity : ComponentActivity() {
    private val viewModel: TemperatureViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    TemperatureDashboardScreen(viewModel)
                }
            }
        }
    }
}

// --- COMPOSABLE UI ---

/**
 * Formats a timestamp (Long) into a "HH:mm:ss" string.
 */
fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * The main screen composable that orchestrates the UI.
 */
@Composable
fun TemperatureDashboardScreen(viewModel: TemperatureViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- Header and Control Button ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Temp Dashboard", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = { viewModel.togglePause() },                colors = ButtonDefaults.buttonColors(
                containerColor = androidGreen,
                contentColor = Color.Black
            )) {
                Text(if (uiState.isPaused) "Resume" else "Pause")
            }
        }
        Spacer(Modifier.height(16.dp))

        // --- Summary Statistics Section ---
        SummaryStats(uiState)
        Spacer(Modifier.height(16.dp))

        // --- Chart Section ---
        Text("Chart (Last 20)", style = MaterialTheme.typography.titleMedium)
        TemperatureChart(readings = uiState.readings)
        Spacer(Modifier.height(16.dp))

        // --- Readings List Section ---
        Text("History", style = MaterialTheme.typography.titleMedium)
        ReadingsList(readings = uiState.readings)
    }
}

/**
 * Displays the current, average, min, and max temperatures.
 */
@Composable
fun SummaryStats(state: TemperatureUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "${state.currentT?.let { "%.1f".format(it) } ?: "--"}°F",
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Current Temperature",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatCard("Avg", state.averageT, "°F")
            StatCard("Min", state.minT ?: 0f, "°F")
            StatCard("Max", state.maxT ?: 0f, "°F")
        }
    }
}

/**
 * A card for displaying a single statistic (e.g., Avg, Min, Max).
 */
@Composable
fun StatCard(label: String, value: Float, unit: String) {
    Card(
        // Add this line to set the background color
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(
                text = if (value == 0f) "--" else "%.1f".format(value),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(unit, style = MaterialTheme.typography.bodySmall)
        }
    }
}

/**
 * Displays a list of temperature readings with their timestamps.
 */
@Composable
fun ReadingsList(readings: List<TemperatureReading>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
    ) {
        items(readings.reversed()) { reading ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTimestamp(reading.timestamp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "%.2f°F".format(reading.value),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        }
    }
}

/**
 * A simple bar chart to visualize temperature readings.
 */
@Composable
fun TemperatureChart(readings: List<TemperatureReading>) {
    val minT = 65f
    val maxT = 85f

    Row(modifier = Modifier
        .fillMaxWidth()
        .height(120.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .padding(horizontal = 4.dp, vertical = 8.dp), // Added horizontal padding
        // Reduce spacing between bars to fit all 20 items on screen
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        readings.forEach { reading ->
            val percentage = ((reading.value - minT) / (maxT - minT)).coerceIn(0f, 1f)
            val barColor = androidGreen

            Box(
                modifier = Modifier
                    .weight(1f) // Each bar takes equal
                    .fillMaxHeight(percentage)
                    .background(barColor)
            )
        }
    }
}
