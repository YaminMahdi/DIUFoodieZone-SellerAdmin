package com.diu.mlab.foodie.admin.domain


sealed interface RequestState<out Data> {
    data class Success<out Data>(val data: Data): RequestState<Data>
    data class Error(val error: String, val code: Int=0): RequestState<Nothing>
}