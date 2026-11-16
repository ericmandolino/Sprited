package com.swirlfist.sprited.domain.error

class SavePixelImageError(
    val innerException: Throwable
) : Throwable()