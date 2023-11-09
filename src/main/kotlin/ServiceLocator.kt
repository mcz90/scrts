object ServiceLocator {

    private val validator: RangeValidator by lazy {
        RangeValidator()
    }

    private val rangeWrapper: RangeWrapper by lazy {
        RangeWrapper(validator)
    }

    private val handler: ExpensiveAndRiskyOperationHandler by lazy {
        ExpensiveAndRiskyOperationHandler()
    }

    val getResultsUseCase: GetResultsUseCase by lazy {
        GetResultsUseCase(rangeWrapper = rangeWrapper, handler = handler)
    }
}
