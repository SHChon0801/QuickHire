package my.edu.tarc.quickhire.ui.home

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentOrganizationHomeDetailBinding
import java.util.UUID

class OrganizationHomeDetailFragment : Fragment() {

    private var _binding: FragmentOrganizationHomeDetailBinding?= null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null
    private lateinit var imageFileName : String
    private var uploadImageClicked: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOrganizationHomeDetailBinding.inflate(inflater, container, false)

        //    Retrieve data from bundle
        val jobId = arguments?.getInt("jobId")
        val jobImage = arguments?.getString("jobImage")
        val jobName = arguments?.getString("jobName")
        val jobDescription = arguments?.getString("jobDescription")
        val jobArea = arguments?.getString("jobArea")
        val jobSpecialist = arguments?.getString("jobSpecialist")
        val jobPayRate = arguments?.getDouble("jobPayRate")

        //Retrieve StringArray from strings.xml
        val areaOptions = resources.getStringArray(R.array.areaoptions)
        val jobOptions = resources.getStringArray(R.array.joboptions)
        //Retrieve Spinner Index
        val areaIndex = areaOptions.indexOf(jobArea)
        val jobIndex = jobOptions.indexOf(jobSpecialist)

        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = jobImage.let { storageRef.child(it.toString()) }
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            // Load the image using Glide
            Glide.with(binding.root.context)
                .load(uri)
                .into(binding.organizationImageDetail)
        }.addOnFailureListener { exception ->
            // Handle any errors
            Log.e(ContentValues.TAG, "Failed to retrieve image download URL: ${exception.message}")
        }
        binding.organizationImageDetail.contentDescription = jobName
        binding.organizationNameDetail.text = Editable.Factory.getInstance().newEditable(jobName)
        binding.organizationDescriptionDetail.text = Editable.Factory.getInstance().newEditable(jobDescription)
        binding.organizationAreaSpinnerDetail.setSelection(areaIndex)
        binding.organizationSpecialistSpinnerDetail.setSelection(jobIndex)
        binding.organizationPayRateDetail.text = Editable.Factory.getInstance().newEditable(jobPayRate.toString())
        binding.organizationBackBtnDetail.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.organizationUploadImageBtnDetail.setOnClickListener {
            pickImageGallery()
            uploadImageClicked = true
        }

        binding.organizationDeleteBtnDetail.setOnClickListener {
            if (jobId != null) {
                deleteJob(jobId)
            }
        }

        binding.btnPostJob.setOnClickListener {
            if (jobId != null) {
                updateUserJob(jobId, uploadImageClicked)
            }
        }

        return binding.root
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }
    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            selectedImageUri = data!!.data
            selectedImageUri?.let { uri ->
                // Generate a unique image filename
                imageFileName = UUID.randomUUID().toString()
                // Call the function to upload the image to Firebase Storage and create the job
                binding.organizationImageDetail.setImageURI(uri)
                uploadImageToStorage(uri)
            }
        } else {
            Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToStorage(imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/$imageFileName.jpg")

        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Upload Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Fail to upload", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteJob(jobID: Int) {
        val jobsRef = FirebaseDatabase.getInstance().getReference("Jobs")

        val query = jobsRef.orderByChild("jobID").equalTo(jobID.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (jobSnapshot in dataSnapshot.children) {
                        val snapshotJobID = jobSnapshot.child("jobID").getValue(Int::class.java)
                        if (snapshotJobID == jobID) {
                            jobSnapshot.ref.removeValue()
                                .addOnSuccessListener {
                                    // Deletion successful
                                    println("Deleted job with jobID: $jobID")
                                }
                                .addOnFailureListener { exception ->
                                    // Deletion failed
                                    println("Failed to delete job with jobID: $jobID. Error: $exception")
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

    private fun validateInputs(): Boolean {
        val jobName = binding.organizationNameDetail.text.toString().trim()
        val jobDescription = binding.organizationDescriptionDetail.text.toString().trim()
        val jobSpecialist = binding.organizationSpecialistSpinnerDetail.selectedItem.toString()
        val jobArea = binding.organizationAreaSpinnerDetail.selectedItem.toString()
        val jobPayRate = binding.organizationPayRateDetail.text.toString().trim()

        if (jobName.isEmpty()) {
            binding.organizationNameDetail.error = "Job name is required"
            binding.organizationNameDetail.requestFocus()
            return false
        }

        if (jobDescription.isEmpty()) {
            binding.organizationDescriptionDetail.error = "Job description is required"
            binding.organizationDescriptionDetail.requestFocus()
            return false
        }

        if (jobSpecialist.isEmpty()) {
            Toast.makeText(requireContext(), "Please Select a Specialist", Toast.LENGTH_SHORT)
                .show()
            binding.organizationSpecialistSpinnerDetail.requestFocus()
            return false
        }
        if (jobArea.isEmpty()) {
            Toast.makeText(requireContext(), "Please Select a Job Area", Toast.LENGTH_SHORT).show()
            binding.organizationAreaSpinnerDetail.requestFocus()
            return false
        }
        if (jobPayRate.isEmpty()) {
            binding.organizationPayRateDetail.error = "Job pay rate is required"
            binding.organizationPayRateDetail.requestFocus()
            return false
        }

        return true
    }
    private fun updateUserJob(jobID: Int, uploadImageClicked: Boolean) {
        val jobsRef = FirebaseDatabase.getInstance().getReference("Jobs")

        val query = jobsRef.orderByChild("jobID").equalTo(jobID.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && validateInputs()) {
                    // Create a temporary list to hold the updated jobs
                    val updatedJobs = mutableListOf<Job>()

                    // Loop through the jobs in the dataSnapshot
                    for (jobSnapshot in dataSnapshot.children) {
                        // Retrieve and modify the emailIDApplied list
                        val emailIDAppliedType = object : GenericTypeIndicator<ArrayList<String>>() {}
                        val currentEmailList = jobSnapshot.child("emailIDApplied").getValue(emailIDAppliedType)
                        val ignoredJobViewCount = arguments?.getInt("jobViewCount")

                        val snapshotJobID = jobSnapshot.child("jobID").getValue(Int::class.java)
                        if (snapshotJobID == jobID) {
                            // Retrieve and modify the job list
                            val jobImage: String? = if (uploadImageClicked){
                                "images/$imageFileName.jpg"
                            } else {
                                arguments?.getString("jobImage")
                            }

                            val jobName = binding.organizationNameDetail.text.toString().trim()
                            val jobDescription = binding.organizationDescriptionDetail.text.toString().trim()
                            val jobSpecialist = binding.organizationSpecialistSpinnerDetail.selectedItem.toString()
                            val jobArea = binding.organizationAreaSpinnerDetail.selectedItem.toString()
                            val jobPayRate = binding.organizationPayRateDetail.text.toString().trim().toDouble()
                            val user = FirebaseAuth.getInstance().currentUser

                            val editedJobs = Job(
                                jobImage = jobImage,
                                jobID = jobID,
                                jobName = jobName,
                                jobDescription = jobDescription,
                                jobArea = jobArea,
                                jobSpecialist = jobSpecialist,
                                jobPayRate = jobPayRate,
                                jobEmail = user!!.email,
                                emailIDApplied = currentEmailList,
                                ignoredViewCount = ignoredJobViewCount
                            )

                            val jobKey = jobSnapshot.key
                            jobKey?.let {
                                jobsRef.child(it)
                                    .setValue(editedJobs)
                                    .addOnSuccessListener {
                                        // Update successful
                                        println("Edited Job list for job with jobID: $jobID")
                                    }
                                    .addOnFailureListener { exception ->
                                        // Update failed
                                        println("Failed to edit job list for job with jobID: $jobID. Error: $exception")
                                    }
                            }

                            // Create a Job object with the updated data
                            val updatedJob = jobSnapshot.getValue(Job::class.java)
                            updatedJob?.let {
                                updatedJobs.add(it)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}