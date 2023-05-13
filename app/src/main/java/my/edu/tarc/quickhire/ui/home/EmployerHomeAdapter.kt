package my.edu.tarc.quickhire.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.RecyclerEmployerJobBinding

class EmployerHomeAdapter(private val dataList: List<Job>): RecyclerView.Adapter<EmployerHomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = RecyclerEmployerJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val user = FirebaseAuth.getInstance().currentUser
        val currentItem = dataList[position]
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = currentItem.jobImage.let { storageRef.child(it.toString()) }
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            // Load the image using Glide
            Glide.with(holder.binding.root.context)
                .load(uri)
                .into(holder.binding.employerJobImage)
        }.addOnFailureListener { exception ->
            // Handle any errors
            Log.e(ContentValues.TAG, "Failed to retrieve image download URL: ${exception.message}")
        }
        holder.binding.employerJobImage.contentDescription = currentItem.jobName
        holder.binding.employerJobName.text = currentItem.jobName
        holder.binding.employerJobDescription.text = currentItem.jobDescription
        holder.binding.employerJobPayRate.text = "Pay Rate Per Hour: " + currentItem.jobPayRate.toString()
        if(currentItem.emailIDApplied != null){
            if(currentItem.emailIDApplied.contains(user!!.email)){
                holder.binding.applyButton.isEnabled = false
            }
        }
        holder.itemView.setOnClickListener {
            currentItem.jobID?.let { it1 -> updateJobViews(it1) }
            val bundle = Bundle().apply {
                putString("jobImage", currentItem.jobImage)
                putString("jobName", currentItem.jobName)
                putString("jobDescription", currentItem.jobDescription)
                putString("jobArea", currentItem.jobArea)
                putString("jobSpecialist", currentItem.jobSpecialist)
                putInt("jobID", currentItem.jobID!!)
                currentItem.jobPayRate?.let { it1 -> putDouble("jobPayRate", it1) }
                putStringArrayList("emailIDApplied", currentItem.emailIDApplied)
            }
            holder.itemView.findNavController().navigate(R.id.action_nav_employer_home_to_employerHomeDetailFragment, bundle)
        }

        holder.binding.applyButton.setOnClickListener{
            user!!.email?.let { updateUserJobIDApplied(currentItem.jobID!!, it) }
            Toast.makeText(holder.itemView.context, "Apply successfully", Toast.LENGTH_SHORT).show()
            holder.binding.applyButton.isEnabled = false
        }
    }

    private fun updateUserJobIDApplied(jobID: Int, emailID: String) {
        val jobsRef = FirebaseDatabase.getInstance().getReference("Jobs")

        val query = jobsRef.orderByChild("jobID").equalTo(jobID.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Create a temporary list to hold the updated jobs
                    val updatedJobs = mutableListOf<Job>()

                    for (jobSnapshot in dataSnapshot.children) {
                        // Retrieve and modify the emailIDApplied list
                        val emailIDAppliedType = object : GenericTypeIndicator<ArrayList<String>>() {}
                        val currentEmailList = jobSnapshot.child("emailIDApplied").getValue(emailIDAppliedType)

                        // Check if the currentEmailList is not null
                        if (currentEmailList != null) {
                            // Add the new email to the list if it doesn't exist already
                            if (!currentEmailList.contains(emailID)) {
                                currentEmailList.add(emailID)

                                val jobKey = jobSnapshot.key
                                jobKey?.let {
                                    jobsRef.child(it).child("emailIDApplied").setValue(currentEmailList)
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

                        // Create a Job object with the updated data
                        val updatedJob = jobSnapshot.getValue(Job::class.java)
                        updatedJob?.let {
                            updatedJobs.add(it)
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

    private fun updateJobViews(jobID: Int) {
        val jobsRef = FirebaseDatabase.getInstance().getReference("Jobs")

        val query = jobsRef.orderByChild("jobID").equalTo(jobID.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Create a temporary list to hold the updated jobs
                    val updatedJobs = mutableListOf<Job>()

                    for (jobSnapshot in dataSnapshot.children) {
                        // Retrieve and modify the viewCount
                        val currentViewCount = jobSnapshot.child("viewCount").getValue(Int::class.java)

                        // Check if the currentViewCount is not null
                        if (currentViewCount != null) {
                            // Increment the viewCount
                            val newViewCount = currentViewCount + 1
                            jobSnapshot.child("viewCount").ref.setValue(newViewCount)
                        } else {
                            // If the currentViewCount is null, set the viewCount to the jobView
                            jobSnapshot.child("viewCount").ref.setValue(1)
                        }

                        // Create a Job object with the updated data
                        val updatedJob = jobSnapshot.getValue(Job::class.java)
                        updatedJob?.let {
                            updatedJobs.add(it)
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
    class HomeViewHolder(val binding: RecyclerEmployerJobBinding): RecyclerView.ViewHolder(binding.root)
}