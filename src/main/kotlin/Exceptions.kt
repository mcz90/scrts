
class MaxRetriesLimitException : Throwable("Reached limit of maximum retries")
class UnknownException : Throwable("Unknown error occurred")
class RangeException(override val message: String) : Throwable(message)