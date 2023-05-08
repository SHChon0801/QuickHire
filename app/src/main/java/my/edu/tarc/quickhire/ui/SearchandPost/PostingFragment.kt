package my.edu.tarc.quickhire.ui.SearchandPost

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentPostingBinding
import my.edu.tarc.quickhire.ui.home.EmployerJob
import java.io.IOException

class PostingFragment : Fragment() {
    private lateinit var binding: FragmentPostingBinding
    private val databaseHelper = FirebaseDatabaseHelper()

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
        ActivityResultCallback<ActivityResult> { result ->
            //get uri of the image
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                selectedImageUri = data!!.data
                binding.imageView2.setImageURI(selectedImageUri)
            } else {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
            }

        }
    )
//    private fun selectImage() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(intent, PICK_IMAGE_REQUEST)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
//            selectedImageUri = data.data
//            try {
//                val bitmap = MediaStore.Images.Media.getBitmap(
//                    requireContext().contentResolver,
//                    selectedImageUri
//                )
//                imageView.setImageBitmap(bitmap)
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//    }

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
        val imageUriString = selectedImageUri.toString()
        val job = EmployerJob(
            jobImage = imageUriString,
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
}

