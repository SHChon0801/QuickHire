package my.edu.tarc.quickhire.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentOrganizationHomeDetailBinding

class OrganizationHomeDetailFragment : Fragment() {

    private var _binding: FragmentOrganizationHomeDetailBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOrganizationHomeDetailBinding.inflate(inflater, container, false)
        // Get a reference to the Firebase Realtime Database
        val database = FirebaseDatabase.getInstance()
        // Get a reference to the "jobs" node in the database
        val jobsRef = database.getReference("Jobs")

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

        binding.btnPostJob.setOnClickListener {
            // Get the updated job data from the UI
            val updatedJob = hashMapOf(
                "name" to binding.organizationNameDetail.text.toString(),
                "description" to binding.organizationDescriptionDetail.text.toString(),
                "area" to binding.organizationAreaSpinnerDetail.selectedItem.toString(),
                "specialist" to binding.organizationSpecialistSpinnerDetail.selectedItem.toString(),
                "payRate" to binding.organizationPayRateDetail.text.toString().toDouble()
            )

            // Update the job data in the database
            jobsRef.child(jobId.toString()).updateChildren(updatedJob as Map<String, Any>)
                .addOnSuccessListener {
                    // Show a success message
                    Toast.makeText(context, "Job updated successfully", Toast.LENGTH_SHORT).show()

                    // Navigate back to the job list
                    findNavController().navigateUp()
                }
                .addOnFailureListener {
                    // Show an error message
                    Toast.makeText(context, "Failed to update job", Toast.LENGTH_SHORT).show()
                }
        }

        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}