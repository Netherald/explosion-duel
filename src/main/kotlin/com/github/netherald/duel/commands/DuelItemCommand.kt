package com.github.netherald.duel.commands

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class DuelItemCommand : TabExecutor {

    override fun onTabComplete(
        sender: CommandSender?,
        command: Command?,
        alias: String?,
        args: Array<out String>?
    ): MutableList<String>? {
        if (args?.size == 1) {
            return mutableListOf("TNT","Nuclear")
        }
        return null
    }

    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if (!sender?.isOp!!) return false
        val player = sender as Player
        if (args?.size == 1) {
            return when (args[0]) {
                "TNT" -> {
                    val tnt = ItemStack(Material.TNT).let {
                        val meta = it.itemMeta
                        meta.displayName ="${ChatColor.RED}TNT"
                        it.itemMeta = meta
                        it
                    }
                    sender.inventory.addItem(tnt)
                    true
                }
                "Nuclear" -> {
                    val nuclear = ItemStack(Material.FIREBALL).let {
                        val meta = it.itemMeta
                        meta.displayName ="${ChatColor.DARK_RED}핵폭탄"
                        it.itemMeta = meta
                        it
                    }
                    sender.inventory.addItem(nuclear)
                    true
                }
                else -> false
            }
        }
        else return false
    }
}