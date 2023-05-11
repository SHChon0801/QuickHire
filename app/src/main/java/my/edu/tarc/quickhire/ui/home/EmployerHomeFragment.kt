package my.edu.tarc.quickhire.ui.home

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentEmployerHomeBinding

class EmployerHomeFragment : Fragment() {

    private var _binding: FragmentEmployerHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private val database = Firebase.database.reference
    private var dataList: ArrayList<Job> = ArrayList()

    private fun getData() {
        database.child("Jobs").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()

                for (jobSnapshot in snapshot.children) {
                    val job = jobSnapshot.getValue(Job::class.java)
                    job?.let{
                        dataList.add(it)
                    }
                }
                recyclerView.adapter = EmployerHomeAdapter(dataList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Database operation cancelled: ${error.message}", error.toException())
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun performSearch(query: String) {
        val filteredList = dataList.filter { job ->
            val isMatchingQuery = job.jobName!!.contains(query, ignoreCase = true)
            isMatchingQuery
        }
        recyclerView.adapter = EmployerHomeAdapter(filteredList)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmployerHomeBinding.inflate(inflater, container, false)

        recyclerView = binding.root.findViewById(R.id.employerJobRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        getData()

        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                performSearch(newText)
                return true
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}