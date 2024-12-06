package keno.piecedgenlib.impl.player.data

import keno.piecedgenlib.impl.PGLib
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType

@Suppress("UnstableApiUsage")
object PGLDataAttachments {
    @JvmStatic
    val defaultRespawnPos: AttachmentType<DefaultRespawnPos> = AttachmentRegistry
        .createPersistent(PGLib.modId("default_respawn_pos"), DefaultRespawnPos.codec)

    @JvmStatic
    val allowedRespawnDimensions: AttachmentType<AllowedRespawnDimensions> = AttachmentRegistry
        .createPersistent(PGLib.modId("allowed_respawn_dimensions"), AllowedRespawnDimensions.codec)

    fun init() {

    }
}