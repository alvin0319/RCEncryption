package dev.minjae.rcencryption.resource

import cn.nukkit.resourcepacks.ZippedResourcePack
import java.io.File

class CustomZippedResourcePack(file: File) : ZippedResourcePack(file) {

    var encryptionKey: String = ""
}
