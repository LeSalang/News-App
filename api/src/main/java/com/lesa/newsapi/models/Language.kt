package com.lesa.newsapi.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Language {
    @SerialName("ar")
    ARABIC,
    @SerialName("de")
    GERMAN,
    @SerialName("en")
    ENGLISH,
    @SerialName("es")
    SPANISH,
    @SerialName("fr")
    FRENCH,
    @SerialName("he")
    HEBREW,
    @SerialName("it")
    ITALIAN,
    @SerialName("nl")
    DUTCH,
    @SerialName("no")
    NORWEGIAN,
    @SerialName("pt")
    PORTUGUESE,
    @SerialName("ru")
    RUSSIAN,
    @SerialName("sv")
    SWEDISH,
    @SerialName("ud")
    URDU,
    @SerialName("zh")
    CHINESE
}