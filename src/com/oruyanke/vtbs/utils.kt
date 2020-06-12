package com.oruyanke.vtbs

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.util.pipeline.PipelineContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

suspend fun <R> PipelineContext<*, ApplicationCall>.errorAware(block: suspend () -> R): R? {
    return try {
        block()
    } catch (e: ResponseException) {
        call.respondError(e.code, e.localizedMessage)
        null
    } catch (e: Exception) {
        call.respondError(HttpStatusCode.InternalServerError, e.toString())
        null
    }
}

suspend fun ApplicationCall.respondError(code: HttpStatusCode, msg: String) =
    this.respondText(
        """{"code": 1, "msg":"$msg"""",
        ContentType.Application.Json,
        code
    )

fun PipelineContext<*, ApplicationCall>.param(name: String) =
    call.parameters[name] ?: throw IllegalArgumentException("Missing '$name'")

fun PipelineContext<*, ApplicationCall>.queryTime(name: String): LocalDate? =
    call.request.queryParameters[name]?.let {
        LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

fun PipelineContext<*, ApplicationCall>.queryTimeOrEpoch(name: String): LocalDate =
    queryTime(name) ?: LocalDate.EPOCH

fun PipelineContext<*, ApplicationCall>.queryTimeOrNow(name: String): LocalDate =
    queryTime(name) ?: LocalDate.now()

fun LocalDate.toHumanReadable() : String =
    this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
