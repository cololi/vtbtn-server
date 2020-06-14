package com.oruyanke.vtbs

import java.time.LocalDate

object UserConfig {
    const val SESSION_VALID_DAYS = 7
    const val SESSION_ID = "vtbtn_session"
}

fun UserSession.expireDate(): LocalDate =
    this.activatedDate.plusDays(UserConfig.SESSION_VALID_DAYS.toLong())

fun UserSession.isExpired() =
    this.expireDate() < LocalDate.now()

fun User.mustBeTheAdminOf(vtuber: String) {
    if (!isRoot && vtuber !in adminVtubers) {
        throw PermissionDenied("You have no permission for vtuber '$vtuber'")
    }
}
