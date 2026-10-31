package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.FoodSchedule
import com.example.data.model.MealLog
import com.example.data.model.Medicine
import com.example.data.model.MedicineLog
import com.example.data.model.TimingInstruction
import com.example.data.model.UserProfile
import com.example.data.model.WaterLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        FoodSchedule::class,
        Medicine::class,
        MealLog::class,
        MedicineLog::class,
        UserProfile::class,
        WaterLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodScheduleDao(): FoodScheduleDao
    abstract fun medicineDao(): MedicineDao
    abstract fun mealLogDao(): MealLogDao
    abstract fun medicineLogDao(): MedicineLogDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun waterLogDao(): WaterLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medimeal_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateDefaultData(getDatabase(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateDefaultData(database: AppDatabase) {
            // Default 10 Meals Schedule
            val defaultMeals = listOf(
                FoodSchedule(1, 1, "Meal 1 – Early Morning", null, "05:00", true, "Hydrate and light snack"),
                FoodSchedule(2, 2, "Meal 2 – Breakfast", null, "07:00", true, "Nutritious breakfast"),
                FoodSchedule(3, 3, "Meal 3 – Mid-Morning", null, "09:00", true, "Fruits or nuts"),
                FoodSchedule(4, 4, "Meal 4 – Pre-Lunch", null, "11:00", true, "Juice, yogurt, or salad"),
                FoodSchedule(5, 5, "Meal 5 – Lunch", null, "13:00", true, "Balanced main meal"),
                FoodSchedule(6, 6, "Meal 6 – Afternoon Snack", null, "15:00", true, "Energy booster / tea"),
                FoodSchedule(7, 7, "Meal 7 – Late Afternoon", null, "17:00", true, "Light protein snack"),
                FoodSchedule(8, 8, "Meal 8 – Dinner", null, "19:00", true, "Healthy dinner"),
                FoodSchedule(9, 9, "Meal 9 – Post-Dinner", null, "21:00", true, "Herbal drink / digestive snack"),
                FoodSchedule(10, 10, "Meal 10 – Night Snack", null, "23:00", true, "Light warm milk or sleep aid")
            )
            database.foodScheduleDao().insertAll(defaultMeals)

            // Default 3 Daily Prescribed Medicines
            val defaultMedicines = listOf(
                Medicine(
                    id = 1,
                    name = "Metformin",
                    category = "Tablet",
                    dosageAmount = 500.0,
                    dosageUnit = "mg",
                    timingInstruction = TimingInstruction.BEFORE_FOOD,
                    associatedMealNumber = 2, // Before Breakfast (Meal 2)
                    mealOffsetMinutes = -30, // 30 min before
                    timesPerDay = 1,
                    reminderTimesString = "06:30",
                    instructions = "Take 30 minutes before breakfast with a full glass of water",
                    isActive = true,
                    colorHex = "#75172B"
                ),
                Medicine(
                    id = 2,
                    name = "Multivitamin Complex",
                    category = "Capsule",
                    dosageAmount = 1.0,
                    dosageUnit = "capsule",
                    timingInstruction = TimingInstruction.AFTER_FOOD,
                    associatedMealNumber = 5, // After Lunch (Meal 5)
                    mealOffsetMinutes = 30, // 30 min after
                    timesPerDay = 1,
                    reminderTimesString = "13:30",
                    instructions = "Take 30 minutes after lunch",
                    isActive = true,
                    colorHex = "#D63850"
                ),
                Medicine(
                    id = 3,
                    name = "Calcium & Vitamin D3",
                    category = "Tablet",
                    dosageAmount = 600.0,
                    dosageUnit = "mg",
                    timingInstruction = TimingInstruction.WITH_FOOD,
                    associatedMealNumber = 8, // With Dinner (Meal 8)
                    mealOffsetMinutes = 0, // With food
                    timesPerDay = 1,
                    reminderTimesString = "19:00",
                    instructions = "Take right with evening meal",
                    isActive = true,
                    colorHex = "#570D1D"
                )
            )
            database.medicineDao().insertAll(defaultMedicines)

            // Default User Profile
            database.userProfileDao().insertOrUpdate(
                UserProfile(
                    id = 1,
                    name = "Alex Vance",
                    email = "alex@example.com",
                    targetMealsPerDay = 10,
                    targetMedsPerDay = 3,
                    languageCode = "en",
                    isDarkMode = null,
                    isVibrationEnabled = true,
                    snoozeMinutes = 10,
                    isWaterReminderEnabled = true,
                    waterDailyGoalGlasses = 8
                )
            )
        }
    }
}
