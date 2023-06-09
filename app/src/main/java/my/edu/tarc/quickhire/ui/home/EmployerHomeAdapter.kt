package my.edu.tarc.quickhire.ui.home

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.RecyclerEmployerJobBinding
import my.edu.tarc.quickhire.ui.notifications.NotificationData
import java.util.*
import kotlin.collections.ArrayList

class EmployerHomeAdapter(private val context: Context, private val dataList: List<Job>): RecyclerView.Adapter<EmployerHomeAdapter.HomeViewHolder>() {

    //Notification
    private lateinit var database: DatabaseReference

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
        holder.binding.employerJobPayRate.text = context.getString(R.string.pay_rate_per_hour, currentItem.jobPayRate.toString())

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

            //Apply Notification
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1 // months are zero-based, so add 1
            val date = calendar.get(Calendar.DATE)

            //Send email
            //val UID = "aaa@gmail-com"
            val encodeUID=currentItem.jobEmail.toString()

            val UID = encodeUID.replace(".","-")

            val user = FirebaseAuth.getInstance().currentUser
            val userEmail = user?.email

            val encodedEmail = userEmail?.replace(".","-")

            val n_title ="New job hire request"
            val n_des ="A new job hire request from job seeker is sent to you. Please do the response as fast as possible."
            val n_time = "$date-$month-$year"
            val n_type = "second_type"
            val n_UID =encodedEmail
            val n_image ="https://firebasestorage.googleapis.com/v0/b/quickhire-409e0.appspot.com/o/images%2Fjob_want.jpg?alt=media&token=3144914c-33a8-4698-b25e-a62d7d191b42"

            // Initialize the database reference
            database = FirebaseDatabase.getInstance()
                .getReference("Organizations").child(UID).child("notification")

            // Retrieve the employee's list of notifications from the database
            database.get().addOnSuccessListener { snapshot ->
                val notificationsEmp = snapshot.getValue(object :
                    GenericTypeIndicator<java.util.ArrayList<NotificationData>>() {})

                // Create a new notification and add it to the list
                val notificationEmp =
                    NotificationData(n_title, n_des, n_time, n_type, n_UID, n_image)
                notificationsEmp?.add(notificationEmp)

                // Update the employee's list of notifications in the database
                database.setValue(notificationsEmp).addOnSuccessListener {
//                    Toast.makeText(requireContext(), "Notification added", Toast.LENGTH_SHORT)
//                        .show()
                }.addOnFailureListener {
//                    Toast.makeText(
//                        requireContext(),
//                        "Failed to add notification",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            }.addOnFailureListener {
//                Toast.makeText(
//                    requireContext(),
//                    "Failed to retrieve notifications",
//                    Toast.LENGTH_SHORT
//                ).show()
            }


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
                        val currentViewCount = jobSnapshot.child("ignoredViewCount").getValue(Int::class.java)

                        // Check if the currentViewCount is not null
                        if (currentViewCount != null) {
                            // Increment the viewCount
                            val newViewCount = currentViewCount + 1
                            jobSnapshot.child("ignoredViewCount").ref.setValue(newViewCount)
                        } else {
                            // If the currentViewCount is null, set the viewCount to the jobView
                            jobSnapshot.child("ignoredViewCount").ref.setValue(1)
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