package my.edu.tarc.quickhire.ui.SearchandPost

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentPostingBinding
import my.edu.tarc.quickhire.ui.home.Job
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
    private lateinit var btnClear: Button
    private lateinit var btnUploadImage: ImageButton
    private lateinit var imageView: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var database: DatabaseReference
    private var maxJobId: Int = 0

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

        database.child("Jobs").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val job = snapshot.getValue(Job::class.java)
                    if (job != null && job.jobID!! > maxJobId) {
                        maxJobId = job.jobID
                    }
                }
                // Increment the last assigned job ID
                lastAssignedJobId = maxJobId
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
        imageView = binding.organizationImageDetail
        myJobName = binding.organizationNameDetail
        myJobDescription = binding.organizationDescriptionDetail
        myJobSpecialist = binding.organizationSpecialistSpinnerDetail
        myJobArea = binding.organizationAreaSpinnerDetail
        myJobPayRate = binding.organizationPayRateDetail
        binding.organizationImageDetail.setImageResource(R.drawable.profileunknown)
        btnPostJob = binding.btnPostJob
        btnClear = binding.btnClear
        btnUploadImage = binding.organizationUploadImageBtnDetail

        btnUploadImage.setOnClickListener {
            pickImageGallery()
        }
        btnPostJob.setOnClickListener {
            postJob()
        }
        btnClear.setOnClickListener {
            clearInputs()
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
                    binding.organizationImageDetail.setImageURI(uri)
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

    private fun createJob(): Job {
        val jobName = myJobName.text.toString().trim()
        val jobDescription = myJobDescription.text.toString().trim()
        val jobSpecialist = myJobSpecialist.selectedItem.toString()
        val jobArea = myJobArea.selectedItem.toString()
        val jobPayRate = myJobPayRate.text.toString().trim().toDouble()
        val user = FirebaseAuth.getInstance().currentUser
        val job = Job(
            jobImage = "images/$imageFileName.jpg",
            jobID = ++lastAssignedJobId,
            jobName = jobName,
            jobDescription = jobDescription,
            jobArea = jobArea,
            jobSpecialist = jobSpecialist,
            jobPayRate = jobPayRate,
            jobEmail = user!!.email
        )
        return job
    }

    private fun clearInputs() {
        myJobName.text.clear()
        myJobDescription.text.clear()
        binding.organizationAreaSpinnerDetail.setSelection(0)
        myJobPayRate.text.clear()
        imageView.setImageResource(0)
        selectedImageUri = null
        binding.organizationSpecialistSpinnerDetail.setSelection(0)
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

            } else {

            }
        }
    }

}

