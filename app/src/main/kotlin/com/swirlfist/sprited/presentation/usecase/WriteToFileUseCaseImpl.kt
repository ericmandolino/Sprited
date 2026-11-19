package com.swirlfist.sprited.presentation.usecase

import android.content.Context
import android.net.Uri
import com.swirlfist.sprited.domain.error.WriteToFileError
import com.swirlfist.sprited.domain.usecase.WriteToFileUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.FileOutputStream
import javax.inject.Inject

class WriteToFileUseCaseImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) : WriteToFileUseCase {
    override suspend fun invoke(params: WriteToFileUseCase.Params): Result<Unit> = try {
        writeToFile(
            params.content,
            params.uri,
        )
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(WriteToFileError(e))
    }

    private fun writeToFile(content: String, uri: Uri) {
        val contentResolver = applicationContext.contentResolver

        contentResolver.openFileDescriptor(uri, "w")?.use { descriptor ->
            FileOutputStream(descriptor.fileDescriptor).use { outputStream ->
                outputStream.channel.truncate(0)
                outputStream.write(content.toByteArray())
            }
        }
    }
}
