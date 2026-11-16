package com.swirlfist.sprited.presentation.di

import com.swirlfist.sprited.domain.usecase.ExportPixelImageToPngUseCase
import com.swirlfist.sprited.domain.usecase.ReadFromFileUseCase
import com.swirlfist.sprited.domain.usecase.WriteToFileUseCase
import com.swirlfist.sprited.presentation.usecase.ExportPixelImageToPngUseCaseImpl
import com.swirlfist.sprited.presentation.usecase.ReadFromFileUseCaseImpl
import com.swirlfist.sprited.presentation.usecase.WriteToFileUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SingletonComponentModule {

    @Binds
    abstract fun bindWriteToFileUseCase(
        impl: WriteToFileUseCaseImpl,
    ): WriteToFileUseCase

    @Binds
    abstract fun bindReadFromFileUseCase(
        impl: ReadFromFileUseCaseImpl,
    ): ReadFromFileUseCase

    @Binds
    abstract fun bindExportPixelImageToPngUseCase(
        impl: ExportPixelImageToPngUseCaseImpl,
    ): ExportPixelImageToPngUseCase
}