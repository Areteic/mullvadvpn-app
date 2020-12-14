package net.mullvad.mullvadvpn.service

import android.os.Bundle
import android.os.Message
import net.mullvad.mullvadvpn.model.Settings

sealed class Event {
    abstract val type: Type

    val message: Message
        get() = Message.obtain().apply {
            what = type.ordinal
            data = Bundle()

            prepareData(data)
        }

    open fun prepareData(data: Bundle) {}

    class SettingsUpdate(val settings: Settings?) : Event() {
        companion object {
            private val settingsKey = "settings"
        }

        override val type = Type.SettingsUpdate

        constructor(data: Bundle) : this(data.getParcelable(settingsKey)) {}

        override fun prepareData(data: Bundle) {
            data.putParcelable(settingsKey, settings)
        }
    }

    enum class Type(val build: (Bundle) -> Event) {
        SettingsUpdate({ data -> SettingsUpdate(data) }),
    }

    companion object {
        fun fromMessage(message: Message): Event {
            val type = Type.values()[message.what]

            return type.build(message.data)
        }
    }
}
