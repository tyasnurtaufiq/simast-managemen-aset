// data/database/DatabaseHelper.kt
package com.example.projekmanajemenaset.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.projekmanajemenaset.data.model.Asset
import com.example.projekmanajemenaset.data.model.User

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "AssetKampus.db"
        private const val DATABASE_VERSION = 1

        // Table Users
        private const val TABLE_USERS = "users"
        private const val COL_USER_ID = "id"
        private const val COL_USERNAME = "username"
        private const val COL_PASSWORD = "password"
        private const val COL_FULL_NAME = "full_name"

        // Table Assets
        private const val TABLE_ASSETS = "assets"
        private const val COL_ASSET_ID = "id"
        private const val COL_ASSET_NAME = "name"
        private const val COL_CATEGORY = "category"
        private const val COL_LOCATION = "location"
        private const val COL_QUANTITY = "quantity"
        private const val COL_CONDITION = "condition"
        private const val COL_PURCHASE_DATE = "purchase_date"
        private const val COL_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create Users Table
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT UNIQUE NOT NULL,
                $COL_PASSWORD TEXT NOT NULL,
                $COL_FULL_NAME TEXT NOT NULL
            )
        """.trimIndent()

        // Create Assets Table
        val createAssetsTable = """
            CREATE TABLE $TABLE_ASSETS (
                $COL_ASSET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ASSET_NAME TEXT NOT NULL,
                $COL_CATEGORY TEXT NOT NULL,
                $COL_LOCATION TEXT NOT NULL,
                $COL_QUANTITY INTEGER NOT NULL,
                $COL_CONDITION TEXT NOT NULL,
                $COL_PURCHASE_DATE TEXT NOT NULL,
                $COL_DESCRIPTION TEXT
            )
        """.trimIndent()

        db?.execSQL(createUsersTable)
        db?.execSQL(createAssetsTable)

        // Insert default admin user
        insertDefaultUser(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ASSETS")
        onCreate(db)
    }

    private fun insertDefaultUser(db: SQLiteDatabase?) {
        val values = ContentValues().apply {
            put(COL_USERNAME, "admin")
            put(COL_PASSWORD, "admin123")
            put(COL_FULL_NAME, "Administrator")
        }
        db?.insert(TABLE_USERS, null, values)
    }

    // User Operations
    fun loginUser(username: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COL_USERNAME = ? AND $COL_PASSWORD = ?",
            arrayOf(username, password),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)),
                fullName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FULL_NAME))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    // Asset CRUD Operations
    fun insertAsset(asset: Asset): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ASSET_NAME, asset.name)
            put(COL_CATEGORY, asset.category)
            put(COL_LOCATION, asset.location)
            put(COL_QUANTITY, asset.quantity)
            put(COL_CONDITION, asset.condition)
            put(COL_PURCHASE_DATE, asset.purchaseDate)
            put(COL_DESCRIPTION, asset.description)
        }
        return db.insert(TABLE_ASSETS, null, values)
    }

    fun getAllAssets(): List<Asset> {
        val assets = mutableListOf<Asset>()
        val db = readableDatabase
        val cursor = db.query(TABLE_ASSETS, null, null, null, null, null, "$COL_ASSET_ID DESC")

        if (cursor.moveToFirst()) {
            do {
                val asset = Asset(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ASSET_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COL_ASSET_NAME)),
                    category = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                    location = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                    quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY)),
                    condition = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONDITION)),
                    purchaseDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_PURCHASE_DATE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION))
                )
                assets.add(asset)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return assets
    }

    fun updateAsset(asset: Asset): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ASSET_NAME, asset.name)
            put(COL_CATEGORY, asset.category)
            put(COL_LOCATION, asset.location)
            put(COL_QUANTITY, asset.quantity)
            put(COL_CONDITION, asset.condition)
            put(COL_PURCHASE_DATE, asset.purchaseDate)
            put(COL_DESCRIPTION, asset.description)
        }
        return db.update(TABLE_ASSETS, values, "$COL_ASSET_ID = ?", arrayOf(asset.id.toString()))
    }

    fun deleteAsset(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_ASSETS, "$COL_ASSET_ID = ?", arrayOf(id.toString()))
    }

    fun searchAssets(query: String): List<Asset> {
        val assets = mutableListOf<Asset>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_ASSETS,
            null,
            "$COL_ASSET_NAME LIKE ? OR $COL_CATEGORY LIKE ? OR $COL_LOCATION LIKE ?",
            arrayOf("%$query%", "%$query%", "%$query%"),
            null, null, "$COL_ASSET_ID DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val asset = Asset(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ASSET_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COL_ASSET_NAME)),
                    category = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                    location = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                    quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY)),
                    condition = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONDITION)),
                    purchaseDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_PURCHASE_DATE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION))
                )
                assets.add(asset)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return assets
    }
}