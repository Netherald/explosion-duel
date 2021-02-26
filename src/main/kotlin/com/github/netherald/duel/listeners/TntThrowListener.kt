package com.github.netherald.duel.listeners

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Fireball
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector
import java.util.*
import kotlin.collections.ArrayList

class TntThrowListener : Listener {

    val dangerIds = ArrayList<UUID>()

    @EventHandler
    fun onPlayerRightClickTnt(e:PlayerInteractEvent) {
        val action = e.action
        val player = e.player
        val item = e.player.itemInHand
        val tlName = "${ChatColor.RED}TNT"
        val nlName = "${ChatColor.DARK_RED}핵폭탄"

        if (item.itemMeta.displayName == tlName) {

            if (action == Action.RIGHT_CLICK_AIR && item.type == Material.TNT) {
                val v = 2
                val direction:Vector = player.eyeLocation.direction.multiply(v)
                val tnt = player.world.spawn(player.eyeLocation.add(direction),TNTPrimed::class.java)

                tnt.velocity = direction
                player.sendMessage("발사!")

            }
            else if (action == Action.RIGHT_CLICK_BLOCK && item.type == Material.TNT) {
                e.isCancelled = true
            }

        }

        else if (item.itemMeta.displayName == nlName) {

            if (action == Action.RIGHT_CLICK_AIR && item.type == Material.FIREBALL) {
                val v = 2
                val direction:Vector = player.eyeLocation.direction.multiply(v)
                val ball = player.world.spawn(player.eyeLocation.add(direction),Fireball::class.java)
                dangerIds.add(ball.uniqueId)
                ball.velocity = direction
                player.sendMessage("발사!")

            }
            else if (action == Action.RIGHT_CLICK_BLOCK && item.type == Material.FIREBALL) {

                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onProjectileHit(e:ProjectileHitEvent) {
        val entity = e.entity
        val dangers = this.dangerIds
        if (entity is Fireball) {
            val entityId = entity.uniqueId
            if (dangers.contains(entityId)) {
                dangers.remove(entityId)
                entity.world.createExplosion(entity.location,10f)
            }
        }
    }

}