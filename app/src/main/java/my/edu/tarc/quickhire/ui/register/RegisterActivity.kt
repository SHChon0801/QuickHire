package my.edu.tarc.quickhire.ui.register

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.google.firebase.storage.FirebaseStorage
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

//            val storageRef = FirebaseStorage.getInstance().getReference("images")
//            val imageRef = storageRef.child("welcome_image.jpg")
//
//            val n_pic = BitmapFactory.decodeResource(resources, R.drawable.profileunknown)
//            val baos = ByteArrayOutputStream()
//            n_pic.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//            val data = baos.toByteArray()

// Get a reference to the Firebase Storage
            val storageRef = Firebase.storage.reference

// Get a reference to the drawable image
            val drawableRef = resources.getDrawable(R.drawable.profileunknown, null)
            val bitmap = (drawableRef as BitmapDrawable).bitmap

// Get a reference to the Firebase Storage path where you want to upload the image
            val imageRef = storageRef.child("images/profileunknown.jpg")

// Convert the bitmap to a byte array
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

// Upload the image to Firebase Storage
            val uploadTask = imageRef.putBytes(data)



            val n_title=getString(R.string.welcome_title)
            val n_des=getString(R.string.welcome_des)

            val n_pic="R.drawable.profileunknown"
            val n_time = "$date-$month-$year"




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

//                            imageRef.putBytes(data)
//                                .addOnSuccessListener { taskSnapshot ->
//                                    // Handle successful upload
//                                }
//                                .addOnFailureListener { exception ->
//                                    // Handle failed upload
//                                }





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
                                            // Get the download URL of the uploaded image
                                            val downloadUrl = taskSnapshot.metadata?.reference?.downloadUrl.toString()
                                            val notificationClass = NotificationData(n_title, n_des, n_time, downloadUrl)
                                            // Save the notification data to Realtime Database
                                            databaseReferenceEmp.child(currentUser.uid).apply {
                                                setValue(newUser)
                                                child("notification").setValue(notificationClass)
                                            }


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



































