package com.github.netherald.duel

import com.github.netherald.duel.listeners.TntThrowListener
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class DuelPlugin: JavaPlugin() {

    private var duelFile: File? = null
    private var configuration: FileConfiguration? = null
    private val manager:PluginManager = server.pluginManager

    override fun onEnable() {
        load()
        manager.registerEvents(TntThrowListener(),this)
        logger.info("Enable Code") // Test Code
    }

    override fun onDisable() {
        save()
        logger.info("Disable Code") // Test Code
    }

    private fun load() {
        duelFile = File(dataFolder, "config.yml").also { file ->
            if (!file.exists()) {
                configuration?.save(file)
            }

            configuration?.load(file)
        }

        configuration = YamlConfiguration.loadConfiguration(duelFile!!)
    }

    private fun save() {
        configuration?.save(duelFile!!)
    }

    fun duelConfig(): FileConfiguration? {
        return configuration
    }
}