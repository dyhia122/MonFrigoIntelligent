package com.example.myapplication


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Aliment
import com.example.myapplication.FrigoDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FrigoViewModel(private val database: FrigoDatabase) : ViewModel() {
    val aliments: Flow<List<Aliment>> = database.alimentDao().getAllAliments()

    fun addAliment(aliment: Aliment) {
        viewModelScope.launch {
            database.alimentDao().insert(aliment)
        }
    }

    fun deleteAliment(aliment: Aliment) {
        viewModelScope.launch {
            database.alimentDao().delete(aliment)
        }
    }
}