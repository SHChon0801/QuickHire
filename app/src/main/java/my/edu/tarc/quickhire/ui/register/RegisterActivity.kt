package my.edu.tarc.quickhire.ui.register

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.User
import my.edu.tarc.quickhire.databinding.ActivityRegisterBinding
import my.edu.tarc.quickhire.ui.login.LoginActivity
import my.edu.tarc.quickhire.ui.notifications.NotificationData
import java.io.ByteArrayOutputStream
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReferenceOrg: DatabaseReference
    private lateinit var databaseReferenceEmp: DatabaseReference
    private lateinit var database: FirebaseDatabase

    //Store profile
    private lateinit var profilePic:String
    private lateinit var name:String
    private lateinit var about:String
    private lateinit var state:String
    private lateinit var currentJob:String
    private lateinit var phone:String
    private lateinit var timePrefer:String
    private lateinit var education:String
    private lateinit var skill:String

    //Store notification
    private lateinit var notification: String

    var imageURL:String?=null
    var uri: Uri?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReferenceOrg = database.getReference("Organizations")
        databaseReferenceEmp = database.getReference("Employees")

        //Testing
        val currentUser = FirebaseAuth.getInstance().currentUser




        binding.btnRegister.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()
            val roleRadioGroup = binding.rgGroup
            val userRole = roleRadioGroup.checkedRadioButtonId
            var role = ""

            //Welcome notification

            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1 // months are zero-based, so add 1
            val date = calendar.get(Calendar.DATE)

            // Get a reference to the Firebase Storage
            val storageRef = Firebase.storage.reference

            // Get a reference to the drawable image
           // val drawableRef = resources.getDrawable(R.drawable.profileunknown, null)
            val drawableRef = resources.getDrawable(R.drawable.welcome, null)
            val bitmap = (drawableRef as BitmapDrawable).bitmap

            // Get a reference to the Firebase Storage path where you want to upload the image
            //val imageRef = storageRef.child("images/profileunknown.png")
            val imageRef = storageRef.child("images/welcome.png")

            // Convert the bitmap to a byte array
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            // Upload the image to Firebase Storage
            val uploadTask = imageRef.putBytes(data)



            val n_title=getString(R.string.welcome_title)
            val n_des=getString(R.string.welcome_des)
            val n_time = "$date-$month-$year"
            val n_type = "first_type"
            val n_image="https://firebasestorage.googleapis.com/v0/b/quickhire-409e0.appspot.com/o/images%2Fwelcome.png?alt=media&token=2fc39e88-398e-410b-989c-76f68e35718a"
            val n_UID="None"




            when(userRole) {
                R.id.rbEmp ->{
                    role = "Employee"
                }
                R.id.rbOrg ->{
                    role = "Organization"
                }
            }



            if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && roleRadioGroup.checkedRadioButtonId != -1){
                if(password == confirmPassword){

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if(it.isSuccessful){

                            profilePic=""
                            name=""
                            about=""
                            state=""
                            currentJob=""
                            phone=""
                            timePrefer=""
                            education=""
                            skill=""



                            val newUser = User(name,about,state,currentJob,phone,timePrefer,
                                education,skill,email,password,role,profilePic)

                            if(currentUser!=null) {
                                when (userRole) {
                                    R.id.rbEmp -> {
//                                        databaseReferenceEmp.child(currentUser.uid)
//                                            .setValue(newUser)
//
//                                        imageRef.putBytes(data)
//                                            .addOnSuccessListener { taskSnapshot ->
//                                                // Handle successful upload
//                                            }
//                                            .addOnFailureListener { exception ->
//                                                // Handle failed upload
//                                            }

                                        uploadTask.addOnSuccessListener { taskSnapshot ->

                                            val downloadUrl = taskSnapshot.metadata?.reference?.downloadUrl.toString()

                                            // Create an ArrayList to hold the notifications
                                            val notifications = ArrayList<NotificationData>()
                                            val notificationClass = NotificationData(n_title, n_des, n_time,n_type,n_UID, downloadUrl)

                                            notifications.add(notificationClass)

                                            // Save the notification data to Realtime Database
                                            databaseReferenceEmp.child(currentUser.uid).apply {
                                                setValue(newUser)
                                                child("notification").setValue(notifications)
                                            }


//                                            // Get the download URL of the uploaded image
//                                            taskSnapshot.storage.downloadUrl.addOnCompleteListener { urlTask ->
//                                                if (urlTask.isSuccessful) {
//                                                    val downloadUrl = urlTask.result.toString()
//
//                                                    // Create an ArrayList to hold the notifications
//                                                    val notifications = ArrayList<NotificationData>()
//                                                    val notificationClass = NotificationData(n_title, n_des, n_time, downloadUrl)
//
//                                                    notifications.add(notificationClass)
//
//                                                    // Save the notification data to Realtime Database
//                                                    databaseReferenceEmp.child(currentUser.uid).apply {
//                                                        setValue(newUser)
//                                                        child("notification").setValue(notifications)
//                                                    }
//                                                } else {
//                                                    // Handle any errors that occurred while retrieving the download URL
//                                                    Log.e(TAG, "Failed to retrieve download URL: ${urlTask.exception?.message}")
//                                                }
//                                            }


                                        }
                                        uploadTask.addOnFailureListener { exception ->
                                            // Handle any errors that occurred during the upload
                                            Log.e(TAG, "Failed to upload image: ${exception.message}")
                                        }






                                    }
                                    R.id.rbOrg -> {
                                        databaseReferenceOrg.child(currentUser.uid).setValue(newUser)

                                    }
                                }
                            }


                            val intent = Intent(this,LoginActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this,"Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this,"Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.textViewLoginPage.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


    }
}



































