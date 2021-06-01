package com.sanjog.baby2bodytest.view.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.sanjog.baby2bodytest.Baby2BodyApplication
import com.sanjog.baby2bodytest.R
import com.sanjog.baby2bodytest.data.entity.CharactersEntity
import com.sanjog.baby2bodytest.databinding.FragmentComicDetailBinding
import com.sanjog.baby2bodytest.utils.autoCleared
import javax.inject.Inject


/**
 * Created by Sanjog Shrestha on 2021/05/31.
 * Copyright (c) Sanjog Shrestha
 */
class ComicDetailFragment : Fragment(), OnItemClickListener {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    var binding by autoCleared<FragmentComicDetailBinding>()
    private lateinit var creatorRecyclerView: RecyclerView
    private lateinit var characterRecyclerView: RecyclerView
    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var characterAdapter: CharacterAdapter
    private lateinit var creatorsAdapter: CreatorsAdapter
    private lateinit var imagesAdapter: ImagesAdapter

    val model: ComicDetailViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            model.setComicID(ComicDetailFragmentArgs.fromBundle(it).comicID)
        }
    }

    override fun onAttach(context: Context) {
        Baby2BodyApplication.getApplicationComponent().inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        characterAdapter = CharacterAdapter(this)
        imagesAdapter = ImagesAdapter(this)
        creatorsAdapter = CreatorsAdapter()
        binding = FragmentComicDetailBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            entity = model.getComicDetail
            initCreatorsRecyclerView()
            initCharacterRecyclerView()
            initComicImagesRecyclerView()
            ViewCompat.setTransitionName(coverImageView, getString(R.string.transition_comic_thumbnail))
        }
    }

    private fun FragmentComicDetailBinding.initCreatorsRecyclerView() {
        creatorRecyclerView = creatorsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = creatorsAdapter
            creatorsAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        model.getComicCreators.observe(viewLifecycleOwner, Observer {
            creatorsEntity = it
            if(it == null) return@Observer
            creatorsAdapter.submitList(it)
        })
    }

    private fun FragmentComicDetailBinding.initCharacterRecyclerView() {
        characterRecyclerView = charactersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = characterAdapter
            creatorsAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        model.fetchCharacters.observe(viewLifecycleOwner, Observer {
            charactersEntity = it
            if(it == null) return@Observer
            characterAdapter.submitList(it.data)
        })
    }

    private fun FragmentComicDetailBinding.initComicImagesRecyclerView() {
        imageRecyclerView = imagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = imagesAdapter
            imagesAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        model.getComicDetail.observe(viewLifecycleOwner, Observer {
            if(it == null) return@Observer
            imagesAdapter.submitList(it.images)
        })
    }

    override fun onCharacterThumbnailClicked(entity: CharactersEntity) {
        val currentList = characterAdapter.currentList
        val list = ArrayList<String?>()
        for (e in currentList) {
            list.add(e.thumbnail)
        }
        findNavController().navigate(ComicDetailFragmentDirections
                .actionComicDetailFragmentToFullScreenImageFragment(
                        urlList = Gson().toJson(list),
                        hasMultipleItems = true))
    }

    override fun onComicThumbnailClicked(url: String) {
        val currentList = imagesAdapter.currentList
        findNavController().navigate(ComicDetailFragmentDirections
                .actionComicDetailFragmentToFullScreenImageFragment(
                        urlList = Gson().toJson(currentList),
                        hasMultipleItems = true))
    }
}