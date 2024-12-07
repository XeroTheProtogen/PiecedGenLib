package keno.piecedgenlib.impl.player.data

import keno.piecedgenlib.impl.PGLib
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType

@Suppress("UnstableApiUsage")
object PGLDataAttachments {
    @JvmStatic
    val defaultRespawnPos: AttachmentType<DefaultRespawnPos> = AttachmentRegistry.create(PGLib.modId("default_respawn_pos")) {
        builder: AttachmentRegistry.Builder<DefaultRespawnPos> ->
        builder.copyOnDeath().persistent(DefaultRespawnPos.codec)
    }

    @JvmStatic
    val allowedRespawnDimensions: AttachmentType<AllowedRespawnDimensions> = AttachmentRegistry.create(PGLib.modId("allowed_respawn_dimensions")) {
        builder: AttachmentRegistry.Builder<AllowedRespawnDimensions> ->
        builder.copyOnDeath().persistent(AllowedRespawnDimensions.codec)
    }

    fun init() {

    }
}