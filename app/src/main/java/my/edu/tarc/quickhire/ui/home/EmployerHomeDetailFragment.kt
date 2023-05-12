package my.edu.tarc.quickhire.ui.home

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import my.edu.tarc.quickhire.databinding.FragmentEmployerHomeDetailBinding

class EmployerHomeDetailFragment : Fragment() {

    private var _binding: FragmentEmployerHomeDetailBinding?= null
    private val binding get() = _binding!!

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEmployerHomeDetailBinding.inflate(inflater, container, false)
        val user = FirebaseAuth.getInstance().currentUser
        //    Retrieve data from bundle
        val jobImage = arguments?.getString("jobImage")
        val jobName = arguments?.getString("jobName")
        val jobDescription = arguments?.getString("jobDescription")
        val jobArea = arguments?.getString("jobArea")
        val jobSpecialist = arguments?.getString("jobSpecialist")
        val jobPayRate = arguments?.getDouble("jobPayRate")
        val emailIDApplied = arguments?.getStringArrayList("emailIDApplied")
        val jobID = arguments?.getInt("jobID")

        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = jobImage.let { storageRef.child(it.toString()) }
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            // Load the image using Glide
            Glide.with(binding.root.context)
                .load(uri)
                .into(binding.employerJobImageDetail)
        }.addOnFailureListener { exception ->
            // Handle any errors
            Log.e(ContentValues.TAG, "Failed to retrieve image download URL: ${exception.message}")
        }
        binding.employerJobImageDetail.contentDescription = jobName
        binding.employerJobNameDetail.text = jobName
        binding.employerJobDescriptionDetail.text = jobDescription
        binding.employerJobAreaDetail.text = jobArea
        binding.employerJobSpecialistDetail.text = jobSpecialist
        binding.employerJobPayRateDetail.text = jobPayRate.toString()
        if(emailIDApplied != null){
            if(emailIDApplied.contains(user!!.email)){
                binding.detailApplyButton.isEnabled = false
            }
        }

        binding.employerBackBtnDetail.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.detailApplyButton.setOnClickListener(){
            user!!.email?.let { updateUserJobIDApplied(jobID!!, it) }
            Toast.makeText(context, "Apply successfully", Toast.LENGTH_SHORT).show()
            binding.detailApplyButton.isEnabled = false


        }
        return  binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
}