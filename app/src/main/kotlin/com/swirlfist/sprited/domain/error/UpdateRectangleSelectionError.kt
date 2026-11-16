package com.swirlfist.sprited.domain.error

class UpdateRectangleSelectionError(
    val innerException: Throwable
) : Throwable()