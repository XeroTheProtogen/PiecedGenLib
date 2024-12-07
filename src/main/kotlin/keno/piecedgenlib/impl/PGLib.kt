package keno.piecedgenlib.impl

import keno.piecedgenlib.api.player.PlayerSpawnManipulator
import keno.piecedgenlib.impl.player.data.PGLDataAttachments
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object PGLib : ModInitializer {
	const val MODID : String = "piecedgenlib"
	val LOGGER: Logger = LoggerFactory.getLogger(MODID)

	fun modId(path: String): Identifier {
		return Identifier.of(MODID, path)
	}

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("What are we doing in this library? Piecing generation or something?")
		PGLDataAttachments.init()

		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register {player: ServerPlayerEntity, _: ServerWorld, destination: ServerWorld ->
			if (destination.registryKey.equals(World.NETHER)) {
				if (!player.hasAttached(PGLDataAttachments.defaultRespawnPos)) {
					val playerPos: Vec3d = player.pos
					PlayerSpawnManipulator.changePlayerDefaultSpawn(
						player,
						playerPos,
						destination.registryKey
					)

					LOGGER.info("{} {} {}", playerPos.getX() + 2, playerPos.getY(), playerPos.getZ() + 2)

					val allowedDimensions: List<Identifier> = listOf(World.NETHER.value)
					PlayerSpawnManipulator.initializeAllowedDimensions(player, allowedDimensions)
				}
			}
		}
	}
}