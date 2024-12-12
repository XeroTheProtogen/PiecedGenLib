package keno.piecedgenlib.impl.worldgen.nbt.pieces

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import keno.piecedgenlib.impl.PGLib
import keno.piecedgenlib.impl.structure.StructurePlacer
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.StructureWorldAccess

class NbtPiece(private val templateName: Identifier,
               private val mirror: BlockMirror, private val rotation: BlockRotation,
               private val ignoreEntities: Boolean, private val integrity: Float,
               private val offset: BlockPos): AbstractNbtPiece(templateName, mirror, rotation, ignoreEntities, integrity) {
    companion object {
            @JvmField
            val CODEC: MapCodec<NbtPiece> = RecordCodecBuilder.mapCodec { instance -> instance.group(
                Identifier.CODEC.fieldOf("template_id").forGetter(NbtPiece::templateName),
                BlockMirror.CODEC.optionalFieldOf("mirror", BlockMirror.NONE).forGetter(NbtPiece::mirror),
                BlockRotation.CODEC.optionalFieldOf("rotation", BlockRotation.NONE).forGetter(NbtPiece::rotation),
                Codec.BOOL.optionalFieldOf("ignore_entities", true).forGetter(NbtPiece::ignoreEntities),
                Codec.FLOAT.optionalFieldOf("integrity", 1.0f).forGetter(NbtPiece::integrity),
                BlockPos.CODEC.optionalFieldOf("offset", BlockPos(0,0,0)).forGetter(NbtPiece::offset)
            ).apply(instance, ::NbtPiece)}
    }

    override fun placeNbtPiece(worldAccess: StructureWorldAccess, blockPos: BlockPos, offset: BlockPos): Boolean {
        val placer = StructurePlacer(worldAccess, templateName, blockPos, mirror, rotation, ignoreEntities, integrity, offset)
        return placer.loadStructure()
    }

    override fun pieceType(): PieceType<*> {
        return PGLib.NBT_PIECE
    }
}