package my.edu.tarc.quickhire.ui.home

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    // Initialize dataList with an empty ArrayList
    private var dataList: ArrayList<EmployerJob> = ArrayList()

    private fun getData() {
        // Show a loading indicator, such as a progress bar or spinner

        database.child("Jobs").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Hide the loading indicator

                if (snapshot.exists()) {
                    for (jobSnapshot in snapshot.children) {
                        val job = jobSnapshot.getValue(EmployerJob::class.java)
                        if (job != null) {
                            dataList.add(job)
                        }
                    }
                    // Check for nullability before setting the adapter
                    if (recyclerView.adapter == null) {
                        recyclerView.adapter = EmployerHomeAdapter(dataList)
                    } else {
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                    // Show a message to the user indicating that no jobs were found
                } else {
                    // Hide the RecyclerView and show a message indicating that there are no jobs available
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Hide the loading indicator
                // Log the error message as a warning and print the stack trace of the error
                Log.w(TAG, "Database operation cancelled: ${error.message}", error.toException())
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmployerHomeBinding.inflate(inflater, container, false)

        val root: View = binding.root

        recyclerView = root.findViewById(R.id.employerJobRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        getData()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
