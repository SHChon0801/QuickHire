package my.edu.tarc.quickhire.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.databinding.FragmentRegisterPage1Binding


class RegisterPage1Fragment : Fragment() {

    private lateinit var binding: FragmentRegisterPage1Binding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterPage1Binding.inflate(inflater,container,false)

        binding.btnOrg.setOnClickListener {

        }


        binding.btnEmployee.setOnClickListener {
            findNavController().navigate(R.id.action_registerPage1Fragment_to_registerPage2Fragment)

        }




        return binding.root
    }




}