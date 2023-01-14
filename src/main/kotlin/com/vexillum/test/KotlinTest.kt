package com.vexillum.test

import java.util.*
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.plugin.java.JavaPlugin

class KotlinTest : JavaPlugin(), Listener {

    override fun onEnable() {
        Bukkit.getConsoleSender().sendMessage("Hello there")
        registerEvents(this)
    }

    @EventHandler
    fun onSwap(e: PlayerDropItemEvent) {
        with(server) {
            broadcastMessage("No tires cosas ${e.player.name}")
            val replaceMap = mapOf(
                "item1" to 1,
                "item2" to null
            )
            broadcastMessage("replace: $replaceMap")
            customMessage("Hola item1 es", replaceMap)
        }
    }

    fun registerEvents(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }

    fun Server.customMessage(
        message: String = "Default message",
        replaceMap: Map<String, Any?> = emptyMap()
    ) {
        val filteredMap = replaceMap.filterNotNullValues()
        server.broadcastMessage("$message $filteredMap")
    }

    fun <K, V> Map<K, V?>.filterNotNullValues(): Map<K, V> =
        this.filterValues { it != null } as Map<K, V>

}