package com.example.goat

data class GradeAndClass(val grade: String, val className: String)
data class Student(
    val name: String,
    val gradeAndClass: String = "",
    var profile: String = "",
    var examGrade: String = "",
    var assignmentGrade: String = "",
    var behavior: String = "",
    var attitude: String = "",
    var specialNote: String = "",
    var status: AttendanceStatus = AttendanceStatus.ATTENDANCE
)
enum class AttendanceStatus {
    ATTENDANCE, LATE, ABSENCE
}