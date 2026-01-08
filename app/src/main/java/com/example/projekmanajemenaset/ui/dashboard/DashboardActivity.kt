// ui/dashboard/DashboardActivity.kt
package com.example.projekmanajemenaset.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.projekmanajemenaset.R
import com.example.projekmanajemenaset.data.database.DatabaseHelper
import com.example.projekmanajemenaset.ui.asset.AssetActivity
import com.example.projekmanajemenaset.ui.login.LoginActivity
import com.example.projekmanajemenaset.utils.SessionManager

class DashboardActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var tvUserName: TextView
    private lateinit var tvTotalAssets: TextView
    private lateinit var tvTotalValue: TextView
    private lateinit var cardAssetManagement: CardView
    private lateinit var btnLogout: Button
    private lateinit var sessionManager: SessionManager
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize
        sessionManager = SessionManager(this)
        dbHelper = DatabaseHelper(this)

        // Check if logged in
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        tvUserName = findViewById(R.id.tvUserName)
        tvTotalAssets = findViewById(R.id.tvTotalAssets)
        tvTotalValue = findViewById(R.id.tvTotalValue)
        cardAssetManagement = findViewById(R.id.cardAssetManagement)
        btnLogout = findViewById(R.id.btnLogout)

        // Setup toolbar
        setSupportActionBar(toolbar)

        // Load user data
        loadUserData()

        // Load statistics
        loadStatistics()

        // Set click listeners
        cardAssetManagement.setOnClickListener {
            val intent = Intent(this, AssetActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh statistics when returning to dashboard
        loadStatistics()
    }

    private fun loadUserData() {
        val fullName = sessionManager.getFullName()
        tvUserName.text = fullName ?: "User"
    }

    private fun loadStatistics() {
        val assets = dbHelper.getAllAssets()
        val totalAssets = assets.size
        val totalQuantity = assets.sumOf { it.quantity }

        tvTotalAssets.text = totalAssets.toString()
        tvTotalValue.text = totalQuantity.toString()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                logout()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun logout() {
        sessionManager.logout()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}