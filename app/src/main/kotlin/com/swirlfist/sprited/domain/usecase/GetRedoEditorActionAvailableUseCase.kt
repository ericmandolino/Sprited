package com.swirlfist.sprited.domain.usecase

import kotlinx.coroutines.flow.Flow

interface GetRedoEditorActionAvailableUseCase : UseCase<UseCaseParams.NoParams, Flow<Boolean>>
