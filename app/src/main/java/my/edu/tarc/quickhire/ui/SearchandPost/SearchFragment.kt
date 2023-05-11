package my.edu.tarc.quickhire.ui.SearchandPost

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentSearchBinding
import my.edu.tarc.quickhire.ui.FavouriteJobPlace
import my.edu.tarc.quickhire.ui.home.Job

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private lateinit var recyclerView: RecyclerView
    private val binding get() = _binding!!
    private val databaseHelper = FirebaseDatabaseHelper()
    private lateinit var dataList: ArrayList<Job>
    private var selectedArea: String? = null
    private var selectedSpecialist: String? = null

//    private fun pushJobToFirebase(job: Job) {
//        val key = databaseHelper.pushJob(job)
//    }
    private fun readJobsData() {
        databaseHelper.readJobsData(object : FirebaseDatabaseHelper.OnDataFetchListener {
            override fun onDataFetched(jobs: List<Job>) {
                // Handle fetched job data
                for (job in jobs) {
                    dataList.add(job)
                }
                recyclerView.adapter = SearchAdapter(dataList)
            }

            override fun onCancelled(exception: Exception) {
                // Handle cancelled/error case
            }
        })
    }

    private fun performSearch(query: String, jobArea: String?, jobSpecialist: String?) {
        val filteredList = dataList.filter { job ->
            val isMatchingQuery = job.jobName!!.contains(query, ignoreCase = true)
            val isMatchingArea = jobArea.isNullOrEmpty() || job.jobArea.equals(jobArea, ignoreCase = true)
            val isMatchingSpecialist = jobSpecialist.isNullOrEmpty() || job.jobSpecialist.equals(jobSpecialist, ignoreCase = true)
            isMatchingQuery && isMatchingArea && isMatchingSpecialist
        }
        recyclerView.adapter = SearchAdapter(filteredList)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)


        val root: View = binding.root

        recyclerView = root.findViewById(R.id.searchRecycleView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        dataList = ArrayList()
//        pushJobToFirebase(job)
        readJobsData()
        selectedArea = binding.spinner.selectedItem.toString()
        selectedSpecialist = binding.spinner1.selectedItem.toString()

        val searchView = binding.searchView
        val areaSpinner = binding.spinner
        val specialistSpinner = binding.spinner1

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Perform search when user submits the query (e.g., presses search button)
                performSearch(query, selectedArea, selectedSpecialist)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Perform search as the user types
                performSearch(newText, selectedArea, selectedSpecialist)
                return true
            }
        })
        // Set up listener for areaSpinner
        areaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedArea = parent?.getItemAtPosition(position).toString()
                // Perform search with updated values
                performSearch(searchView.query.toString(), selectedArea, selectedSpecialist)
                val job = FavouriteJobPlace(
                    jobArea = selectedArea,
                    clickRate = 0
                )
                databaseHelper.postOrUpdateFavouriteJobPlace(job)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedArea = null
                // Perform search with updated values
                performSearch(searchView.query.toString(), selectedArea, selectedSpecialist)
            }
        }

        // Set up listener for specialistSpinner
        specialistSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedSpecialist = parent?.getItemAtPosition(position).toString()
                // Perform search with updated values
                performSearch(searchView.query.toString(), selectedArea, selectedSpecialist)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedSpecialist = null
                // Perform search with updated values
                performSearch(searchView.query.toString(), selectedArea, selectedSpecialist)
            }
        }
        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}