package my.edu.tarc.quickhire.ui.searchandPost

import com.google.firebase.database.*
import my.edu.tarc.quickhire.ui.home.Job

class FirebaseDatabaseHelper {
    private lateinit var mReferenceJob: DatabaseReference
    private lateinit var mDatabase: FirebaseDatabase
    private var jobs: ArrayList<Job> = ArrayList()
    val database = FirebaseDatabase.getInstance()

    init {
        mDatabase = FirebaseDatabase.getInstance()
        mReferenceJob = mDatabase.reference.child("Jobs")
    }

    fun readJobsData(listener: OnDataFetchListener) {
        mReferenceJob.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                jobs.clear()

                for (jobSnapshot in snapshot.children) {
                    val job = jobSnapshot.getValue(Job::class.java)
                    job?.let {
                        jobs.add(it)
                    }
                }

                listener.onDataFetched(jobs)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database read error
                listener.onCancelled(error.toException())
            }
        })
    }

    fun pushJob(job: Job): String {
        val newJobRef = mReferenceJob.push()
        newJobRef.setValue(job)
            .addOnSuccessListener {
                // Data inserted successfully
            }
            .addOnFailureListener { exception ->
                // Failed to insert data
            }
        return newJobRef.key ?: ""
    }
    interface OnDataFetchListener {
        fun onDataFetched(jobs: List<Job>)
        fun onCancelled(exception: Exception)
    }
}