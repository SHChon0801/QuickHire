package my.edu.tarc.quickhire.ui.home

data class Job(
    val jobImage: String?=null,
    val jobID: Int?=null,
    val jobName: String?=null,
    val jobDescription: String?=null,
    val jobArea: String?=null,
    val jobSpecialist: String?=null,
    val jobPayRate: Double?=null,
    val jobEmail: String?=null,
    val emailIDApplied: ArrayList<String>? = null,
    val ignoredViewCount: Int?= null
)
