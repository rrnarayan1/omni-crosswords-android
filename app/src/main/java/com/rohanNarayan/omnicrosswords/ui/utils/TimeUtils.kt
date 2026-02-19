package com.rohanNarayan.omnicrosswords.ui.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun toFormattedDate(date: Long): String {
    return DateTimeFormatter.ofPattern("EE M/d/yy")
        .withZone(ZoneId.of("UTC"))
        .format(Instant.ofEpochSecond(date))
}