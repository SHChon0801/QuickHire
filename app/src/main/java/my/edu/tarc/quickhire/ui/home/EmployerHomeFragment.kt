package my.edu.tarc.quickhire.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentEmployerHomeBinding

class EmployerHomeFragment : Fragment() {

    private var _binding: FragmentEmployerHomeBinding? = null

    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<EmployerJob>
    private lateinit var jobImageList: Array<Int>
    private lateinit var jobNameList: Array<String>
    private lateinit var jobDescriptionList: Array<String>
    private lateinit var jobSpecialistList: Array<String>
    private lateinit var jobPayRateList: Array<Double>

    private fun getData() {
        for (i in jobImageList.indices) {
            val employerJob = EmployerJob(jobImageList[i], jobNameList[i], jobDescriptionList[i], jobSpecialistList[i], jobPayRateList[i])
            dataList.add(employerJob)
        }
        recyclerView.adapter = EmployerHomeAdapter(dataList)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmployerHomeBinding.inflate(inflater, container, false)

        jobImageList = arrayOf(
            R.drawable.baseline_arrow_back_24,
            R.drawable.baseline_arrow_back_24,
            R.drawable.baseline_arrow_back_24,
            R.drawable.baseline_arrow_back_24,
            R.drawable.baseline_arrow_back_24,
            R.drawable.baseline_arrow_back_24,
            R.drawable.baseline_arrow_back_24,
            R.drawable.baseline_arrow_back_24,
            R.drawable.baseline_arrow_back_24,
            R.drawable.baseline_arrow_back_24
        )

        jobNameList = arrayOf(
            "ntest1",
            "ntest2",
            "ntest3",
            "ntest4",
            "ntest5",
            "ntest1",
            "ntest2",
            "ntest3",
            "ntest4",
            "ntest5",
        )

        jobDescriptionList = arrayOf(
            "dtest1",
            "dtest2",
            "dtest3",
            "dtest4",
            "dtest5",
            "dtest1",
            "dtest2",
            "dtest3",
            "dtest4",
            "dtest5",
        )

        jobSpecialistList = arrayOf(
            "stest1",
            "stest2",
            "stest3",
            "stest4",
            "stest5",
            "stest1",
            "stest2",
            "stest3",
            "stest4",
            "stest5",
        )

        jobPayRateList = arrayOf(
            0.1,
            0.2,
            0.3,
            0.4,
            0.5,
            0.1,
            0.2,
            0.3,
            0.4,
            0.5,
        )

        val root: View = binding.root

        recyclerView = root.findViewById(R.id.employerJobRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf()
        getData()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}