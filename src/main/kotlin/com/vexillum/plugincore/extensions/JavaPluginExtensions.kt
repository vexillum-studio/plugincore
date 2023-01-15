package com.vexillum.plugincore.extensions

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

fun JavaPlugin.dispatchEvent(event: Event) =
    server.pluginManager.callEvent(event)

fun JavaPlugin.registerEvents(listener: Listener) =
    server.pluginManager.registerEvents(listener, this)

fun unregisterEvents(listener: Listener) =
    HandlerList.unregisterAll(listener)
