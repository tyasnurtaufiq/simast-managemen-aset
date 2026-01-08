// ui/login/LoginActivity.kt
package com.example.projekmanajemenaset.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projekmanajemenaset.R
import com.example.projekmanajemenaset.data.database.DatabaseHelper
import com.example.projekmanajemenaset.ui.dashboard.DashboardActivity
import com.example.projekmanajemenaset.utils.SessionManager
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize session manager
        sessionManager = SessionManager(this)

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToDashboard()
            return
        }

        setContentView(R.layout.activity_login)

        // Initialize views
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        // Initialize database
        dbHelper = DatabaseHelper(this)

        // Set click listener
        btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validation
        if (username.isEmpty()) {
            etUsername.error = "Username tidak boleh kosong"
            etUsername.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Password tidak boleh kosong"
            etPassword.requestFocus()
            return
        }

        // Authenticate user
        val user = dbHelper.loginUser(username, password)

        if (user != null) {
            // Save session
            sessionManager.createLoginSession(user.id, user.username, user.fullName)

            Toast.makeText(this, "Selamat datang, ${user.fullName}!", Toast.LENGTH_SHORT).show()

            // Navigate to dashboard
            navigateToDashboard()
        } else {
            Toast.makeText(this, "Username atau password salah!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}