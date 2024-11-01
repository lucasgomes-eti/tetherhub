package eti.lucasgomes.tetherhub.exception

open class TetherHubError(val httCode: Int, val internalCode: String, val message: String)