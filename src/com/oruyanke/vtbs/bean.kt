package com.oruyanke.vtbs

data class GroupResponse(val name: String, val desc: Map<String, String>, var voices: List<VoiceResponse>)

data class VoiceResponse(val name: String, val url: String, val group: String, val desc: Map<String, String>)

data class AddVoiceRequest(val name: String, val url: String, val desc: Map<String, String>)

data class AddGroupRequest(val name: String, val desc: Map<String, String>)

fun Map<String, String>.toLocalizedTexts() =
    this.map { LocalizedText(it.key, it.value) }
        .toList()
