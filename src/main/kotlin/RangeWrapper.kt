class RangeWrapper(
    private val validator: RangeValidator,
) {

    fun getRange(from: Int, to: Int): RangeResult {
        return when (val validation = validateRange(from, to)) {
            ValidationResult.Valid -> RangeResult.Range(from..<to)
            is ValidationResult.Invalid -> RangeResult.Invalid(Error(validation.error))
        }
    }

    private fun validateRange(from: Int, to: Int) = validator.validate(from, to)

    sealed class RangeResult {
        data class Range(val range: IntRange) : RangeResult()
        data class Invalid(val error: Error) : RangeResult()
    }
}