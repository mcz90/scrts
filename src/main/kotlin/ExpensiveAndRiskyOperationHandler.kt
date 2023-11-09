import ExpensiveAndRiskyOperationHandler.RiskyFunctionOperationResult.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class ExpensiveAndRiskyOperationHandler {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getResults(range: IntRange, maxRetries: Int): RiskyFunctionOperationResult {
        val resultList = range.asFlow()
            .flatMapMerge {
                expensiveAndRiskyFunctionFlow(it, retries = maxRetries)
            }
            .catch { throwable ->
                val error = when (throwable) {
                    is MaxRetriesLimitException -> Error(throwable)
                    is UnknownException -> Error(throwable)
                    else -> Error(throwable)
                }
                emit(error)
            }
            .toList()


        return when {
            resultList.isNotEmpty() && resultList.all { it is Success } -> {
                Success(
                    resultList
                        .map { (it as Success).longValues }
                        .flatten()
                        .distinct()
                )
            }

            resultList.isNotEmpty() && resultList.any { it is Error } -> {
                val firstError = resultList.filterIsInstance<Error>().first()
                Error(firstError.exception)
            }

            else -> Error(UnknownException())
        }
    }


    private suspend fun expensiveAndRiskyFunctionFlow(
        value: Int,
        retries: Int = Int.MAX_VALUE
    ): Flow<RiskyFunctionOperationResult> {
        return flow {
            emit(
                retryOnArithmeticException(retries) {
                    expensiveAndRiskyFunction(value)
                }
            )
        }
    }

    @Throws(ArithmeticException::class)
    private suspend fun expensiveAndRiskyFunction(n: Int): Long {
        val randomNumberThatThrows = (0..Int.MAX_VALUE).random()
        if (n == randomNumberThatThrows) throw ArithmeticException()
        var result = 0L
        for (i in (n % 10) until 10) {
            delay(100)
            result += i / n
        }
        return result
    }

    private suspend fun retryOnArithmeticException(
        maxRetries: Int,
        action: suspend () -> Long
    ): RiskyFunctionOperationResult {
        var retryCount = 0
        var result: Long?
        while (retryCount < maxRetries) {
            result = try {
                action()
            } catch (error: ArithmeticException) {
                null
            } catch (error: Throwable) {
                throw UnknownException()
            }
            if (result == null) {
                println("Retrying...")
                retryCount++
            } else {
                return Success(listOf(result))
            }
        }
        throw MaxRetriesLimitException()
    }

    sealed class RiskyFunctionOperationResult {
        data class Success(val longValues: List<Long>) : RiskyFunctionOperationResult()
        data class Error(val exception: Throwable) : RiskyFunctionOperationResult()
    }
}
