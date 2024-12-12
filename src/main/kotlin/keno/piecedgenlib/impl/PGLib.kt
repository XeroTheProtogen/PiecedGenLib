package keno.piecedgenlib.impl

import keno.piecedgenlib.impl.player.data.PGLDataAttachments
import keno.piecedgenlib.impl.worldgen.nbt.pieces.AbstractNbtPieceImpl
import keno.piecedgenlib.impl.worldgen.nbt.pieces.NbtPiece
import keno.piecedgenlib.impl.worldgen.nbt.pieces.PieceType
import net.fabricmc.api.ModInitializer
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object PGLib : ModInitializer {
	const val MODID : String = "piecedgenlib"
	val LOGGER: Logger = LoggerFactory.getLogger(MODID)

	val NBT_PIECE: PieceType<NbtPiece> = register(modId("nbt_piece"), PieceType(NbtPiece.CODEC))

	fun <T: AbstractNbtPieceImpl>register(id: Identifier, pieceType: PieceType<T>): PieceType<T> {
		return Registry.register(PieceType.REGISTRY, id, pieceType)
	}

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