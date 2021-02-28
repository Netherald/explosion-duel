package com.github.netherald.duel.listeners

import net.minecraft.server.v1_8_R3.ChatComponentText
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
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
import kotlin.math.roundToInt

data class CoolTime(var now: Float, val max: Int, var percent: Float)

class TntThrowListener : Listener {

    val dangerIds = ArrayList<UUID>()
    var cooltimesTnt = HashMap<UUID, CoolTime>()
    var cooltimesNuclear = HashMap<UUID, CoolTime>()

    fun getPsNum(now: Float, max: Int) : String {
        var psNum = "["
        for(i in 1..(now * 4).roundToInt()) {
            psNum += "■"
        }
        for(i in 1..((max * 4) - (now * 4).roundToInt())) {
            psNum += "□"
        }
        psNum += "]"
        return psNum
    }

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
                cooltimesTnt[e.player.uniqueId] = CoolTime(5.0F, 5, 100F)

                val scheduleId =
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("duel"), {
                        if(player.inventory.itemInHand.type == Material.TNT) {
                            val packet = PacketPlayOutChat(
                                ChatComponentText(
                                    "${ChatColor.RED}TNT - ${
                                        String.format(
                                            "%.1f",
                                            cooltimesTnt[e.player.uniqueId]?.now!!
                                        )
                                    }s - ${
                                        getPsNum(
                                            cooltimesTnt[e.player.uniqueId]?.now!!,
                                            cooltimesTnt[e.player.uniqueId]?.max!!
                                        )
                                    }"
                                ),
                                2.toByte()
                            )
                            (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
                        }
                        cooltimesTnt[e.player.uniqueId]?.now = cooltimesTnt[e.player.uniqueId]?.now?.minus(0.1F)!!
                    }, 0L, 2L)

                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("duel"), {
                    if (player.inventory.itemInHand.type == Material.TNT) {
                        val packet = PacketPlayOutChat(
                            ChatComponentText("${ChatColor.RED}TNT - 0.0s - [□□□□□□□□□□□□□□□□□□□□]"),
                            2.toByte()
                        )
                        (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
                    }
                    cooltimesTnt.remove(player.uniqueId)
                    Bukkit.getScheduler().cancelTask(scheduleId)
                }, (cooltimesTnt[e.player.uniqueId]?.max?.times(20))!!.toLong())

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
                cooltimesNuclear[e.player.uniqueId] = CoolTime(5.0F, 5, 100F)
                val scheduleId =
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("duel"), {
                        if (player.inventory.itemInHand.type == Material.FIREBALL) {
                            val packet = PacketPlayOutChat(
                                ChatComponentText(
                                    "${ChatColor.RED}핵폭탄 - ${
                                        String.format(
                                            "%.1f",
                                            cooltimesNuclear[e.player.uniqueId]?.now!!
                                        )
                                    }s - ${
                                        getPsNum(
                                            cooltimesNuclear[e.player.uniqueId]?.now!!,
                                            cooltimesNuclear[e.player.uniqueId]?.max!!
                                        )
                                    }"
                                ),
                                2.toByte()
                            )
                            (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
                        }

                        cooltimesNuclear[e.player.uniqueId]?.now = cooltimesNuclear[e.player.uniqueId]?.now?.minus(0.1F)!!
                    }, 0L, 2L)

                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("duel"), {
                    if (player.inventory.itemInHand.type == Material.FIREBALL) {
                        val packet = PacketPlayOutChat(
                            ChatComponentText("${ChatColor.RED}핵폭탄 - 0.0s - [□□□□□□□□□□□□□□□□□□□□]"),
                            2.toByte()
                        )
                        (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
                    }
                    cooltimesNuclear.remove(player.uniqueId)
                    Bukkit.getScheduler().cancelTask(scheduleId)
                }, (cooltimesNuclear[e.player.uniqueId]?.max?.times(20))!!.toLong())
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