package dev.minjae.rcencryption.packet

import cn.nukkit.network.protocol.DataPacket
import cn.nukkit.network.protocol.ProtocolInfo
import cn.nukkit.resourcepacks.ResourcePack
import dev.minjae.rcencryption.resource.CustomZippedResourcePack

class CustomResourcePacksInfoPacket : DataPacket() {
    var mustAccept = false

    var scripting = false
    var forceServerPacks = false
    var behaviourPackEntries = mutableListOf<ResourcePack>()
    var resourcePackEntries = mutableListOf<ResourcePack>()

    override fun pid(): Byte {
        return NETWORK_ID
    }

    override fun decode() {
    }

    override fun encode() {
        reset()
        putBoolean(mustAccept)
        putBoolean(scripting)
        putBoolean(forceServerPacks)
        encodeBehaviourPacks()
        encodeResourcePacks()
    }

    private fun encodeResourcePacks() {
        putLShort(resourcePackEntries.size)
        for (entry in resourcePackEntries) {
            putString(entry.packId.toString())
            putString(entry.packVersion)
            putLLong(entry.packSize.toLong())
            if (entry is CustomZippedResourcePack) {
                putString(entry.encryptionKey)
            } else {
                putString("") // encryption key
            }

            putString("") // sub-pack name
            if (entry is CustomZippedResourcePack) {
                putString(entry.packId.toString())
            } else {
                putString("") // content identity
            }

            putBoolean(false) // scripting

            putBoolean(false) // raytracing capable
        }
    }

    private fun encodeBehaviourPacks() {
        putLShort(behaviourPackEntries.size)
        for (entry in behaviourPackEntries) {
            putString(entry.packId.toString())
            putString(entry.packVersion)
            putLLong(entry.packSize.toLong())
            putString("") // encryption key

            putString("") // sub-pack name

            putString("") // content identity

            putBoolean(false) // scripting
        }
    }

    companion object {
        const val NETWORK_ID = ProtocolInfo.RESOURCE_PACKS_INFO_PACKET
    }
}
