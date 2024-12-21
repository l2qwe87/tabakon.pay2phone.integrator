package ru.tabakon.integrator.host.http

interface IHttpResponse {
    val body : String;
}

interface IHttpClient {
    suspend fun get(method : String): IHttpResponse
    suspend fun post(method: String, body: String): IHttpResponse
}