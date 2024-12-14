package keno.piecedgenlib.impl.worldgen.nbt.pieces

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import keno.piecedgenlib.impl.PGLib
import net.minecraft.block.Block
import net.minecraft.server.world.ServerWorld
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.StructureTemplate
import net.minecraft.structure.StructureTemplateManager
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
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
        try {
            if (!worldAccess.isClient) {
                val world: ServerWorld = worldAccess.toServerWorld()
                    val templateManager: StructureTemplateManager = world.server.structureTemplateManager
                    if (templateManager.getTemplate(templateName).isPresent) {
                        val structure: StructureTemplate = templateManager.getTemplate(templateName).get()
                        val random = Random.create(world.seed)
                        val data: StructurePlacementData = StructurePlacementData().setMirror(mirror).setRotation(rotation).setIgnoreEntities(ignoreEntities)
                        structure.place(worldAccess, blockPos, blockPos, data, random, Block.NOTIFY_ALL)
                        return true
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun pieceType(): PieceType<*> {
        return PGLib.NBT_PIECE
    }
}