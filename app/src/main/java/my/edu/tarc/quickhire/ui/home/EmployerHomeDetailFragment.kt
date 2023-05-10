package my.edu.tarc.quickhire.ui.home

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
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

        //    Retrieve data from bundle
        val jobImage = arguments?.getString("jobImage")
        val jobName = arguments?.getString("jobName")
        val jobDescription = arguments?.getString("jobDescription")
        val jobArea = arguments?.getString("jobArea")
        val jobSpecialist = arguments?.getString("jobSpecialist")
        val jobPayRate = arguments?.getDouble("jobPayRate")

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

        binding.employerJobNameDetail.text = jobName
        binding.employerJobDescriptionDetail.text = jobDescription
        binding.employerJobAreaDetail.text = jobArea
        binding.employerJobSpecialistDetail.text = jobSpecialist
        binding.employerJobPayRateDetail.text = jobPayRate.toString()

        binding.employerBackBtnDetail.setOnClickListener{
            findNavController().navigateUp()
        }

        return  binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}