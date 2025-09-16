package com.example.calculadora

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calculadora.R.id.txtResultado
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    private lateinit var tvDisplay: TextView

    private var currentInput: String = ""
    private var fullExpression: String = ""
    private var operand: Double? = null
    private var pendingOp: String? = null
    private var resetInput: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TextView de display
        tvDisplay = findViewById(txtResultado)

        // Botões de dígitos
        val digits = listOf(
            "0" to R.id.btn0,
            "1" to R.id.btn1,
            "2" to R.id.btn2,
            "3" to R.id.btn3,
            "4" to R.id.btn4,
            "5" to R.id.btn5,
            "6" to R.id.btn6,
            "7" to R.id.btn7,
            "8" to R.id.btn8,
            "9" to R.id.btn9,
            "." to R.id.btnPonto
        )
        digits.forEach { (digit, id) ->
            findViewById<Button>(id).setOnClickListener { appendDigit(digit) }
        }

        // Botões de operações
        val ops = listOf(
            "+" to R.id.btnSomar,
            "-" to R.id.btnSubtrair,
            "×" to R.id.btnMultiplicar,
            "÷" to R.id.btnDividir
        )
        ops.forEach { (op, id) ->
            findViewById<Button>(id).setOnClickListener { onOperator(op) }
        }

        // Botão igual
        findViewById<Button>(R.id.btnIgual).setOnClickListener { onEquals() }

        // Botão limpar tudo (AC)
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { clearAll() }

        // Botão backspace (⌫)
        findViewById<Button>(R.id.btnClear).setOnClickListener { backspace() }

        // Botões de parênteses
        findViewById<MaterialButton>(R.id.btnParentesesE).setOnClickListener { appendParenthesis("(") }
        findViewById<MaterialButton>(R.id.btnParentesesD).setOnClickListener { appendParenthesis(")") }

        // Botões de navegação do cursor
        findViewById<MaterialButton>(R.id.btnAvancar).setOnClickListener { moveCursor(1) }
        findViewById<MaterialButton>(R.id.btnVoltar).setOnClickListener { moveCursor(-1) }

        // Botão de quadrado (x²)
        findViewById<MaterialButton>(R.id.btnQuadrado).setOnClickListener { calculateSquare() }

        // Botão de raiz quadrada (√)
        findViewById<MaterialButton>(R.id.btnRaiz).setOnClickListener { calculateSquareRoot() }

        updateDisplay()
    }

    private fun appendDigit(d: String) {
        if (resetInput) {
            currentInput = ""
            resetInput = false
        }

        if (d == "." && currentInput.contains(".")) return
        currentInput = if (currentInput == "0") d else currentInput + d
        updateDisplay()
    }

    private fun appendParenthesis(parenthesis: String) {
        if (resetInput) {
            currentInput = ""
            resetInput = false
        }

        currentInput = if (currentInput == "0") parenthesis else currentInput + parenthesis
        updateDisplay()
    }

    private fun onOperator(op: String) {
        if (currentInput.isNotEmpty()) {
            // Adiciona o número atual à expressão completa
            if (fullExpression.isNotEmpty() && !fullExpression.endsWith(" ")) {
                fullExpression += " $currentInput"
            } else {
                fullExpression += currentInput
            }

            // Adiciona o operador à expressão completa
            fullExpression += " $op "

            val value = currentInput.toDoubleOrNull()
            if (value != null) {
                if (operand == null) {
                    operand = value
                } else {
                    operand = performOperation(operand!!, value, pendingOp)
                }
            }
            resetInput = true
        } else if (fullExpression.isNotEmpty() && pendingOp != null) {
            // Permite trocar o operador se não houver novo número
            fullExpression = fullExpression.dropLast(3) + " $op "
        }

        pendingOp = op
        updateDisplay()
    }

    private fun onEquals() {
        if (operand != null && currentInput.isNotEmpty()) {
            // Adiciona o último número à expressão completa
            if (fullExpression.isNotEmpty() && !fullExpression.endsWith(" ")) {
                fullExpression += " $currentInput"
            } else {
                fullExpression += currentInput
            }

            val value = currentInput.toDoubleOrNull() ?: return
            val result = performOperation(operand!!, value, pendingOp)

            // Mostra a expressão completa e o resultado
            fullExpression += " = $result"
            currentInput = result.toString()
            operand = null
            pendingOp = null
            resetInput = true
            updateDisplay()
        }
    }

    private fun performOperation(a: Double, b: Double, op: String?): Double {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "×" -> a * b
            "÷" -> if (b == 0.0) {
                Toast.makeText(this, "Divisão por zero", Toast.LENGTH_SHORT).show()
                a
            } else a / b
            else -> b
        }
    }

    private fun calculateSquare() {
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull()
            if (value != null) {
                fullExpression = "($currentInput)² = "
                currentInput = (value * value).toString()
                resetInput = true
                updateDisplay()
            }
        }
    }

    private fun calculateSquareRoot() {
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull()
            if (value != null && value >= 0) {
                fullExpression = "√($currentInput) = "
                currentInput = Math.sqrt(value).toString()
                resetInput = true
                updateDisplay()
            } else if (value != null && value < 0) {
                Toast.makeText(this, "Não é possível calcular raiz de número negativo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearAll() {
        currentInput = ""
        fullExpression = ""
        operand = null
        pendingOp = null
        resetInput = false
        updateDisplay()
    }

    private fun backspace() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            if (currentInput.isEmpty()) currentInput = "0"
            updateDisplay()
        }
    }

    private fun moveCursor(direction: Int) {
        // Esta função é mais simbólica, já que o TextView padrão não permite
        // controle direto do cursor via código de forma simples
        Toast.makeText(this,
            if (direction > 0) "Cursor avançado" else "Cursor retrocedido",
            Toast.LENGTH_SHORT).show()
    }

    private fun updateDisplay() {
        // Mostra a expressão completa se existir, senão mostra o input atual
        val displayText = if (fullExpression.isNotEmpty()) {
            "$fullExpression${if (!resetInput) currentInput else ""}"
        } else {
            currentInput.ifEmpty { "0" }
        }

        tvDisplay.text = displayText
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentInput", currentInput)
        outState.putString("fullExpression", fullExpression)
        outState.putDouble("operand", operand ?: Double.NaN)
        outState.putString("pendingOp", pendingOp)
        outState.putBoolean("resetInput", resetInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentInput = savedInstanceState.getString("currentInput", "")
        fullExpression = savedInstanceState.getString("fullExpression", "")
        val opnd = savedInstanceState.getDouble("operand", Double.NaN)
        operand = if (opnd.isNaN()) null else opnd
        pendingOp = savedInstanceState.getString("pendingOp")
        resetInput = savedInstanceState.getBoolean("resetInput", false)
        updateDisplay()
    }
}