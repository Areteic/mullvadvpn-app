package net.mullvad.mullvadvpn.service

import android.os.Bundle
import android.os.Message
import java.util.ArrayList
import net.mullvad.mullvadvpn.model.GeoIpLocation
import net.mullvad.mullvadvpn.model.KeygenEvent
import net.mullvad.mullvadvpn.model.LoginStatus as LoginStatusData
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

    class AccountHistory(val history: ArrayList<String>?) : Event() {
        companion object {
            private val historyKey = "history"

            fun buildHistory(data: Bundle): ArrayList<String>? {
                return data.getStringArray(historyKey)?.let { historyArray ->
                    ArrayList(historyArray.toList())
                }
            }
        }

        override val type = Type.AccountHistory

        constructor(data: Bundle) : this(buildHistory(data)) {}

        override fun prepareData(data: Bundle) {
            data.putStringArray(historyKey, history?.toTypedArray())
        }
    }

    class LoginStatus(val status: LoginStatusData?) : Event() {
        companion object {
            private val statusKey = "status"
        }

        override val type = Type.LoginStatus

        constructor(data: Bundle) : this(data.getParcelable(statusKey)) {}

        override fun prepareData(data: Bundle) {
            data.putParcelable(statusKey, status)
        }
    }

    class NewLocation(val location: GeoIpLocation?) : Event() {
        companion object {
            private val locationKey = "location"
        }

        override val type = Type.NewLocation

        constructor(data: Bundle) : this(data.getParcelable(locationKey)) {}

        override fun prepareData(data: Bundle) {
            data.putParcelable(locationKey, location)
        }
    }

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

    class WireGuardKeyStatus(val keyStatus: KeygenEvent?) : Event() {
        companion object {
            private val keyStatusKey = "keyStatus"
        }

        override val type = Type.WireGuardKeyStatus

        constructor(data: Bundle) : this(data.getParcelable(keyStatusKey)) {}

        override fun prepareData(data: Bundle) {
            data.putParcelable(keyStatusKey, keyStatus)
        }
    }

    enum class Type(val build: (Bundle) -> Event) {
        AccountHistory({ data -> AccountHistory(data) }),
        LoginStatus({ data -> LoginStatus(data) }),
        NewLocation({ data -> NewLocation(data) }),
        SettingsUpdate({ data -> SettingsUpdate(data) }),
        WireGuardKeyStatus({ data -> WireGuardKeyStatus(data) }),
    }

    companion object {
        fun fromMessage(message: Message): Event {
            val type = Type.values()[message.what]

            return type.build(message.data)
        }
    }
}
