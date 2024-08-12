package com.mukto.todo.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukto.todo.MainApplication
import com.mukto.todo.db.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class ExpenseViewModel : ViewModel() {

    val expenseDao = MainApplication.todoDatabase.getExpenseDao()
    val expenseList: LiveData<List<Expense>> = expenseDao.getAllExpenses()
    val totalCost: LiveData<Double> = expenseDao.getTotalCost()

    @RequiresApi(Build.VERSION_CODES.O)
    fun addExpense(amount: Double, description: String, category: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseDao.addExpense(Expense(amount = amount, description = description, date = Date(), category = category))
        }
    }

    fun deleteExpense(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseDao.deleteExpense(id)
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseDao.updateExpense(expense)
        }
    }

    fun getMonthlyCost(monthYear: String): LiveData<Double> {
        return expenseDao.getMonthlyCost(monthYear)
    }
}
