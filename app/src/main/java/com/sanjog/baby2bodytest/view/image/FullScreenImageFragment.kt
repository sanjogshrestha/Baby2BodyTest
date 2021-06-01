package com.sanjog.baby2bodytest.view.image

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sanjog.baby2bodytest.databinding.FragmentImageFullScreenBinding
import com.sanjog.baby2bodytest.utils.autoCleared


/**
 * Created by Sanjog Shrestha on 2021/05/31.
 * Copyright (c) Sanjog Shrestha
 */
class FullScreenImageFragment : Fragment(){
    var binding by autoCleared<FragmentImageFullScreenBinding>()
    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var url : String
    private var isList : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val fromBundle = FullScreenImageFragmentArgs.fromBundle(it)
            url = fromBundle.urlList
            isList = fromBundle.hasMultipleItems
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        imagesAdapter = ImagesAdapter()
        binding = FragmentImageFullScreenBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            initRecyclerView()

            closeImageView.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun FragmentImageFullScreenBinding.initRecyclerView() {
        with(viewPager) {
            clipToPadding = false   // allow full width shown with padding
            clipChildren = false    // allow left/right item is not clipped
            offscreenPageLimit = 2  // make sure left/right item is rendered

            // increase this offset to show more of left/right
            val offsetPx = 20.dpToPx()
            setPadding(offsetPx, 0, offsetPx, 0)

            // increase this offset to increase distance between 2 items
            setPageTransformer(MarginPageTransformer(5))
            adapter = imagesAdapter
        }

        val list = ArrayList<String>()
        if(isList) {
            val type = object : TypeToken<List<String>>() {}.type
            val gsonList : List<String> = Gson().fromJson(url, type)
            list.addAll(gsonList)
        }else{
            list.add(url)
        }
        imagesAdapter.submitList(list)
        TabLayoutMediator(imageIndicator, viewPager) { _, _ -> }.attach()
    }

    private fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}