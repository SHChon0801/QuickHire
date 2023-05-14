package my.edu.tarc.quickhire.ui.notifications.organization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentNotificationDetail2Binding
import my.edu.tarc.quickhire.ui.notifications.NotificationData
import java.lang.Exception
import java.util.*


class NotificationDetail2Fragment : Fragment() {
    private var _binding: FragmentNotificationDetail2Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var databaseReferenceEmp: DatabaseReference

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
        val UID = arguments?.getString("UID")

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

            val n_title = getString(R.string.Congrate)
            val n_des = getString(R.string.Congrate_detail)
            val n_time = "$date-$month-$year"
            val n_type = "first_type"
            val n_UID = currentUser?.email.toString()
            val n_image =
                "https://firebasestorage.googleapis.com/v0/b/quickhire-409e0.appspot.com/o/images%2Fsuccess.jpg?alt=media&token=49feeaba-a91c-4c8c-9878-04f3de7fd879"

//
//            database = FirebaseDatabase.getInstance()
//                .getReference("Organizations").child(currentUser!!.uid).child("notification")
//
            // Retrieve the employee's list of notifications from the database


            if (UID != null) {
                // Initialize the database reference
                database = FirebaseDatabase.getInstance()
                    .getReference("Employees").child(UID).child("notification")

                // Retrieve the employee's list of notifications from the database
                database.get().addOnSuccessListener { snapshot ->
                    val notificationsEmp = snapshot.getValue(object :
                        GenericTypeIndicator<ArrayList<NotificationData>>() {})

                    // Create a new notification and add it to the list
                    val notificationEmp =
                        NotificationData(n_title, n_des, n_time, n_type, n_UID, n_image)
                    notificationsEmp?.add(notificationEmp)

                    // Update the employee's list of notifications in the database
                    database.setValue(notificationsEmp).addOnSuccessListener {

                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Failed to add notification",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Failed to retrieve notifications",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Toast.makeText(requireContext(), "Apply Success", Toast.LENGTH_SHORT).show()

            }

        }

        binding.reject.setOnClickListener {

            //firebase auth
            auth = FirebaseAuth.getInstance()
            val currentUser = FirebaseAuth.getInstance().currentUser


            //Apply notification
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1 // months are zero-based, so add 1
            val date = calendar.get(Calendar.DATE)

            val n_title = getString(R.string.rejected)
            val n_des = getString(R.string.reject_detail)
            val n_time = "$date-$month-$year"
            val n_type = "first_type"
            val n_UID = currentUser?.email.toString()
            val n_image ="https://firebasestorage.googleapis.com/v0/b/quickhire-409e0.appspot.com/o/images%2Freject.jpg?alt=media&token=71325a2a-1591-4ba3-b077-15a0ad38d534"
//
//            database = FirebaseDatabase.getInstance()
//                .getReference("Organizations").child(currentUser!!.uid).child("notification")
//
            // Retrieve the employee's list of notifications from the database


            if (UID != null) {
                // Initialize the database reference
                database = FirebaseDatabase.getInstance()
                    .getReference("Employees").child(UID).child("notification")

                // Retrieve the employee's list of notifications from the database
                database.get().addOnSuccessListener { snapshot ->
                    val notificationsEmp = snapshot.getValue(object :
                        GenericTypeIndicator<ArrayList<NotificationData>>() {})

                    // Create a new notification and add it to the list
                    val notificationEmp =
                        NotificationData(n_title, n_des, n_time, n_type, n_UID, n_image)
                    notificationsEmp?.add(notificationEmp)

                    // Update the employee's list of notifications in the database
                    database.setValue(notificationsEmp).addOnSuccessListener {
                        Toast.makeText(requireContext(), "Notification added", Toast.LENGTH_SHORT)
                            .show()
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Failed to add notification",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Failed to retrieve notifications",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Toast.makeText(requireContext(), "Rejected Success", Toast.LENGTH_SHORT).show()

            }

        }

        binding.viewDetail.setOnClickListener{

            val UID = arguments?.getString("UID")
            val encodedEmail = UID?.replace(".","-")

            val dataRef = FirebaseDatabase.getInstance().reference.child("Employees").child(encodedEmail?:"")

            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value!=null) {
                        val firstname = snapshot.child("firstName").value as String


                        val lastname = snapshot.child("lastName").value as String
                        val state = snapshot.child("state").value as String


                        val email1 = snapshot.child("email").value as String

                        val phone = snapshot.child("phone").value as String
                        val timePrefer = snapshot.child("timePrefer").value as String
                        val education=snapshot.child("education").value as String
                        val skill=snapshot.child("skill").value as String
                        val profilePic = snapshot.child("profilePic").value as String

                        //set data
                        binding.name.text = firstname+" "+lastname
                        binding.stated.text = state
                        binding.TimePrefer.text = timePrefer
                        binding.Email.text = email1
                        binding.ContactNumber.text = phone
                        binding.educational.text = education
                        binding.skill.text = skill

                        //set image
                        try{
                            Glide.with(requireContext())
                                .load(profilePic)
                                .placeholder(R.drawable.profileunknown)
                                .into(binding.profileImage)
                        }catch (e: Exception){

                        }
                    }else{
                        Toast.makeText(requireContext(), "Retrieved failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
            dataRef.addListenerForSingleValueEvent(eventListener)

            val expandableView: LinearLayout = binding.expandedView
            if (expandableView.visibility == View.GONE) {
                binding.identify.setImageResource(R.drawable.baseline_expand_more_24)
                expandableView.visibility = View.VISIBLE
            } else {
                expandableView.visibility = View.GONE
                binding.identify.setImageResource(R.drawable.baseline_chevron_right_24)
            }
        }


        return binding.root
    }

    private fun createNotificationChannel()
    {

    }

}