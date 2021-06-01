package com.sanjog.baby2bodytest.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sanjog.baby2bodytest.view.detail.ComicDetailViewModel
import com.sanjog.baby2bodytest.view.list.ComicListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds @IntoMap
    @ViewModelKey(ComicListViewModel::class)
    abstract fun bindComicListViewModel(viewModel: ComicListViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(ComicDetailViewModel::class)
    abstract fun bindComicDetailViewModel(viewModel: ComicDetailViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: Baby2BodyViewModelFactory): ViewModelProvider.Factory
}
