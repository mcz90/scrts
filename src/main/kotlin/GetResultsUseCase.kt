import ExpensiveAndRiskyOperationHandler.RiskyFunctionOperationResult

class GetResultsUseCase(
    private val rangeWrapper: RangeWrapper,
    private val handler: ExpensiveAndRiskyOperationHandler,
) {

    suspend fun execute(from: Int, to: Int, maxRetries: Int): ListResult {
        val range = when (val rangeResult = rangeWrapper.getRange(from, to)) {
            is RangeWrapper.RangeResult.Range -> rangeResult.range
            is RangeWrapper.RangeResult.Invalid -> return ListResult.Error(RangeException("Incorrect range"))
        }
        return when (val result = handler.getResults(range = range, maxRetries = maxRetries)) {
            is RiskyFunctionOperationResult.Success -> ListResult.Success(result.longValues)
            is RiskyFunctionOperationResult.Error -> ListResult.Error(result.exception)
        }

    }

    sealed class ListResult {
        data class Success(val longValues: List<Long>) : ListResult()
        data class Error(val exception: Throwable) : ListResult()
    }
}
