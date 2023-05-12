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
import com.google.firebase.database.DatabaseReference
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

        }

        binding.btnPostJob.setOnClickListener {
            if (jobId != null) {
                updateJob(jobId)
            }
        }

        return binding.root
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
    private fun updateJob(jobId: Int) {
        if (validateInputs()) {
            val jobImage = arguments?.getString("jobImage")
            val jobName = binding.organizationNameDetail.text.toString().trim()
            val jobDescription = binding.organizationDescriptionDetail.text.toString().trim()
            val jobSpecialist = binding.organizationSpecialistSpinnerDetail.selectedItem.toString()
            val jobArea = binding.organizationAreaSpinnerDetail.selectedItem.toString()
            val jobPayRate = binding.organizationPayRateDetail.text.toString().trim().toDouble()

            val mDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
            val mReferenceJob: DatabaseReference = mDatabase.reference.child("Jobs").child(jobId.toString())
            val updatedJob = Job(
                jobImage = jobImage,
                jobID = jobId,
                jobName = jobName,
                jobDescription = jobDescription,
                jobArea = jobArea,
                jobSpecialist = jobSpecialist,
                jobPayRate = jobPayRate,
                jobEmail = arguments?.getString("jobEmail")
            )
            mReferenceJob.setValue(updatedJob)
                .addOnSuccessListener {
                    // Data updated successfully
                    findNavController().navigateUp()
                }
                .addOnFailureListener {
                    // Failed to update data
                    Toast.makeText(requireContext(), "Failed to update job", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}