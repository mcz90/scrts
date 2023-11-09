class RangeValidator {
    fun validate(from: Int, to: Int): ValidationResult {
        return when {
            from <= 0 -> ValidationResult.Invalid(IllegalStateException("range must start with value bigger than 0"))
            to <= 0 -> ValidationResult.Invalid(IllegalStateException("range must end with value bigger than 0"))
            from >= to -> ValidationResult.Invalid(IllegalStateException("from value must be bigger than to"))
            else -> ValidationResult.Valid
        }
    }
}

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val error: Throwable) : ValidationResult()
}
