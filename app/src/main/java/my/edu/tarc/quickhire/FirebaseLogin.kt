//package my.edu.tarc.quickhire
//
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//
//class FirebaseLogin {
//
//    private lateinit var mReferenceEmp: DatabaseReference
//    private lateinit var mReferenceOrg: DatabaseReference
//    private lateinit var mDatabase: FirebaseDatabase
//    val database = FirebaseDatabase.getInstance()
//
//    init {
//        mDatabase = FirebaseDatabase.getInstance()
//        mReferenceEmp = mDatabase.reference.child("Employees")
//        mReferenceEmp = mDatabase.reference.child("Organizations")
//    }
//
//
//    fun readRole(listener: OnDataFetchListener) {
//       mReferenceEmp.addValueEventListener(object : ValueEventListener{
//           override fun onDataChange(employeesSnapshot: DataSnapshot) {
//               if (employeesSnapshot.exists()){
//
//               }
//           }
//
//       })
//    }
//
//
//
//    fun readJobsData(listener: OnDataFetchListener) {
//        mReferenceJob.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                employerJobs.clear()
//
//                for (jobSnapshot in snapshot.children) {
//                    val job = jobSnapshot.getValue(EmployerJob::class.java)
//                    job?.let {
//                        employerJobs.add(it)
//                    }
//                }
//
//                listener.onDataFetched(employerJobs)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle database read error
//                listener.onCancelled(error.toException())
//            }
//        })
//    }
//
//}