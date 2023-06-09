package my.edu.tarc.quickhire.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentOrganizationHomeBinding

class OrganizationHomeFragment : Fragment() {

    private var _binding: FragmentOrganizationHomeBinding? = null

    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private val database = Firebase.database.reference
    private lateinit var dataList: ArrayList<Job>

    private fun getData() {
        database.child("Jobs").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()

                val user = FirebaseAuth.getInstance().currentUser
                for (jobSnapshot in snapshot.children) {
                    val job = jobSnapshot.getValue(Job::class.java)
                    job?.let{
                        if (user?.email == job.jobEmail) { // filter out jobs with current user's email
                            dataList.add(it)
                        }
                    }
                }

                if (dataList.isEmpty()) {
                    binding.organizationJobRecyclerView.visibility = View.GONE // hide RecyclerView
                } else {
                    binding.organizationJobRecyclerView.visibility = View.VISIBLE // show RecyclerView
                    recyclerView.adapter = OrganizationHomeAdapter(dataList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Database operation cancelled: ${error.message}", error.toException())
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun performSearch(query: String) {
        val filteredList = dataList.filter { job ->
            val isMatchingQuery = job.jobName!!.contains(query, ignoreCase = true)
            isMatchingQuery
        }.toMutableList()
        recyclerView.adapter = OrganizationHomeAdapter(filteredList)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrganizationHomeBinding.inflate(inflater, container, false)


        val root: View = binding.root
        recyclerView = root.findViewById(R.id.organizationJobRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf()
        getData()

        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                performSearch(newText)
                return true
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}