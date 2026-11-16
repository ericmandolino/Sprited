package com.swirlfist.sprited.domain.error

class ReadFromFileError(
    val innerException: Throwable
) : Throwable()