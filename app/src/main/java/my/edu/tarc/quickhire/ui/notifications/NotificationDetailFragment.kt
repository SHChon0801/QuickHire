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
        val priority = arguments?.getString("Priority")

        binding.detailDesc.text = description
        binding.detailTitle.text = title
        binding.detailPriority.text = priority
        Glide.with(this).load(image).into(binding.detailImage)

//        val bundle = arguments
//        if (bundle != null) {
//            binding.detailDesc.text = bundle.getString("Description")
//            binding.detailTitle.text = bundle.getString("Title")
//            binding.detailPriority.text = bundle.getString("Priority")
//            imageURL = bundle.getString("Image")!!
//            Glide.with(this).load(bundle.getString("Image"))
//                .into(binding.detailImage)
//        }



//
//        //val bundle = intent.extras
//        if (bundle != null) {
//            binding.detailDesc.text = bundle.getString("Description")
//            binding.detailTitle.text = bundle.getString("Title")
//            binding.detailPriority.text = bundle.getString("Priority")
//            imageURL = bundle.getString("Image")!!
//            Glide.with(this).load(bundle.getString("Image"))
//                .into(binding.detailImage)
//        }


        binding.back.setOnClickListener{
            findNavController().navigate(R.id.nav_notification)
        }


        return binding.root
    }
}