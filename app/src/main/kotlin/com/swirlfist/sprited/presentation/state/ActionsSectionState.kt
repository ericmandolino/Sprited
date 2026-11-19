package com.swirlfist.sprited.presentation.state

import com.swirlfist.sprited.domain.model.ActionModel
import com.swirlfist.sprited.presentation.section.ActionButtonType

data class ActionsSectionState(val actionModels: Map<ActionButtonType, ActionModel> = emptyMap())
