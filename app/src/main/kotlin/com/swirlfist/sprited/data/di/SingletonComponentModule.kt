package com.swirlfist.sprited.data.di

import com.swirlfist.sprited.data.repository.BasePixelImageRepository
import com.swirlfist.sprited.data.repository.BasePixelImageRepositoryImpl
import com.swirlfist.sprited.data.repository.PalettePresetsRepository
import com.swirlfist.sprited.data.repository.PalettePresetsRepositoryImpl
import com.swirlfist.sprited.data.repository.PixelImageEditorActionRepository
import com.swirlfist.sprited.data.repository.PixelImageEditorActionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SingletonComponentModule {

    @Singleton
    @Binds
    abstract fun bindBasePixelImageRepository(
        impl: BasePixelImageRepositoryImpl,
    ): BasePixelImageRepository

    @Singleton
    @Binds
    abstract fun bindPixelImageEditorActionRepository(
        impl: PixelImageEditorActionRepositoryImpl,
    ): PixelImageEditorActionRepository

    @Singleton
    @Binds
    abstract fun bindPalettePresetsRepository(
        impl: PalettePresetsRepositoryImpl,
    ): PalettePresetsRepository
}
