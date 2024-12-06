package keno.piecedgenlib.api.player

import keno.piecedgenlib.impl.player.data.AllowedRespawnDimensions
import keno.piecedgenlib.impl.player.data.DefaultRespawnPos
import keno.piecedgenlib.impl.player.data.PGLDataAttachments
import net.minecraft.registry.RegistryKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@Suppress("UnstableApiUsage")
object PlayerSpawnManipulator {
    @JvmStatic
    @JvmOverloads
    fun changePlayerDefaultSpawn(player: ServerPlayerEntity, spawnPos: BlockPos? = player.spawnPointPosition,
                                 dimensionKey: RegistryKey<World>? = player.spawnPointDimension) {
        val mutableSpawnPos: BlockPos = spawnPos ?: BlockPos(0,0,0)
        val mutableDimensionKey: RegistryKey<World> = dimensionKey ?: player.spawnPointDimension

        player.setAttached(PGLDataAttachments.defaultRespawnPos, DefaultRespawnPos(mutableSpawnPos, mutableDimensionKey.value))
    }

    @JvmStatic
    fun initializeAllowedDimensions(player: ServerPlayerEntity, dimensionIds: List<Identifier>) {
        player.setAttached(PGLDataAttachments.allowedRespawnDimensions, AllowedRespawnDimensions(dimensionIds))
    }

    @JvmStatic
    fun addAllowedDimension(player: ServerPlayerEntity, dimensionId: Identifier) {
        player.modifyAttached(PGLDataAttachments.allowedRespawnDimensions) { allowedDimensions: AllowedRespawnDimensions
            -> allowedDimensions.addDimension(dimensionId)
        }
    }

    @JvmStatic
    fun removeAllowedDimension(player: ServerPlayerEntity, dimensionId: Identifier) {
        player.modifyAttached(PGLDataAttachments.allowedRespawnDimensions) { allowedDimensions: AllowedRespawnDimensions
            -> allowedDimensions.removeDimension(dimensionId)
        }
    }
}