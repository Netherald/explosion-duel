package com.github.netherald.duel.listeners

import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import org.bukkit.Bukkit
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
import kotlin.collections.HashMap
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer

import net.minecraft.server.v1_8_R3.ChatComponentText

data class CoolTime(var now: Float, val max: Int, var percent: Float)

class TntThrowListener : Listener {

    val dangerIds = ArrayList<UUID>()
    var cooltimesTnt = HashMap<UUID, CoolTime>()

    @EventHandler
    fun onPlayerRightClickTnt(e:PlayerInteractEvent) {
        val action = e.action
        val player = e.player
        val item = e.player.itemInHand
        val tlName = "${ChatColor.RED}TNT"
        val nlName = "${ChatColor.DARK_RED}핵폭탄"

        if (item.itemMeta.displayName == tlName) {

            if (action == Action.RIGHT_CLICK_AIR && item.type == Material.TNT) {
                if(!cooltimesTnt.containsKey(e.player.uniqueId)) {
                    val v = 2
                    val direction: Vector = player.eyeLocation.direction.multiply(v)
                    val tnt = player.world.spawn(player.eyeLocation.add(direction), TNTPrimed::class.java)

                    tnt.fuseTicks = 20
                    tnt.velocity = direction
                    player.sendMessage("발사!")
                    cooltimesTnt[e.player.uniqueId] = CoolTime(5.0F, 5, 100F)

                    val onePercent = (100 / cooltimesTnt[e.player.uniqueId]?.max!!) / 20
                    println(onePercent)

                    val scheduleId =
                        Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("duel"), {

                            var ps = "["
                            println(Math.round(cooltimesTnt[e.player.uniqueId]?.percent!!))
                            for (i in 1..Math.round(cooltimesTnt[e.player.uniqueId]?.percent!!)) {
                                ps += "|"
                            }
                            ps += "]"

                            val packet = PacketPlayOutChat(
                                ChatComponentText("${ChatColor.RED}쿨타임: ${cooltimesTnt[e.player.uniqueId]?.now} $ps"),
                                2.toByte()
                            )
                            (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
                            cooltimesTnt[e.player.uniqueId]?.now = cooltimesTnt[e.player.uniqueId]?.now?.minus(0.1F)!!
                            cooltimesTnt[e.player.uniqueId]?.percent = cooltimesTnt[e.player.uniqueId]?.percent?.minus(onePercent)!!
                        }, 0L, 2L)

                    Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("duel"), {
                        val packet = PacketPlayOutChat(ChatComponentText("${ChatColor.RED}쿨타임: 0"), 2.toByte())
                        (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
                        cooltimesTnt.remove(player.uniqueId)
                        Bukkit.getScheduler().cancelTask(scheduleId)
                    }, (cooltimesTnt[e.player.uniqueId]?.max?.times(20))!!.toLong())
                }

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