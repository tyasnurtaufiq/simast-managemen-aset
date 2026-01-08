// ui/asset/AssetActivity.kt
package com.example.projekmanajemenaset.ui.asset

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmanajemenaset.R
import com.example.projekmanajemenaset.data.database.DatabaseHelper
import com.example.projekmanajemenaset.data.model.Asset
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AssetActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var etSearch: EditText
    private lateinit var btnClearSearch: ImageButton
    private lateinit var rvAssets: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: AssetAdapter
    private var allAssets: List<Asset> = emptyList()

    private val categories = arrayOf("Elektronik", "Furnitur", "Kendaraan", "Peralatan", "Bangunan", "Lainnya")
    private val conditions = arrayOf("Baik", "Rusak Ringan", "Rusak Berat")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asset)

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        etSearch = findViewById(R.id.etSearch)
        btnClearSearch = findViewById(R.id.btnClearSearch)
        rvAssets = findViewById(R.id.rvAssets)
        emptyState = findViewById(R.id.emptyState)
        fabAdd = findViewById(R.id.fabAdd)

        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Initialize database
        dbHelper = DatabaseHelper(this)

        // Setup RecyclerView
        setupRecyclerView()

        // Load assets
        loadAssets()

        // Setup search
        setupSearch()

        // FAB click listener
        fabAdd.setOnClickListener {
            showAssetDialog(null)
        }
    }

    private fun setupRecyclerView() {
        adapter = AssetAdapter(
            assets = emptyList(),
            onEditClick = { asset -> showAssetDialog(asset) },
            onDeleteClick = { asset -> showDeleteDialog(asset) },
            onItemClick = { asset -> showAssetDetail(asset) }
        )

        rvAssets.layoutManager = LinearLayoutManager(this)
        rvAssets.adapter = adapter
    }

    private fun loadAssets() {
        allAssets = dbHelper.getAllAssets()
        adapter.updateData(allAssets)
        updateEmptyState()
    }

    private fun updateEmptyState() {
        if (allAssets.isEmpty()) {
            emptyState.visibility = View.VISIBLE
            rvAssets.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            rvAssets.visibility = View.VISIBLE
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    btnClearSearch.visibility = View.VISIBLE
                    searchAssets(query)
                } else {
                    btnClearSearch.visibility = View.GONE
                    adapter.updateData(allAssets)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnClearSearch.setOnClickListener {
            etSearch.text.clear()
        }
    }

    private fun searchAssets(query: String) {
        val filteredAssets = dbHelper.searchAssets(query)
        adapter.updateData(filteredAssets)
    }

    private fun showAssetDialog(asset: Asset?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_asset, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Get dialog views
        val tvDialogTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val etAssetName = dialogView.findViewById<TextInputEditText>(R.id.etAssetName)
        val spinnerCategory = dialogView.findViewById<AutoCompleteTextView>(R.id.spinnerCategory)
        val etLocation = dialogView.findViewById<TextInputEditText>(R.id.etLocation)
        val etQuantity = dialogView.findViewById<TextInputEditText>(R.id.etQuantity)
        val spinnerCondition = dialogView.findViewById<AutoCompleteTextView>(R.id.spinnerCondition)
        val etPurchaseDate = dialogView.findViewById<TextInputEditText>(R.id.etPurchaseDate)
        val etDescription = dialogView.findViewById<TextInputEditText>(R.id.etDescription)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        // Setup spinners
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        spinnerCategory.setAdapter(categoryAdapter)

        val conditionAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, conditions)
        spinnerCondition.setAdapter(conditionAdapter)

        // Date picker
        val calendar = Calendar.getInstance()
        etPurchaseDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    val date = String.format("%04d-%02d-%02d", year, month + 1, day)
                    etPurchaseDate.setText(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Fill data if editing
        if (asset != null) {
            tvDialogTitle.text = "Edit Aset"
            etAssetName.setText(asset.name)
            spinnerCategory.setText(asset.category, false)
            etLocation.setText(asset.location)
            etQuantity.setText(asset.quantity.toString())
            spinnerCondition.setText(asset.condition, false)
            etPurchaseDate.setText(asset.purchaseDate)
            etDescription.setText(asset.description)
        } else {
            tvDialogTitle.text = "Tambah Aset"
            // Set default date to today
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            etPurchaseDate.setText(sdf.format(Date()))
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val name = etAssetName.text.toString().trim()
            val category = spinnerCategory.text.toString().trim()
            val location = etLocation.text.toString().trim()
            val quantityStr = etQuantity.text.toString().trim()
            val condition = spinnerCondition.text.toString().trim()
            val purchaseDate = etPurchaseDate.text.toString().trim()
            val description = etDescription.text.toString().trim()

            // Validation
            if (name.isEmpty()) {
                etAssetName.error = "Nama aset harus diisi"
                return@setOnClickListener
            }
            if (category.isEmpty()) {
                Toast.makeText(this, "Kategori harus dipilih", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (location.isEmpty()) {
                etLocation.error = "Lokasi harus diisi"
                return@setOnClickListener
            }
            if (quantityStr.isEmpty()) {
                etQuantity.error = "Jumlah harus diisi"
                return@setOnClickListener
            }
            if (condition.isEmpty()) {
                Toast.makeText(this, "Kondisi harus dipilih", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (purchaseDate.isEmpty()) {
                etPurchaseDate.error = "Tanggal pembelian harus diisi"
                return@setOnClickListener
            }

            val quantity = quantityStr.toIntOrNull() ?: 0
            if (quantity <= 0) {
                etQuantity.error = "Jumlah harus lebih dari 0"
                return@setOnClickListener
            }

            // Save or update
            val newAsset = Asset(
                id = asset?.id ?: 0,
                name = name,
                category = category,
                location = location,
                quantity = quantity,
                condition = condition,
                purchaseDate = purchaseDate,
                description = description
            )

            if (asset != null) {
                // Update
                dbHelper.updateAsset(newAsset)
                Toast.makeText(this, "Aset berhasil diupdate", Toast.LENGTH_SHORT).show()
            } else {
                // Insert
                dbHelper.insertAsset(newAsset)
                Toast.makeText(this, "Aset berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            }

            loadAssets()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteDialog(asset: Asset) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Aset")
            .setMessage("Apakah Anda yakin ingin menghapus \"${asset.name}\"?")
            .setPositiveButton("Hapus") { _, _ ->
                dbHelper.deleteAsset(asset.id)
                Toast.makeText(this, "Aset berhasil dihapus", Toast.LENGTH_SHORT).show()
                loadAssets()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showAssetDetail(asset: Asset) {
        val message = """
            Nama: ${asset.name}
            Kategori: ${asset.category}
            Lokasi: ${asset.location}
            Jumlah: ${asset.quantity} Unit
            Kondisi: ${asset.condition}
            Tanggal Beli: ${asset.purchaseDate}
            Deskripsi: ${asset.description.ifEmpty { "-" }}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Detail Aset")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}