package net.mullvad.mullvadvpn.service

import android.os.Messenger

class ServiceInstance(
    val messenger: Messenger,
    val daemon: MullvadDaemon,
    val connectionProxy: ConnectionProxy,
    val customDns: CustomDns,
) {
    fun onDestroy() {
        customDns.onDestroy()
    }
}
