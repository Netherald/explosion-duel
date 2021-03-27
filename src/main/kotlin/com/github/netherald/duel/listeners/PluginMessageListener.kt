package com.github.netherald.duel.listeners

import com.github.netherald.duel.DuelPlugin
import com.google.common.annotations.Beta
import com.google.common.io.ByteStreams
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

@Beta
class PluginMessageListener(val plugin:DuelPlugin) : PluginMessageListener {

    override fun onPluginMessageReceived(channel: String?, player: Player?, message: ByteArray?) {
        if (!channel?.equals("exduel:channel",true)!!) return
        val din = ByteStreams.newDataInput(message!!)
        val subChannel = din.readUTF()
        if (subChannel.equals("")) {
            val data1 = din.readUTF()
            val data2 = din.readInt()

            TODO("할꺼 넣기")
        }
    }


}