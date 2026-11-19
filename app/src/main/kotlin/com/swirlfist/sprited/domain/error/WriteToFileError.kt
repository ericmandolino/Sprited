package com.swirlfist.sprited.domain.error

class WriteToFileError(val innerException: Throwable) : Throwable()
