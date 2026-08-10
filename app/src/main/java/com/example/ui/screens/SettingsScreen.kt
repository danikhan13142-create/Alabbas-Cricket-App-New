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

@Composable
fun SettingsScreen(
    viewModel: CricketViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showBackupDialog by remember { mutableStateOf<String?>(null) }
    var importJsonText by remember { mutableStateOf("") }
    var showImportModal by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Settings & Backup",
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
                colors = CardDefaults.cardColors(containerColor = CricketGreenDark)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Team Profile", color = CricketGoldLight, style = MaterialTheme.typography.bodySmall)
                    Text("Alabbas Cricket Mithial", color = CricketGold, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("District League • Mithial Sports Stadium", color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.bodyMedium)
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
                    Text("Alabbas Cricket Mithial v1.0.0", fontWeight = FontWeight.Bold, color = CricketGreenPrimary)
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
