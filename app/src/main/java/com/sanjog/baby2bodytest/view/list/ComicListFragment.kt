package com.sanjog.baby2bodytest.view.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.sanjog.baby2bodytest.Baby2BodyApplication
import com.sanjog.baby2bodytest.R
import com.sanjog.baby2bodytest.data.entity.ComicEntity
import com.sanjog.baby2bodytest.databinding.DialogDateSelectionBinding
import com.sanjog.baby2bodytest.databinding.FragmentComicListBinding
import com.sanjog.baby2bodytest.utils.RetryCallback
import com.sanjog.baby2bodytest.utils.SpaceItemDecoration
import com.sanjog.baby2bodytest.utils.autoCleared
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


/**
 * Created by Sanjog Shrestha on 2021/05/30.
 * Copyright (c) Sanjog Shrestha
 */
class ComicListFragment : Fragment(), OnItemClickListener {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    var binding by autoCleared<FragmentComicListBinding>()
    private var dialogBinding by autoCleared<DialogDateSelectionBinding>()

    lateinit var recyclerView: RecyclerView
    private lateinit var comicAdapter: ComicAdapter

    val model: ComicListViewModel by viewModels {
        viewModelFactory
    }

    override fun onAttach(context: Context) {
        Baby2BodyApplication.getApplicationComponent().inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val year = model.yearQuery.value
        if(year.isNullOrEmpty()) {
            model.setYear(Calendar.getInstance().get(Calendar.YEAR).toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        comicAdapter = ComicAdapter(this)
        binding = FragmentComicListBinding.inflate(inflater, container, false).apply {
            model.yearQuery.observe(viewLifecycleOwner, {
                calendarYear.text = it
            })
            calendarYear.setOnClickListener {
                initDateDialog()
            }
            overflowMenu.setOnClickListener {
                val popupMenu = PopupMenu(requireContext(), it)
                popupMenu.setOnMenuItemClickListener { p0 ->
                    if (p0?.itemId == R.id.menu_licence) {
                        OssLicensesMenuActivity.setActivityTitle("Open Source License")
                        startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
                        return@setOnMenuItemClickListener true
                    }
                    false
                }
                popupMenu.inflate(R.menu.menu_dashboard)
                popupMenu.show()
            }
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            paginateResult = model.getComics
            viewModel = model
            initRecyclerView()

            callback = object : RetryCallback {
                override fun retry() {
                    model.refresh()
                }
            }
        }
    }

    private fun initDateDialog() {
        dialogBinding = DialogDateSelectionBinding.inflate(LayoutInflater.from(context)).apply {
            lifecycleOwner = this@ComicListFragment
        }

        val mBottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle)
        mBottomSheetDialog.setContentView(dialogBinding.root)
        mBottomSheetDialog.show()

        val yearPicker = dialogBinding.yearPicker

        val years = ArrayList<String>()
        for(i in Calendar.getInstance().get(Calendar.YEAR) downTo 1950){
            years.add(i.toString())
        }

        var yearList = arrayOfNulls<String>(years.size)
        yearList = years.toArray(yearList)
        yearPicker.minValue = 0
        yearPicker.maxValue = years.size-1
        yearPicker.displayedValues = yearList

        yearPicker.value = yearList.indexOf(model.yearQuery.value)

        var selectedYearValue = yearPicker.value

        yearPicker.setOnValueChangedListener { _, _, value ->
            selectedYearValue = value
        }

        dialogBinding.okButton.setOnClickListener {
            mBottomSheetDialog.dismiss()
            model.setYear(yearList[selectedYearValue].toString())
        }

        dialogBinding.negativeButton.setOnClickListener {
            mBottomSheetDialog.dismiss()
        }
    }

    private fun FragmentComicListBinding.initRecyclerView() {
        recyclerView = comicRecyclerView.apply {
            addItemDecoration(SpaceItemDecoration(this.context, R.dimen.grid_space))
            adapter = comicAdapter
            comicAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == comicAdapter.itemCount - 1) {
                        model.loadNextPage()
                    }
                }
            })
        }

        model.getComics.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            val data = it.data
            comicAdapter.submitList(data)
        })

        model.loadMoreStatus.observe(viewLifecycleOwner, { more ->
            if (more == null) {
                binding.loadingMore = false
            } else {
                binding.loadingMore = more.isRunning
                val error = more.errorMessageIfNotHandled
                if (error != null) {
                    showSnackBar(binding.loadMoreBar, error)
                }
            }
        })
    }

    override fun onComicThumbnailClicked(entity: ComicEntity, view: View) {
        val action = ComicListFragmentDirections.actionComicListFragmentToComicDetailFragment(
            comicID = entity.id
        )
        val extras = FragmentNavigator.Extras.Builder()
                .addSharedElement(view, ViewCompat.getTransitionName(view)!!)
                .build()
        findNavController().navigate(action, extras)
    }

    /**
     *  Displays snackbar message
     *  @param v : view binded for snackbar
     *  @param message : Content for the snack
     *  */
    private fun showSnackBar(v: View, message: String?){
        val msg : String = if (message.isNullOrEmpty()) getString(R.string.try_again) else message
        val snackBar: Snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG)
        snackBar.apply {
            val snackTextView =
                    (this.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView)
            snackTextView.maxLines = 4
        }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }
}