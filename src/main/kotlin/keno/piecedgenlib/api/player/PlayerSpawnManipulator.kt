package keno.piecedgenlib.api.player

import keno.piecedgenlib.impl.player.data.AllowedRespawnDimensions
import keno.piecedgenlib.impl.player.data.DefaultRespawnPos
import keno.piecedgenlib.impl.player.data.PGLDataAttachments
import net.minecraft.registry.RegistryKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

@Suppress("UnstableApiUsage")
object PlayerSpawnManipulator {
    @JvmStatic
    @JvmOverloads
    fun initializePlayerDefaultSpawn(player: ServerPlayerEntity, spawnPos: BlockPos,
                                     dimensionKey: RegistryKey<World>? = player.spawnPointDimension) {
        changePlayerDefaultSpawn(player, spawnPos, dimensionKey)
        if (dimensionKey != null) {
            initializeAllowedDimensions(player, dimensionKey.value)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun changePlayerDefaultSpawn(player: ServerPlayerEntity, spawnPos: BlockPos,
                                 dimensionKey: RegistryKey<World>? = player.spawnPointDimension) {
        val mutableDimensionKey: RegistryKey<World> = dimensionKey ?: player.spawnPointDimension
        player.setAttached(PGLDataAttachments.defaultRespawnPos, DefaultRespawnPos(spawnPos, mutableDimensionKey.value))
    }

    @JvmStatic
    @JvmOverloads
    fun changePlayerDefaultSpawn(player: ServerPlayerEntity, spawnPos: Vec3d,
                                 dimensionKey: RegistryKey<World>? = player.spawnPointDimension) {
        changePlayerDefaultSpawn(player, BlockPos.ofFloored(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()), dimensionKey)
    }

    @JvmStatic
    fun initializeAllowedDimensions(player: ServerPlayerEntity, dimensionIds: Identifier) {
        player.setAttached(PGLDataAttachments.allowedRespawnDimensions, AllowedRespawnDimensions(listOf(dimensionIds)))
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