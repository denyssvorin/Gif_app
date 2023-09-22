package com.example.natifetesttask.ui.gifdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.natifetesttask.R
import com.example.natifetesttask.databinding.FragmentGifsDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GifDetailsFragment: Fragment() {
    private var _binding: FragmentGifsDetailsBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<GifDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGifsDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireContext())
            .load(args.imageUrl)
            .error(R.drawable.error_24)
            .into(binding.imageViewGif)
    }
}