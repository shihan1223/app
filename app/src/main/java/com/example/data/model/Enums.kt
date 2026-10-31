package com.example.data.model

enum class TimingInstruction(val labelEn: String, val labelBn: String) {
    BEFORE_FOOD("Before Food", "খাবারের আগে"),
    AFTER_FOOD("After Food", "খাবারের পরে"),
    WITH_FOOD("With Food", "খাবারের সাথে"),
    EMPTY_STOMACH("Empty Stomach", "খালি পেটে"),
    ANYTIME("Anytime / No Food Restriction", "যেকোনো সময় / কোনো খাবারের বাধ্যবাধকতা নেই")
}

enum class CompletionStatus {
    PENDING,
    COMPLETED, // or TAKEN for meds
    SNOOZED,
    SKIPPED,
    MISSED
}
