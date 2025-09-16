package com.example.calculadora

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class HistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var btnClose: MaterialButton
    private val historyList = mutableListOf<CalculationHistory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        rvHistory = findViewById(R.id.rvHistory)
        btnClose = findViewById(R.id.btnClose)

        // Recebe o histórico da intent
        val history = intent.getStringArrayListExtra("history") ?: ArrayList()
        historyList.clear()
        history.forEach { item ->
            val parts = item.split("|")
            if (parts.size == 2) {
                historyList.add(CalculationHistory(parts[0], parts[1]))
            }
        }

        // Configura o RecyclerView
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = HistoryAdapter(historyList)

        btnClose.setOnClickListener {
            finish()
        }
    }

    data class CalculationHistory(val expression: String, val result: String)

    class HistoryAdapter(private val historyList: List<CalculationHistory>) :
        RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvExpression: TextView = itemView.findViewById(R.id.tvExpression)
            val tvResult: TextView = itemView.findViewById(R.id.tvResult)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_history, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = historyList[position]
            holder.tvExpression.text = item.expression
            holder.tvResult.text = "= ${item.result}"
        }

        override fun getItemCount(): Int = historyList.size
    }
}