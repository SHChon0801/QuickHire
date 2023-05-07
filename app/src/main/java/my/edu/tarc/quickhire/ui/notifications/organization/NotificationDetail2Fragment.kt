package my.edu.tarc.quickhire.ui.notifications.organization

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentNotificationDetail2Binding
import my.edu.tarc.quickhire.databinding.FragmentNotificationDetailOrganizationBinding
import my.edu.tarc.quickhire.ui.notifications.NotificationData
import java.util.*

class NotificationDetail2Fragment : Fragment() {
    private var _binding: FragmentNotificationDetail2Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    var imageURL=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationDetail2Binding.inflate(inflater, container, false)

        val image = arguments?.getString("Image")
        val description = arguments?.getString("Description")
        val title = arguments?.getString("Title")
        val time = arguments?.getString("Time")
        val type = arguments?.getString("Type")

        binding.detailDesc.text = description
        binding.detailTitle.text = title
        binding.detailTime.text = time
        Glide.with(this).load(image).into(binding.detailImage)



        binding.back.setOnClickListener{
            findNavController().navigate(R.id.nav_notificationOrganization)
        }

        binding.apply.setOnClickListener {

            //firebase auth
            auth = FirebaseAuth.getInstance()
            val currentUser = FirebaseAuth.getInstance().currentUser


            //Apply notification
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1 // months are zero-based, so add 1
            val date = calendar.get(Calendar.DATE)

            val n_title=getString(R.string.Congrate)
            val n_des=getString(R.string.Congrate_detail)
            val n_time = "$date-$month-$year"
            val n_type = "first_type"
            val n_image="https://firebasestorage.googleapis.com/v0/b/quickhire-409e0.appspot.com/o/images%2Fsuccess.jpg?alt=media&token=49feeaba-a91c-4c8c-9878-04f3de7fd879"

//
//            database = FirebaseDatabase.getInstance()
//                .getReference("Organizations").child(currentUser!!.uid).child("notification")
//

            if(currentUser!=null) {
                // Create an ArrayList to hold the notifications
                val notifications = ArrayList<NotificationData>()
                val notificationClass = NotificationData(n_title, n_des, n_time,n_type, n_image)

                notifications.add(notificationClass)

                // Save the notification data to Realtime Database
                database.child("Employees").child("notification").setValue(notifications)

                Toast.makeText(requireContext(),"Apply Success",Toast.LENGTH_SHORT).show()

            }




        }












        binding.reject.setOnClickListener {

        }

        return binding.root
    }
}