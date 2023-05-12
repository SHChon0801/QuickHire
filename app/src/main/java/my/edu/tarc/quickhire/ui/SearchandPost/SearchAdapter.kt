package my.edu.tarc.quickhire.ui.SearchandPost

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.ui.home.Job

class SearchAdapter(private val jobs: MutableList<Job>) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private val stringBuilder = StringBuilder()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_employer_job, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val job = jobs[position]
        holder.bind(job)
    }

    override fun getItemCount(): Int {
        return jobs.size
    }

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val user = FirebaseAuth.getInstance().currentUser
        private val applyButton: Button = itemView.findViewById(R.id.applyButton)
        private val jobImage: ImageView = itemView.findViewById(R.id.employerJobImage)
        private val jobNameTextView: TextView = itemView.findViewById(R.id.employerJobName)
        private val jobDescriptionTextView: TextView =
            itemView.findViewById(R.id.employerJobDescription)
        private val jobPayRate: TextView = itemView.findViewById(R.id.employerJobPayRate)

        init {
            itemView.setOnClickListener(this)
            applyButton.setOnClickListener(this)
        }

        fun bind(job: Job) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef =
                job.jobImage?.let { storageRef.child(it) } // Assuming job.jobImage contains the path to the image in Firebase Storage

            // Fetch the download URL of the image
            imageRef!!.downloadUrl.addOnSuccessListener { uri ->
                // Load the image using Glide
                Glide.with(jobImage.context)
                    .load(uri)
                    .into(jobImage)
            }.addOnFailureListener { exception ->
                // Handle any errors
                Log.e(TAG, "Failed to retrieve image download URL: ${exception.message}")
            }
            jobNameTextView.text = "Job Name : ${job.jobName} "
            jobDescriptionTextView.text = "Job Description :\n ${job.jobDescription} "
            jobPayRate.text = "Pay Rate Per Hours : ${job.jobPayRate} "
            if(job.emailIDApplied != null){
                if(job.emailIDApplied!!.contains(user!!.email)){
                    applyButton.isEnabled = false
                }
            }
        }

        override fun onClick(v: View) {
            if (v == applyButton) {
                val job = jobs[adapterPosition] // Get the clicked job at the current position
                user!!.email?.let { updateUserJobIDApplied(job.jobID!!, it) }
                Toast.makeText(v.context, "Apply successfully", Toast.LENGTH_SHORT).show()


            } else {
                val job = jobs[adapterPosition] // Get the clicked job at the current position

                val bundle = Bundle().apply {
                    putString("jobImage", job.jobImage)
                    putString("jobName", job.jobName)
                    putString("jobDescription", job.jobDescription)
                    putString("jobArea", job.jobArea)
                    putString("jobSpecialist", job.jobSpecialist)
                    putInt("jobID",job.jobID!!)
                    job.jobPayRate?.let { putDouble("jobPayRate", it) }
                    putStringArrayList("emailIDApplied", job.emailIDApplied)
                }
                v.findNavController().navigate(R.id.action_nav_search_to_nav_employer_home_detail, bundle)
            }
        }
    }

    fun updateUserJobIDApplied(jobID: Int, emailID: String) {
        val jobsRef = FirebaseDatabase.getInstance().getReference("Jobs")

        val query = jobsRef.orderByChild("jobID").equalTo(jobID.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Loop through the jobs in the adapter
                    for (i in jobs.indices) {
                        val job = jobs[i]

                        // Find the matching job in the dataSnapshot
                        for (jobSnapshot in dataSnapshot.children) {
                            val snapshotJobID = jobSnapshot.child("jobID").getValue(Int::class.java)
                            if (snapshotJobID == jobID && snapshotJobID == job.jobID) {
                                // Retrieve and modify the emailIDApplied list
                                val emailIDAppliedType = object : GenericTypeIndicator<ArrayList<String>>() {}
                                val currentEmailList =
                                    jobSnapshot.child("emailIDApplied").getValue(emailIDAppliedType)

                                // Check if the currentEmailList is not null
                                if (currentEmailList != null) {
                                    // Add the new email to the list if it doesn't exist already
                                    if (!currentEmailList.contains(emailID)) {
                                        currentEmailList.add(emailID)

                                        val jobKey = jobSnapshot.key
                                        jobKey?.let {
                                            jobsRef.child(it).child("emailIDApplied")
                                                .setValue(currentEmailList)
                                                .addOnSuccessListener {
                                                    // Update successful
                                                    println("Email added to emailIDApplied list for job with jobID: $jobID")
                                                }
                                                .addOnFailureListener { exception ->
                                                    // Update failed
                                                    println("Failed to add email to emailIDApplied list for job with jobID: $jobID. Error: $exception")
                                                }
                                        }
                                    }
                                } else {
                                    // If the currentEmailList is null, create a new list with the email
                                    val newEmailList = ArrayList<String>()
                                    newEmailList.add(emailID)
                                    jobSnapshot.child("emailIDApplied").ref.setValue(newEmailList)
                                }

                                // Update the job in the adapter
                                jobs[i] = jobSnapshot.getValue(Job::class.java)!!

                                // Notify the adapter that the dataset has changed for the specific item
                                notifyItemChanged(i)

                                break
                            }
                        }
                    }
                } else {
                    println("No matching jobs found for jobID: $jobID")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled event
            }
        })
    }

}




