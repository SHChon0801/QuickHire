package my.edu.tarc.quickhire.ui.notifications

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentNotificationBinding
import java.util.*

class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    //firebase auth
    //private lateinit var auth: FirebaseAuth
    val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var notification: String

    //Recycler View
    private lateinit var notificationRecv: RecyclerView
    private lateinit var notificationList:ArrayList<NotificationData>
    private lateinit var notificationAdapter: NotificationAdapter

    var databaseReference:DatabaseReference?=null
    //private lateinit var databaseReference: DatabaseReference
     var eventListener:ValueEventListener?=null


    //Send Email
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNotificationBinding.inflate(inflater, container, false)

        val gridLayoutManager=GridLayoutManager(requireContext(),1)
        binding.recycleViewNotification.layoutManager=gridLayoutManager

        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        //Send email
        val UID = "aaa@gmail-com"

        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email

        val encodedEmail = userEmail?.replace(".","-")


        //Here
        notificationList = ArrayList()
        notificationAdapter = NotificationAdapter(requireContext(), notificationList)
        binding.recycleViewNotification.adapter = notificationAdapter
        databaseReference = FirebaseDatabase.getInstance()
            .getReference("Employees").child(encodedEmail?:"").child("notification")

        dialog.show()

        eventListener = databaseReference!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                notificationList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(NotificationData::class.java)
                    if (dataClass != null) {
                        notificationList.add(dataClass)
                    }
                }

//                for (itemSnapshot in snapshot.children) {
//                    val notificationMap = itemSnapshot.getValue() as HashMap<*, *>
//                    val n_title = notificationMap["notificationTitle"] as String
//                    val n_des = notificationMap["notificationDec"] as String
//                    val n_time = notificationMap["notificationTime"] as String
//                    val n_pic = notificationMap["notificationImage"] as String
//                    val dataClass = NotificationData(n_title, n_des, n_time, n_pic)
//                    notificationList.add(dataClass)
//                }
                notificationAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }
        })

        //BackUp
//        binding.test.setOnClickListener {
//
//            //Apply notification
//            val calendar = Calendar.getInstance()
//            val year = calendar.get(Calendar.YEAR)
//            val month = calendar.get(Calendar.MONTH) + 1 // months are zero-based, so add 1
//            val date = calendar.get(Calendar.DATE)
//
//            val user = FirebaseAuth.getInstance().currentUser
//            val userEmail = user?.email
//
//            val encodedEmail = userEmail?.replace(".","-")
//
//            val n_title =getString(R.string.request_job_title)
//            val n_des =getString(R.string.request_job)
//            val n_time = "$date-$month-$year"
//            val n_type = "second_type"
//            val n_UID =encodedEmail
//            val n_image ="https://firebasestorage.googleapis.com/v0/b/quickhire-409e0.appspot.com/o/images%2Fjob_want.jpg?alt=media&token=3144914c-33a8-4698-b25e-a62d7d191b42"
////
////            database = FirebaseDatabase.getInstance()
////                .getReference("Organizations").child(currentUser!!.uid).child("notification")
////
//            // Retrieve the employee's list of notifications from the database
//
//
//            // Initialize the database reference
//            database = FirebaseDatabase.getInstance()
//                .getReference("Organizations").child(UID).child("notification")
//
//            // Retrieve the employee's list of notifications from the database
//            database.get().addOnSuccessListener { snapshot ->
//                val notificationsEmp = snapshot.getValue(object :
//                    GenericTypeIndicator<ArrayList<NotificationData>>() {})
//
//                // Create a new notification and add it to the list
//                val notificationEmp =
//                    NotificationData(n_title, n_des, n_time, n_type, n_UID, n_image)
//                notificationsEmp?.add(notificationEmp)
//
//                // Update the employee's list of notifications in the database
//                database.setValue(notificationsEmp).addOnSuccessListener {
//                    Toast.makeText(requireContext(), "Notification added", Toast.LENGTH_SHORT)
//                        .show()
//                }.addOnFailureListener {
//                    Toast.makeText(
//                        requireContext(),
//                        "Failed to add notification",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }.addOnFailureListener {
//                Toast.makeText(
//                    requireContext(),
//                    "Failed to retrieve notifications",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//            Toast.makeText(requireContext(), "Apply Success", Toast.LENGTH_SHORT).show()
//
//        }

        binding.search.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })

        return binding.root
    }

    fun searchList(text: String) {
        val searchList = java.util.ArrayList<NotificationData>()
        for (dataClass in notificationList) {
            if (dataClass.notificationTitle?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                searchList.add(dataClass)
            }
        }
        notificationAdapter.searchDataList(searchList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}