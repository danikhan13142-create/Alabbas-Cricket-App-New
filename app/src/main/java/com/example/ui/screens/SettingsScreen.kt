package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.viewmodel.CricketViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color

@Composable
fun SettingsScreen(
    viewModel: CricketViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val themeSettings by viewModel.themeSettings.collectAsState()
    val scorecardCustomization by viewModel.scorecardCustomization.collectAsState()

    var showBackupDialog by remember { mutableStateOf<String?>(null) }
    var importJsonText by remember { mutableStateOf("") }
    var showImportModal by remember { mutableStateOf(false) }

    var primaryHexInput by remember(themeSettings) { mutableStateOf(themeSettings.customPrimaryColorHex ?: "#1B3D2B") }
    var accentHexInput by remember(themeSettings) { mutableStateOf(themeSettings.customAccentColorHex ?: "#D4AF37") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Settings & Personalization",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        // Team Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Team Profile", color = CricketGoldLight, style = MaterialTheme.typography.bodySmall)
                    Text("Alabbas Cricket Mithial", color = CricketGold, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("District League • Mithial Sports Stadium", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // 1. App Theme Selection Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("App Theme Preset", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Dark Mode", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.width(4.dp))
                            Switch(
                                checked = themeSettings.isDarkMode,
                                onCheckedChange = { isDark ->
                                    viewModel.updateThemeSettings(themeSettings.copy(isDarkMode = isDark))
                                }
                            )
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(AppThemeStyle.values()) { style ->
                            val isSelected = themeSettings.themeStyle == style
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.updateThemeSettings(themeSettings.copy(themeStyle = style))
                                },
                                label = { Text(style.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // 2. Custom Color Customization
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Custom Theme Colors", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = primaryHexInput,
                            onValueChange = { 
                                primaryHexInput = it
                                viewModel.updateThemeSettings(themeSettings.copy(customPrimaryColorHex = it))
                            },
                            label = { Text("Primary Color (Hex)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = accentHexInput,
                            onValueChange = { 
                                accentHexInput = it
                                viewModel.updateThemeSettings(themeSettings.copy(customAccentColorHex = it))
                            },
                            label = { Text("Accent Color (Hex)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    TextButton(
                        onClick = {
                            primaryHexInput = "#1B3D2B"
                            accentHexInput = "#D4AF37"
                            viewModel.updateThemeSettings(
                                themeSettings.copy(
                                    customPrimaryColorHex = null,
                                    customAccentColorHex = null
                                )
                            )
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Colors to Default")
                    }
                }
            }
        }

        // 3. Font Customization Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Font & Typography", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                    Text("Font Style", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(AppFontFamily.values()) { font ->
                            val isSelected = themeSettings.fontFamily == font
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateThemeSettings(themeSettings.copy(fontFamily = font)) },
                                label = { Text(font.displayName) }
                            )
                        }
                    }

                    Text("Font Size Density", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(AppFontScale.values()) { scale ->
                            val isSelected = themeSettings.fontScale == scale
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateThemeSettings(themeSettings.copy(fontScale = scale)) },
                                label = { Text(scale.displayName) }
                            )
                        }
                    }
                }
            }
        }

        // 4. Scorecard Customization Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Scorecard Display Preferences", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Customize what details are shown on live & match scorecards.", style = MaterialTheme.typography.bodySmall)

                    ScorecardToggleItem("Team Crest / Logos", scorecardCustomization.showTeamLogo) {
                        viewModel.updateScorecardCustomization(scorecardCustomization.copy(showTeamLogo = it))
                    }
                    ScorecardToggleItem("Strike / Non-Striker Indicators", scorecardCustomization.showStrikeIndicator) {
                        viewModel.updateScorecardCustomization(scorecardCustomization.copy(showStrikeIndicator = it))
                    }
                    ScorecardToggleItem("Partnerships Breakdown", scorecardCustomization.showPartnerships) {
                        viewModel.updateScorecardCustomization(scorecardCustomization.copy(showPartnerships = it))
                    }
                    ScorecardToggleItem("Fall of Wickets (FOW)", scorecardCustomization.showFallOfWickets) {
                        viewModel.updateScorecardCustomization(scorecardCustomization.copy(showFallOfWickets = it))
                    }
                    ScorecardToggleItem("Extras Breakdown", scorecardCustomization.showExtrasBreakdown) {
                        viewModel.updateScorecardCustomization(scorecardCustomization.copy(showExtrasBreakdown = it))
                    }
                    ScorecardToggleItem("Current & Required Run Rates", scorecardCustomization.showRunRates) {
                        viewModel.updateScorecardCustomization(scorecardCustomization.copy(showRunRates = it))
                    }
                    ScorecardToggleItem("Visual Wagon Wheel Graph", scorecardCustomization.showWagonWheel) {
                        viewModel.updateScorecardCustomization(scorecardCustomization.copy(showWagonWheel = it))
                    }
                    ScorecardToggleItem("Ball-by-Ball Commentary Stream", scorecardCustomization.showCommentary) {
                        viewModel.updateScorecardCustomization(scorecardCustomization.copy(showCommentary = it))
                    }
                }
            }
        }

        // Backup & Restore Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Backup & Local Restore", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Export your players, match scorecards, fixtures, and expenses as a clean offline JSON backup, or restore from a saved backup file.", style = MaterialTheme.typography.bodyMedium)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val json = viewModel.exportBackupJson()
                                    showBackupDialog = json
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export Backup")
                        }

                        OutlinedButton(
                            onClick = { showImportModal = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Upload, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Import Backup")
                        }
                    }
                }
            }
        }

        // About App
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("About Application", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Alabbas Cricket Mithial v2.5 Pro", fontWeight = FontWeight.Bold, color = CricketGreenPrimary)
                    Text("Complete offline Android Cricket Team Management & Live Ball-by-Ball Scoring system.")
                    Text("Currencies used: Pakistani Rupees (Rs)")
                }
            }
        }
    }

    // Export Backup JSON Dialog
    if (showBackupDialog != null) {
        val json = showBackupDialog!!
        AlertDialog(
            onDismissRequest = { showBackupDialog = null },
            title = { Text("Backup JSON Generated") },
            text = {
                OutlinedTextField(
                    value = json,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("Cricket Backup", json)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Backup copied to clipboard!", Toast.LENGTH_SHORT).show()
                        showBackupDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary)
                ) { Text("Copy to Clipboard") }
            },
            dismissButton = {
                TextButton(onClick = { showBackupDialog = null }) { Text("Close") }
            }
        )
    }

    // Import Backup JSON Dialog
    if (showImportModal) {
        AlertDialog(
            onDismissRequest = { showImportModal = false },
            title = { Text("Import Backup JSON") },
            text = {
                OutlinedTextField(
                    value = importJsonText,
                    onValueChange = { importJsonText = it },
                    placeholder = { Text("Paste JSON backup data here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val success = viewModel.importBackupJson(importJsonText)
                            if (success) {
                                Toast.makeText(context, "Backup imported successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Invalid JSON format!", Toast.LENGTH_SHORT).show()
                            }
                            showImportModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary)
                ) { Text("Restore") }
            },
            dismissButton = {
                TextButton(onClick = { showImportModal = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ScorecardToggleItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
