package com.example.goat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AttendanceManager {
    private val students = mutableListOf<Student>()


    fun addStudent(name: String, profile: String) {
        val student = Student(
            name = name,
            gradeAndClass = "", // 또는 필요한 값으로 변경
            profile = profile,
            examGrade = "", // 또는 필요한 값으로 변경
            assignmentGrade = "", // 또는 필요한 값으로 변경
            behavior = "", // 또는 필요한 값으로 변경
            attitude = "", // 또는 필요한 값으로 변경
            specialNote = "", // 또는 필요한 값으로 변경
            status = AttendanceStatus.ATTENDANCE // 또는 필요한 값으로 변경
        )
        students.add(student)
    }


    fun getAllStudents(): List<Student> {
        return students
    }

    fun getStudentByName(name: String): Student? {
        return students.find { it.name == name }
    }

    // 추가한 부분: 출결 상태 업데이트 함수
    fun updateAttendance(studentName: String, newStatus: AttendanceStatus) {
        val student = students.find { it.name == studentName }
        if (student != null) {
            student.status = newStatus
        }
    }
}

