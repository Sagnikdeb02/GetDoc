import com.example.getdoc.data.model.DoctorInfo
import com.example.getdoc.data.model.PatientInfo

data class Appointment(
    var id: String = "", // ✅ Add this field
    var patientId: String = "", // ✅ Ensure patientId exists
    var patientInfo: PatientInfo = PatientInfo(), // ✅ Ensure PatientInfo exists
    var doctorId: String = "",
    var date: String? = null,
    var timeSlot: String? = null,
    var status: String? = "pending",
    var profileImage: String? = null,// ✅ Ensure profileImage exists
    var doctorInfo: DoctorInfo? = null,  // ✅ Added DoctorInfo to store doctor's details
    var reviewId: String? = null,
    var reviewText: String? = null,
    var reviewRating: Int? = null,
    var Timestamp: Long? = null

)

