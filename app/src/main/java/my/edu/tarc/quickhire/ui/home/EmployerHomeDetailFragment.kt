package my.edu.tarc.quickhire.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import my.edu.tarc.quickhire.databinding.FragmentEmployerHomeDetailBinding

class EmployerHomeDetailFragment : Fragment() {

    private var _binding: FragmentEmployerHomeDetailBinding?= null
    private val binding get() = _binding!!

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
//    val jobPayRate = arguments?.getDouble("jobPayRate")

        Glide.with(this).load(jobImage).into(binding.employerJobImageDetail)
        binding.employerJobImageDetail
        binding.employerJobNameDetail.text = jobName
        binding.employerJobDescriptionDetail.text = jobDescription
        binding.employerJobAreaDetail.text = jobArea
        binding.employerJobSpecialistDetail.text = jobSpecialist
//        binding.employerJobPayRateDetail.text = jobPayRate

        return  binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}