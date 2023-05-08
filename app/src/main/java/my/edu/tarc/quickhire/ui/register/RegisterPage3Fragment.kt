package my.edu.tarc.quickhire.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import my.edu.tarc.quickhire.Employee
import my.edu.tarc.quickhire.Organization
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentRegisterPage2Binding
import my.edu.tarc.quickhire.databinding.FragmentRegisterPage3Binding
import my.edu.tarc.quickhire.ui.notifications.NotificationData
import java.util.*


class RegisterPage3Fragment : Fragment() {
    private lateinit var binding: FragmentRegisterPage3Binding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReferenceOrg: DatabaseReference
    private lateinit var database: FirebaseDatabase

    //Profile
    private lateinit var job:String
    private lateinit var address:String
    private lateinit var profilePic:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterPage3Binding.inflate(inflater,container,false)


        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReferenceOrg = database.getReference("Organization")


        binding.btnOrgReg.setOnClickListener {
            val email = binding.editTextOrgEmail.text.toString()
            val password = binding.editTextOrgPass.text.toString()
            val confirmPassword = binding.editTextOrgConfirmPass.text.toString()
            val name = binding.editTextOrgName.text.toString()
            val phone = binding.editTextPhoneOrg.text.toString()
            var role = "Organization"


            //Profile
            profilePic=""
            address=""
            job=""

            //Welcome notification
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1 // months are zero-based, so add 1
            val date = calendar.get(Calendar.DATE)

            val n_title=getString(R.string.welcome_title)
            val n_des=getString(R.string.welcome_des)
            val n_time = "$date-$month-$year"
            val n_type = "first_type"
            val n_image="https://firebasestorage.googleapis.com/v0/b/quickhire-409e0.appspot.com/o/images%2Fwelcome.png?alt=media&token=2fc39e88-398e-410b-989c-76f68e35718a"
            val n_UID="no-reply@gmail.com"


            val encodedEmail = email.replace(".","-")


            val orgRef = database.reference.child("Organizations")

            val newOrgRef = orgRef.child(encodedEmail)

            if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() &&
                name.isNotEmpty()){
                if(password == confirmPassword){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        if(it.isSuccessful){

                            // Create an ArrayList to hold the notifications
                            val notifications = ArrayList<NotificationData>()
                            val notificationClass = NotificationData(n_title, n_des, n_time,n_type,n_UID, n_image)
                            notifications.add(notificationClass)

                            val newOrg = Organization(email,password,name,phone,role,job,address,profilePic)


                            //newOrgRef.setValue(newOrg)
                            newOrgRef.apply {
                                setValue(newOrg)
                                child("notification").setValue(notifications)
                            }

                        }  else{
                            showToast(it.exception.toString(),Toast.LENGTH_SHORT)
                        }
                    }
                }else{
                    showToast("Passwords do not match",Toast.LENGTH_SHORT)
                }
            }else{
                showToast("Fields cannot be empty",Toast.LENGTH_SHORT)
            }

        }








        return binding.root
    }


    private fun showToast(message: String, length: Int) {
        val context = context
        if (context != null) {
            Toast.makeText(context, message, length).show()
        }
    }

}