package my.edu.tarc.quickhire

data class Employee (
    val email: String? = null,
    val password: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val role:String? = null,
    val about:String?=null,
    val state:String?=null,
    val currentJob:String?=null,
    val timePrefer:String?=null,
    val education:String?=null,
    val skill:String?=null,
    val profilePic:String?=null,
    val jobIDApplied: ArrayList<Int>? = null

)
