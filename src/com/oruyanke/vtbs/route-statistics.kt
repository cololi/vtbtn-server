package com.oruyanke.vtbs

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import org.koin.ktor.ext.inject
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.eq


fun Route.statisticsRoutes() {
    val mongo: CoroutineClient by inject()

    route("/statistics") {
        get("/{vtb}") {
            errorAware {
                val vtb = param("vtb")
                var sum = 0
                mongo.forVtuber(vtb).statistics().find()
                    .toList()
                    .forEach {
                        sum += it.time
                    }
                call.respond(
                    mapOf(
                        "vtuber" to vtb,
                        "clickTimes" to sum
                    )
                )
            }
        }

        get("/{vtb}/{group}") {
            errorAware {
                val vtb = param("vtb")
                val group = param("group")
                var sum = 0
                mongo.forVtuber(vtb).statistics().find(Statistic::group eq group)
                    .toList()
                    .forEach {
                        sum += it.time
                    }
                call.respond(
                    mapOf(
                        "vtuber" to vtb,
                        "group" to group,
                        "clickTimes" to sum
                    )
                )
            }
        }

        get("/{vtb}/{name}") {
            errorAware {
                val vtb = param("vtb")
                val voiceName = param("name")
                var sum = 0
                mongo.forVtuber(vtb).statistics().find(Statistic::name eq voiceName)
                    .toList()
                    .forEach {
                        sum += it.time
                    }
                call.respond(
                    mapOf(
                        "vtuber" to vtb,
                        "name" to voiceName,
                        "clickTimes" to sum
                    )
                )
            }
        }

        post<PlusOneRequest>("/{vtb}") {
            errorAware {
                val vtb = param("vtb")
                mongo.forVtuber(vtb).statistics().plusOne(it)
            }
        }
    }
}
