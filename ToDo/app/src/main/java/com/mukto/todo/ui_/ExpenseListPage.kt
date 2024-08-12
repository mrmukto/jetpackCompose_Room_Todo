package com.mukto.todo.ui_

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mukto.todo.R
import com.mukto.todo.db.Expense
import com.mukto.todo.viewModel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseListPage(viewModel: ExpenseViewModel) {
    // Observe the expense list and costs
    val expenseList by viewModel.expenseList.observeAsState(emptyList())
    val totalCost by viewModel.totalCost.observeAsState()
    val monthYear = remember { SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date()) }
    val monthlyCost by viewModel.getMonthlyCost(monthYear).observeAsState()

    // Input fields and dialog state
    var inputAmount by remember { mutableStateOf("") }
    var inputDescription by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var customCategory by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedExpense by remember { mutableStateOf<Expense?>(null) }
    val categories = listOf("Transport", "Home Expense", "Others")


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Display total and monthly costs
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Expenses",
                fontSize = 22.sp
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Total Cost: ${"%.2f".format(totalCost)} TK",
                fontSize = 16.sp
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Monthly Cost: ${"%.2f".format(monthlyCost)} TK",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Display expense list or a message if empty
            if (expenseList.isEmpty()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "No expenses yet",
                    fontSize = 16.sp
                )
            } else {
                LazyColumn(content = {
                    itemsIndexed(expenseList) { index: Int, item: Expense ->
                        ExpenseItem(item = item,
                            onDelete = { viewModel.deleteExpense(item.id) },
                            onUpdate = {
                                selectedExpense = item
                                inputAmount = item.amount.toString()
                                inputDescription = item.description.orEmpty()
                                selectedCategory = item.category.orEmpty()
                                showDialog = true
                            })
                    }
                })
            }
        }

        // Floating action button for adding a new expense
        FloatingActionButton(
            onClick = {
                selectedExpense = null
                inputAmount = ""
                inputDescription = ""
                selectedCategory = ""
                customCategory = ""
                showDialog = true
            }, modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Expense")
        }

        // Dialog for adding or updating an expense
        if (showDialog) {
            var amountError by rememberSaveable { mutableStateOf<String?>(null) }
            var descriptionError by rememberSaveable { mutableStateOf<String?>(null) }
            var categoryError by rememberSaveable { mutableStateOf<String?>(null) }

            // Validation for the input fields
            amountError = when {
                inputAmount.isBlank() -> "Please enter an amount."
                inputAmount.toDoubleOrNull() == null -> "Invalid amount."
                else -> null
            }
            categoryError = when {
                customCategory.isBlank() && selectedCategory.isBlank() -> "Please enter a category or select from dropdown."
                else -> null
            }
            descriptionError = when {
                inputDescription.isBlank() -> "Please enter a description."
                inputDescription.length < 2 -> "Description is too short."
                else -> null
            }
            val finalCategory =
                if (customCategory.isNotEmpty()) customCategory else selectedCategory
            AlertDialog(onDismissRequest = { showDialog = false },
                title = { Text(text = if (selectedExpense == null) "Add Expense" else "Update Expense") },
                text = {
                    Column {
                        OutlinedTextField(value = inputAmount,
                            onValueChange = { inputAmount = it },
                            label = { Text("Amount") },
                            supportingText = { Text(text = amountError.orEmpty()) },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ))

                        Spacer(modifier = Modifier.height(8.dp))
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
                            expanded = !expanded
                        }) {
                            OutlinedTextField(value = finalCategory,
                                onValueChange = { input ->
                                    customCategory = input
                                    selectedCategory = ""
                                },
                                label = { Text("Category") },
                                supportingText = { Text(text = categoryError.orEmpty()) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded
                                    )
                                },
                                singleLine = true,
                                readOnly = customCategory.isEmpty()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }) {
                                categories.forEach { category ->
                                    DropdownMenuItem(onClick = {
                                        selectedCategory = category
                                        customCategory = ""
                                        expanded = false
                                    }, text = { Text(category) })
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = inputDescription,
                            onValueChange = { inputDescription = it },
                            label = { Text("Description (Optional)") },
                            supportingText = { Text(text = descriptionError.orEmpty()) })

                    }
                },
                confirmButton = {
                    Button(enabled = amountError == null && categoryError == null && finalCategory.isNotBlank(),
                        onClick = {
                            if (selectedExpense == null) {
                                viewModel.addExpense(
                                    amount = inputAmount.toDouble(),
                                    description = inputDescription.ifBlank { null },
                                    category = finalCategory.toString()
                                )
                            } else {
                                viewModel.updateExpense(
                                    selectedExpense!!.copy(
                                        amount = inputAmount.toDouble(),
                                        description = inputDescription,
                                        category = finalCategory.toString()
                                    )
                                )
                            }
                            showDialog = false
                            inputAmount = ""
                            inputDescription = ""
                            selectedCategory = ""
                            customCategory = ""
                        }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialog = false
                        inputAmount = ""
                        inputDescription = ""
                        selectedCategory = ""
                        customCategory = ""
                    }) {
                        Text("Cancel")
                    }
                })
        }
    }
}

@Composable
fun ExpenseItem(item: Expense, onDelete: () -> Unit, onUpdate: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.DarkGray)
            .padding(16.dp)
            .clickable { onUpdate() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = SimpleDateFormat("EEE MMM dd, yyyy", Locale.getDefault()).format(item.date),
                color = Color.LightGray,
                fontSize = 12.sp
            )
            Text(
                text = "${"%.2f".format(item.amount)} TK",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            item.category?.let {
                Text(
                    text = it, fontSize = 12.sp, color = Color.Cyan
                )
            }
            item.description?.let {
                Text(
                    text = it, fontSize = 14.sp, color = Color.Yellow
                )
            }
        }
        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = "Delete",
                tint = Color.Red
            )
        }
    }
}
