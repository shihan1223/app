package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarm.AlarmScheduler
import com.example.data.datastore.UserPreferences
import com.example.data.local.AppDatabase
import com.example.data.model.CompletionStatus
import com.example.data.model.DailyTimelineItem
import com.example.data.model.FoodSchedule
import com.example.data.model.MealLog
import com.example.data.model.Medicine
import com.example.data.model.MedicineLog
import com.example.data.model.RacetrackCheckpoint
import com.example.data.model.TimingInstruction
import com.example.data.model.UserProfile
import com.example.data.model.WaterLog
import com.example.data.repository.MediMealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = MediMealRepository(database)
    private val userPrefs = UserPreferences(application)

    val selectedDate = MutableStateFlow(LocalDate.now().toString())

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isBangla: StateFlow<Boolean> = userProfile
        .map { it?.languageCode == "bn" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val foodSchedules: StateFlow<List<FoodSchedule>> = repository.allFoodSchedules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeMedicines: StateFlow<List<Medicine>> = repository.activeMedicines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val racetrackCheckpoints: StateFlow<List<RacetrackCheckpoint>> = selectedDate
        .flatMapLatest { date -> repository.getRacetrackCheckpointsFlow(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyTimeline: StateFlow<List<DailyTimelineItem>> = selectedDate
        .flatMapLatest { date -> repository.getDailyTimelineFlow(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayMealLogs: StateFlow<List<MealLog>> = selectedDate
        .flatMapLatest { date -> repository.getMealLogsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayMedicineLogs: StateFlow<List<MedicineLog>> = selectedDate
        .flatMapLatest { date -> repository.getMedicineLogsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val waterLog: StateFlow<WaterLog?> = selectedDate
        .flatMapLatest { date -> repository.getWaterLogForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allMealHistory: StateFlow<List<MealLog>> = repository.allMealHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMedicineHistory: StateFlow<List<MedicineLog>> = repository.allMedicineHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isOnboardingDone: StateFlow<Boolean> = userPrefs.isOnboardingCompleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
            AlarmScheduler.rescheduleAll(getApplication(), repository)
        }
    }

    fun setDate(date: String) {
        selectedDate.value = date
    }

    fun markMealStatus(
        mealNumber: Int,
        mealName: String,
        scheduledTime: String,
        status: CompletionStatus,
        notes: String? = null
    ) {
        viewModelScope.launch {
            repository.logMealAction(
                date = selectedDate.value,
                mealNumber = mealNumber,
                mealName = mealName,
                scheduledTime = scheduledTime,
                status = status,
                notes = notes
            )
        }
    }

    fun markMedicineStatus(
        medicineId: Long,
        medicineName: String,
        scheduledTime: String,
        dosageInfo: String,
        timingInstruction: TimingInstruction,
        associatedMealNumber: Int?,
        status: CompletionStatus,
        notes: String? = null
    ) {
        viewModelScope.launch {
            repository.logMedicineAction(
                date = selectedDate.value,
                medicineId = medicineId,
                medicineName = medicineName,
                scheduledTime = scheduledTime,
                dosageInfo = dosageInfo,
                timingInstruction = timingInstruction,
                associatedMealNumber = associatedMealNumber,
                status = status,
                notes = notes
            )
        }
    }

    fun updateWaterIntake(delta: Int) {
        viewModelScope.launch {
            repository.updateWaterIntake(selectedDate.value, delta)
        }
    }

    fun regenerateFoodSchedule(startTime: String, intervalMinutes: Int, totalMeals: Int) {
        viewModelScope.launch {
            repository.regenerateFoodSchedule(startTime, intervalMinutes, totalMeals)
            AlarmScheduler.rescheduleAll(getApplication(), repository)
        }
    }

    fun updateFoodSchedule(schedule: FoodSchedule) {
        viewModelScope.launch {
            repository.updateFoodSchedule(schedule)
            AlarmScheduler.scheduleMealAlarm(getApplication(), schedule)
        }
    }

    fun addMedicine(medicine: Medicine) {
        viewModelScope.launch {
            val id = repository.addMedicine(medicine)
            val newMed = medicine.copy(id = id)
            AlarmScheduler.rescheduleAll(getApplication(), repository)
        }
    }

    fun updateMedicine(medicine: Medicine) {
        viewModelScope.launch {
            repository.updateMedicine(medicine)
            AlarmScheduler.rescheduleAll(getApplication(), repository)
        }
    }

    fun deleteMedicine(medicine: Medicine) {
        viewModelScope.launch {
            repository.deleteMedicine(medicine)
            AlarmScheduler.rescheduleAll(getApplication(), repository)
        }
    }

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.updateUserProfile(profile)
        }
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            val current = userProfile.value ?: return@launch
            val nextLang = if (current.languageCode == "bn") "en" else "bn"
            repository.updateUserProfile(current.copy(languageCode = nextLang))
            userPrefs.setAppLanguage(nextLang)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPrefs.setOnboardingCompleted(true)
        }
    }
}
