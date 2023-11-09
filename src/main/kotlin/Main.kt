import kotlinx.coroutines.runBlocking

fun main() {
    val useCase = ServiceLocator.getResultsUseCase
    runBlocking {
        val time = System.currentTimeMillis()
        when (val result = useCase.execute(from = 1, to = 40, maxRetries = 10)) {
            is GetResultsUseCase.ListResult.Success -> {
                println(getSuccessMessage(result.longValues))
                println("Computed in ${System.currentTimeMillis() - time} milliseconds")
            }

            is GetResultsUseCase.ListResult.Error -> {
                println(result.exception)
            }
        }
    }
}

private fun getSuccessMessage(values: List<Long>): String {
    return StringBuilder()
        .append("The result, list of ")
        .append("${values.size} ")
        .append(if (values.size == 1) "element" else "elements")
        .append(" ")
        .append("$values")
        .toString()
}


