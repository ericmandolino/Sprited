package com.swirlfist.sprited.domain.error

class OpenPixelImageError(
    val innerException: Throwable
) : Throwable()