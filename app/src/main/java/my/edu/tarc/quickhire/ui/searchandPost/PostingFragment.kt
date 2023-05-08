package my.edu.tarc.quickhire.ui.searchandPost

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import my.edu.tarc.quickhire.databinding.FragmentPostingBinding
import my.edu.tarc.quickhire.ui.home.EmployerJob
import java.util.*

class PostingFragment : Fragment() {
    private lateinit var binding: FragmentPostingBinding
    private val databaseHelper = FirebaseDatabaseHelper()
    private lateinit var imageFileName : String
    private lateinit var myJobName: EditText
    private lateinit var myJobDescription: EditText
    private lateinit var myJobSpecialist: Spinner
    private lateinit var myJobArea: Spinner
    private lateinit var myJobPayRate: EditText
    private lateinit var btnPostJob: Button
    private lateinit var btnUploadImage: ImageButton
    private lateinit var imageView: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var database: DatabaseReference

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private var lastAssignedJobId: Int = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = FirebaseDatabase.getInstance().reference
        //_binding= FragmentEditProfileBinding.inflate(inflater,container,false)
        binding = FragmentPostingBinding.inflate(inflater, container, false)
        //firebase auth
        imageView = binding.imageView2
        binding.imageView2.setImageResource(R.drawable.profileunknown)
        btnPostJob = binding.btnPostJob
        btnUploadImage = binding.imageButton
        btnUploadImage.setOnClickListener {
            pickImageGallery()
        }
        btnPostJob.setOnClickListener {
            postJob()
        }

        return binding.root
    }
    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                selectedImageUri = data!!.data
                selectedImageUri?.let { uri ->
                    // Generate a unique image filename
                    imageFileName = UUID.randomUUID().toString()
                    // Call the function to upload the image to Firebase Storage and create the job
                    binding.imageView2.setImageURI(uri)
                    uploadImageToStorage(uri)
                }
            } else {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )


    private fun postJob() {
        if (validateInputs()) {
            val job = createJob()
            databaseHelper.pushJob(job)
            clearInputs()
        }
    }

    private fun validateInputs(): Boolean {
        myJobName = binding.etJobName
        myJobDescription = binding.etJobDescription
        myJobSpecialist = binding.jobspinner
        myJobArea = binding.areaspinner
        myJobPayRate = binding.etJobPayRate

        val jobName = myJobName.text.toString().trim()
        val jobDescription = myJobDescription.text.toString().trim()
        val jobSpecialist = myJobSpecialist.selectedItem.toString()
        val jobArea = myJobArea.selectedItem.toString()
        val jobPayRate = myJobPayRate.text.toString().trim()

        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            return false
        }

        if (jobName.isEmpty()) {
            myJobName.error = "Job name is required"
            myJobName.requestFocus()
            return false
        }

        if (jobDescription.isEmpty()) {
            myJobDescription.error = "Job description is required"
            myJobDescription.requestFocus()
            return false
        }

        if (jobSpecialist.isEmpty()) {
            Toast.makeText(requireContext(), "Please Select a Specialist", Toast.LENGTH_SHORT)
                .show()
            myJobSpecialist.requestFocus()
            return false
        }
        if (jobArea.isEmpty()) {
            Toast.makeText(requireContext(), "Please Select a Job Area", Toast.LENGTH_SHORT).show()
            myJobArea.requestFocus()
            return false
        }
        if (jobPayRate.isEmpty()) {
            myJobPayRate.error = "Job pay rate is required"
            myJobPayRate.requestFocus()
            return false
        }

        return true
    }

    private fun createJob(): EmployerJob {
        val jobName = myJobName.text.toString().trim()
        val jobDescription = myJobDescription.text.toString().trim()
        val jobSpecialist = myJobSpecialist.selectedItem.toString()
        val jobArea = myJobArea.selectedItem.toString()
        val jobPayRate = myJobPayRate.text.toString().trim().toDouble()

        val job = EmployerJob(
            jobImage = "images/$imageFileName.jpg",
            jobID = ++lastAssignedJobId,
            jobName = jobName,
            jobDescription = jobDescription,
            jobArea = jobArea,
            jobSpecialist = jobSpecialist,
            jobPayRate = jobPayRate
        )
        return job
    }

    private fun clearInputs() {
        myJobName.text.clear()
        myJobDescription.text.clear()
        binding.areaspinner.setSelection(0)
        myJobPayRate.text.clear()
        imageView.setImageResource(0)
        selectedImageUri = null
        binding.jobspinner.setSelection(0)
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
                val imageUrl = task.result.toString()
                // Call the function to save the image URL to the Realtime Database
                saveImageUrlToDatabase(imageUrl)
            } else {
                // Handle the failure case
            }
        }
    }

    private fun saveImageUrlToDatabase(imageUrl: String) {
        val databaseRef = FirebaseDatabase.getInstance().reference
        val jobRef = databaseRef.child("jobs").push()

        val job = EmployerJob(
            jobImage = imageUrl,
            // Set other properties of the EmployerJob object as needed
            // ...
        )

        jobRef.setValue(job)
            .addOnSuccessListener {
                // Image URL and other job details are saved successfully
                // Handle the success case
            }
            .addOnFailureListener {
                // Handle the failure case
            }
    }
}

