package my.edu.tarc.quickhire.ui.home

data class EmployerJob(
    val jobImage: Int?=null,
    val jobID: Int?=null,
    val jobName: String?=null,
    val jobDescription: String?=null,
    val jobArea: String?=null,
    val jobSpecialist: String?=null,
    var jobPayRate: Double?=null
)
