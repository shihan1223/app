package com.example.ui.components

import com.example.data.model.TimingInstruction

object LanguageSupport {
    fun getMedicineInstruction(instruction: TimingInstruction, isBangla: Boolean): String {
        return if (isBangla) instruction.labelBn else instruction.labelEn
    }

    fun getActionText(action: String, isBangla: Boolean): String {
        return when (action) {
            "mark_taken" -> if (isBangla) "গ্রহণ করা হয়েছে" else "Mark Taken"
            "mark_eaten" -> if (isBangla) "খাবার সম্পন্ন" else "Mark Eaten"
            "snooze" -> if (isBangla) "স্নুজ করুন" else "Snooze"
            "skip" -> if (isBangla) "বাদ দিন" else "Skip"
            "add_medicine" -> if (isBangla) "ওষুধ যোগ করুন" else "Add Medicine"
            "add_food" -> if (isBangla) "খাবার সময় পরিবর্তন" else "Adjust Schedule"
            "next_upcoming" -> if (isBangla) "পরবর্তী ইভেন্ট" else "Next Upcoming"
            "daily_summary" -> if (isBangla) "দৈনিক সারসংক্ষেপ" else "Daily Summary"
            "completed" -> if (isBangla) "সম্পন্ন" else "Completed"
            "pending" -> if (isBangla) "অপেক্ষমান" else "Pending"
            "racetrack" -> if (isBangla) "রেসট্র্যাক টাইমলাইন" else "Racetrack Timeline"
            else -> action
        }
    }
}
