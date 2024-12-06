package keno.piecedgenlib.impl

import keno.piecedgenlib.impl.player.data.PGLDataAttachments
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
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
	}
}