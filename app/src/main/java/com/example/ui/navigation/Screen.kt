package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object PermissionSetup : Screen("permission_setup")
    object Home : Screen("home")
    object RacetrackDetail : Screen("racetrack_detail")
    object DailySchedule : Screen("daily_schedule")
    object EditFoodSchedule : Screen("edit_food_schedule")
    object MedicineList : Screen("medicine_list")
    object AddMedicine : Screen("add_medicine")
    object EditMedicine : Screen("edit_medicine/{medicineId}") {
        fun createRoute(medicineId: Long) = "edit_medicine/$medicineId"
    }
    object MedicineDetail : Screen("medicine_detail/{medicineId}") {
        fun createRoute(medicineId: Long) = "medicine_detail/$medicineId"
    }
    object History : Screen("history")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
}
