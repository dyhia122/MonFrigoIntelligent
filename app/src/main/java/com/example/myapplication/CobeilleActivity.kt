package com.example.myapplication

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class CorbeilleActivity : AppCompatActivity() {

    private lateinit var listeCorbeille: ListView
    private lateinit var adapter: CorbeilleAdapter
    private lateinit var corbeilleDao: CorbeilleDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_corbeille)

        listeCorbeille = findViewById(R.id.listeCorbeille)
        corbeilleDao = FrigoDatabase.getDatabase(this).corbeilleDao()
        adapter = CorbeilleAdapter(this, mutableListOf(), corbeilleDao)
        listeCorbeille.adapter = adapter

        lifecycleScope.launch {
            corbeilleDao.getAllCorbeille().collectLatest { list ->
                adapter.updateData(list)
            }
        }
    }
}
