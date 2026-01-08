// ui/asset/AssetAdapter.kt
package com.example.projekmanajemenaset.ui.asset

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmanajemenaset.R
import com.example.projekmanajemenaset.data.model.Asset

class AssetAdapter(
    private var assets: List<Asset>,
    private val onEditClick: (Asset) -> Unit,
    private val onDeleteClick: (Asset) -> Unit,
    private val onItemClick: (Asset) -> Unit
) : RecyclerView.Adapter<AssetAdapter.AssetViewHolder>() {

    inner class AssetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAssetName: TextView = view.findViewById(R.id.tvAssetName)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvLocation: TextView = view.findViewById(R.id.tvLocation)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        val tvCondition: TextView = view.findViewById(R.id.tvCondition)
        val tvPurchaseDate: TextView = view.findViewById(R.id.tvPurchaseDate)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_asset, parent, false)
        return AssetViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val asset = assets[position]

        holder.tvAssetName.text = asset.name
        holder.tvCategory.text = asset.category
        holder.tvLocation.text = asset.location
        holder.tvQuantity.text = "${asset.quantity} Unit"
        holder.tvCondition.text = asset.condition
        holder.tvPurchaseDate.text = asset.purchaseDate

        // Set condition color
        when (asset.condition) {
            "Baik" -> holder.tvCondition.setTextColor(Color.parseColor("#4CAF50"))
            "Rusak Ringan" -> holder.tvCondition.setTextColor(Color.parseColor("#FF9800"))
            "Rusak Berat" -> holder.tvCondition.setTextColor(Color.parseColor("#F44336"))
        }

        // Click listeners
        holder.itemView.setOnClickListener { onItemClick(asset) }
        holder.btnEdit.setOnClickListener { onEditClick(asset) }
        holder.btnDelete.setOnClickListener { onDeleteClick(asset) }
    }

    override fun getItemCount(): Int = assets.size

    fun updateData(newAssets: List<Asset>) {
        assets = newAssets
        notifyDataSetChanged()
    }
}