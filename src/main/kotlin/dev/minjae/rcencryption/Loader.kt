package dev.minjae.rcencryption

import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.server.DataPacketSendEvent
import cn.nukkit.network.protocol.ResourcePacksInfoPacket
import cn.nukkit.plugin.PluginBase
import cn.nukkit.resourcepacks.ZippedResourcePack
import cn.nukkit.utils.Config
import dev.minjae.rcencryption.packet.CustomResourcePacksInfoPacket
import dev.minjae.rcencryption.resource.CustomZippedResourcePack
import java.io.File

class Loader : PluginBase(), Listener {

    val encryptionKeys: MutableMap<String, String> = mutableMapOf()

    override fun onEnable() {
        saveResource("encryption_keys.yml")
        val config = Config(dataFolder.resolve("encryption_keys.yml"), Config.YAML)
        val encryptionKeys = config.get("encryption-keys", mapOf<String, String>()) as Map<String, String>
        if (encryptionKeys.isNotEmpty()) {
            val iterator = server.resourcePackManager.resourceStack.iterator()
            while (iterator.hasNext()) {
                val current = iterator.next()
                if (current is ZippedResourcePack) {
                    val field = ZippedResourcePack::class.java.getDeclaredField("file")
                    field.isAccessible = true
                    val file = field.get(current) as File
                    val fileName = file.name
                    if (encryptionKeys.containsKey(fileName)) {
                        logger.info("Loaded encryption key for resource pack ${current.packName} (${encryptionKeys[fileName]!!})")
                        val customResourcePack = CustomZippedResourcePack(file)
                        customResourcePack.encryptionKey = encryptionKeys[fileName]!!
                        server.resourcePackManager.resourceStack[
                            server.resourcePackManager.resourceStack.indexOf(
                                current
                            )
                        ] = customResourcePack
                    }
                }
            }
        }
        server.pluginManager.registerEvents(this, this)

        server.network.registerPacket(
            CustomResourcePacksInfoPacket.NETWORK_ID,
            CustomResourcePacksInfoPacket::class.java
        )
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("UNUSED")
    fun onDataPacketSend(event: DataPacketSendEvent) {
        val packet = event.packet
        if (packet is ResourcePacksInfoPacket) {
            event.isCancelled = true
            val infoPacket = CustomResourcePacksInfoPacket()
            infoPacket.resourcePackEntries = server.resourcePackManager.resourceStack.toMutableList()
            infoPacket.mustAccept = server.forceResources
            event.player.dataPacket(infoPacket)
        }
    }
}
