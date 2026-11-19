package com.swirlfist.sprited.domain.usecase

import kotlinx.coroutines.flow.Flow

interface GetUndoEditorActionAvailableUseCase : UseCase<UseCaseParams.NoParams, Flow<Boolean>>
