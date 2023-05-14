package my.edu.tarc.quickhire.ui.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import my.edu.tarc.quickhire.R

import android.content.Intent
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import my.edu.tarc.quickhire.databinding.FragmentNotificationDetailBinding


class NotificationDetailFragment : Fragment() {
    private var _binding: FragmentNotificationDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var imageURL=""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationDetailBinding.inflate(inflater, container, false)

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
            findNavController().navigate(R.id.nav_notification)
        }



        return binding.root
    }



}