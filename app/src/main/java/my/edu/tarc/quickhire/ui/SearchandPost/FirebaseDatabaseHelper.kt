package my.edu.tarc.quickhire.ui.SearchandPost

import com.google.firebase.database.*
import my.edu.tarc.quickhire.ui.FavouriteJobPlace
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
    fun postOrUpdateFavouriteJobPlace(job: FavouriteJobPlace) {
        if (job.jobArea!!.isEmpty()) {
            return
        }

        val query = mDatabase.reference.child("FavouriteJobPlaces")
            .orderByChild("jobArea")
            .equalTo(job.jobArea)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var foundMatchingJobArea = false

                for (favouriteJobPlaceSnapshot in snapshot.children) {
                    val favouriteJobPlaceId = favouriteJobPlaceSnapshot.key

                    if (favouriteJobPlaceId != null) {
                        val existingFavouriteJobPlace = favouriteJobPlaceSnapshot.getValue(FavouriteJobPlace::class.java)

                        if (existingFavouriteJobPlace != null) {
                            existingFavouriteJobPlace.clickRate = existingFavouriteJobPlace.clickRate!! + 1
                            mDatabase.reference.child("FavouriteJobPlaces").child(favouriteJobPlaceId).setValue(existingFavouriteJobPlace)
                                .addOnSuccessListener {
                                    // Existing data updated successfully
                                }
                                .addOnFailureListener { exception ->
                                    // Failed to update existing data
                                }

                            foundMatchingJobArea = true
                            break
                        }
                    }
                }

                if (!foundMatchingJobArea) {
                    val newFavouriteJobPlace = FavouriteJobPlace(job.jobArea, 1)
                    mDatabase.reference.child("FavouriteJobPlaces").push().setValue(newFavouriteJobPlace)
                        .addOnSuccessListener {
                            // New data inserted successfully
                        }
                        .addOnFailureListener { exception ->
                            // Failed to insert new data
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database read error
            }
        })
    }
    interface OnDataFetchListener {
        fun onDataFetched(jobs: List<Job>)
        fun onCancelled(exception: Exception)
    }
}