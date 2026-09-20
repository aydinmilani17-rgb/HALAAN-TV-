package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [VideoEntity::class, CommentEntity::class, UserEntity::class], version = 4, exportSchema = false)
abstract class HalaanDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
    abstract fun commentDao(): CommentDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: HalaanDatabase? = null

        fun getDatabase(context: Context): HalaanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HalaanDatabase::class.java,
                    "halaan_tv.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
