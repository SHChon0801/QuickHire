package my.edu.tarc.quickhire.ui.notifications.organization

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentNotificationBinding
import my.edu.tarc.quickhire.databinding.FragmentNotificationOrganizationBinding
import my.edu.tarc.quickhire.ui.notifications.NotificationAdapter
import my.edu.tarc.quickhire.ui.notifications.NotificationData
import java.util.*

class NotificationOrganizationFragment : Fragment() {
    private var _binding: FragmentNotificationOrganizationBinding? = null
    private val binding get() = _binding!!

    //firebase auth
    //private lateinit var auth: FirebaseAuth
    val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var notification: String

    //Recycler View
    private lateinit var notificationRecv: RecyclerView
    private lateinit var notificationList: ArrayList<NotificationData>
    private lateinit var notificationAdapter: NotificationAdapterOrganization

    var databaseReference: DatabaseReference?=null
    //private lateinit var databaseReference: DatabaseReference
    var eventListener: ValueEventListener?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationOrganizationBinding.inflate(inflater, container, false)

        val gridLayoutManager= GridLayoutManager(requireContext(),1)
        binding.recycleViewNotification.layoutManager=gridLayoutManager

        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        notificationList = ArrayList()
        notificationAdapter = NotificationAdapterOrganization(requireContext(), notificationList)
        binding.recycleViewNotification.adapter = notificationAdapter
        databaseReference = FirebaseDatabase.getInstance()
            .getReference("Organizations").child(currentUser!!.uid).child("notification")

//        databaseReference = FirebaseDatabase.getInstance("https://quickhire-409e0-default-rtdb.asia-southeast1.firebasedatabase.app/")
//            .getReference("Employees").child(currentUser!!.uid).child("notification")
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

                notificationAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }
        })

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