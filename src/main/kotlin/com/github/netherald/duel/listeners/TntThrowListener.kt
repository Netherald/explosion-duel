package com.github.netherald.duel.listeners

import org.bukkit.Material
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector

class TntThrowListener : Listener {

    @EventHandler
    fun onPlayerRightClickTnt(e:PlayerInteractEvent) {
        val action = e.action
        val player = e.player
        if (action == Action.RIGHT_CLICK_AIR && e.player.itemInHand.type == Material.TNT) {
            val v = 2
            val direction:Vector = player.eyeLocation.direction.multiply(v)
            val tnt = player.world.spawn(player.eyeLocation.add(direction),TNTPrimed::class.java)

            tnt.velocity = direction
            player.sendMessage("발사!")

        }
        else if (action == Action.RIGHT_CLICK_BLOCK && e.player.itemInHand.type == Material.TNT) {
            e.isCancelled = true
        }
    }

}