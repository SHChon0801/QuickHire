package my.edu.tarc.quickhire.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import my.edu.tarc.quickhire.Employee
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentRegisterPage1Binding
import my.edu.tarc.quickhire.databinding.FragmentRegisterPage2Binding
import my.edu.tarc.quickhire.ui.login.LoginActivity
import my.edu.tarc.quickhire.ui.notifications.NotificationData
import java.util.*


class RegisterPage2Fragment : Fragment() {
    private lateinit var binding: FragmentRegisterPage2Binding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReferenceEmp: DatabaseReference
    private lateinit var database: FirebaseDatabase

    //Profile
    private lateinit var profilePic:String
    private lateinit var about:String
    private lateinit var state:String
    private lateinit var currentJob:String
    private lateinit var timePrefer:String
    private lateinit var education:String
    private lateinit var skill:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterPage2Binding.inflate(inflater,container,false)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        //databaseReferenceEmp = database.reference

        //val currentUser = FirebaseAuth.getInstance().currentUser


        binding.btnEmpReg.setOnClickListener {
            val email = binding.editTextEmpEmail.text.toString()
            val password = binding.editTextEmpPass.text.toString()
            val confirmPassword = binding.editTextEmpConfirmPass.text.toString()
            val firstName = binding.editTextFirstName.text.toString()
            val lastName = binding.editTextLastName.text.toString()
            val phone = binding.editTextEmpPhone.text.toString()
            val role = "Employee"
            //Profile
            profilePic=""
            about=""
            state=""
            currentJob=""
            timePrefer=""
            education=""
            skill=""

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




            val employeesRef = database.reference.child("Employees")



            val newEmployeeRef = employeesRef.child(encodedEmail)

            if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() &&
                    firstName.isNotEmpty() && lastName.isNotEmpty()){
                if(password == confirmPassword){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        if(it.isSuccessful){

                            // Create an ArrayList to hold the notifications
                            val notifications = ArrayList<NotificationData>()
                            val notificationClass = NotificationData(n_title, n_des, n_time,n_type,n_UID, n_image)
                            notifications.add(notificationClass)

                            val newEmp = Employee(email,password,firstName,lastName,phone,role,about,state,currentJob,timePrefer,education,skill,profilePic)


//                            newEmployeeRef.setValue(newEmp)

                            // Save the notification data to Realtime Database
                            newEmployeeRef.apply {
                                setValue(newEmp)
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

