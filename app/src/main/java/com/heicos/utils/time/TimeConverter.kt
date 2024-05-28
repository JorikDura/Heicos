package com.heicos.utils.time

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun convertTime(time: Long): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");

    val instant = Instant.ofEpochMilli(time)

    val date = formatter.format(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()))

    return date
}