package com.swirlfist.sprited.data.persistence.room.database

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.swirlfist.sprited.data.entity.PalettePresetEntity
import com.swirlfist.sprited.data.persistence.room.converters.Converters
import com.swirlfist.sprited.data.persistence.room.dao.PalettePresetDao

@Database(entities = [PalettePresetEntity::class], version = 1)
@ColumnTypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun palettePresetDao(): PalettePresetDao

    companion object {
        const val NAME = "app-database"
    }
}