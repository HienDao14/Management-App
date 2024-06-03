package com.example.managementapp.util

sealed class AuthResult<T>(val data: T? = null) {
    class Authorized<T>(data: T? = null) : AuthResult<T>(data)
    class Unauthorized<T>(): AuthResult<T>()
    class MissingInfo<T>(): AuthResult<T>()
    class DuplicateUsername<T>(): AuthResult<T>()
    class IncorrectInfo<T>(): AuthResult<T>()
    class UnknownError<T>() : AuthResult<T>()
}