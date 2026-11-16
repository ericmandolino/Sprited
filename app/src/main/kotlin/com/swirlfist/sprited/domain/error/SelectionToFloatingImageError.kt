package com.swirlfist.sprited.domain.error

class SelectionToFloatingImageError(
    val innerException: Throwable
) : Throwable()