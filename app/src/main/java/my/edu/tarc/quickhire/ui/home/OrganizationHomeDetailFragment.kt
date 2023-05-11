package my.edu.tarc.quickhire.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
        val jobImage = arguments?.getString("jobImage")
        val jobName = arguments?.getString("jobName")
        val jobDescription = arguments?.getString("jobDescription")
        val jobArea = arguments?.getString("jobArea")
        val jobSpecialist = arguments?.getString("jobSpecialist")
        val jobPayRate = arguments?.getDouble("jobPayRate")

        binding.organizationBackBtnDetail.setOnClickListener{
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}