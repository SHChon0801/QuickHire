package my.edu.tarc.quickhire.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentOrganizationHomeBinding

class OrganizationHomeFragment : Fragment() {

    private var _binding: FragmentOrganizationHomeBinding? = null

    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<OrganizationJob>
    private lateinit var jobImageList: Array<Int>
    private lateinit var jobNameList: Array<String>

    private fun getData() {
        for (i in jobImageList.indices) {
            val organizationJob = OrganizationJob(jobImageList[i], jobNameList[i])
            dataList.add(organizationJob)
        }
        recyclerView.adapter = OrganizationHomeAdapter(dataList)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrganizationHomeBinding.inflate(inflater, container, false)

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

        val root: View = binding.root
        recyclerView = root.findViewById(R.id.organizationJobRecyclerView)
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