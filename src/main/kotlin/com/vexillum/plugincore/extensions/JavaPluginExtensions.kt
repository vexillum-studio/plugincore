package com.vexillum.plugincore.extensions

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

val JavaPlugin.implementationVersion: String?
    get() = this::class.java.`package`?.implementationVersion

fun JavaPlugin.dispatchEvent(event: Event) =
    server.pluginManager.callEvent(event)

fun JavaPlugin.registerEvents(listener: Listener) =
    server.pluginManager.registerEvents(listener, this)

fun JavaPlugin.unregisterEvents(listener: Listener) =
    HandlerList.unregisterAll(listener)

fun JavaPlugin.disablePlugin() =
    Bukkit.getPluginManager().disablePlugin(this)

fun JavaPlugin.runOnNextTick(runnable: Runnable) {
    Bukkit.getScheduler().runTaskLater(this, runnable, 1)
}
