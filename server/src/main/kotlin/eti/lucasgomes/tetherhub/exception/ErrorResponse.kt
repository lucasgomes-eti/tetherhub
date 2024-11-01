package eti.lucasgomes.tetherhub.exception

data class ErrorResponse(
    val httCode: Int,
    val message: String,
    val internalCode: String,
    val errors: List<FieldErrorResponse>
)
