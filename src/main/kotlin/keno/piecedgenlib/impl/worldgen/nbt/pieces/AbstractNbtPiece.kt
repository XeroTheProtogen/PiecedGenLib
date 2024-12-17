package keno.piecedgenlib.impl.worldgen.nbt.pieces

import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.StructureWorldAccess

abstract class AbstractNbtPiece(private val templateName: Identifier?,
                                private val mirror: BlockMirror, private val rotation: BlockRotation,
                                private val ignoreEntities: Boolean, private val integrity: Float): AbstractNbtPieceImpl {
    override fun getPieceType(): PieceType<*> {
        return pieceType()
    }

    abstract fun placeNbtPiece(worldAccess: StructureWorldAccess, blockPos: BlockPos, offset: BlockPos?): Boolean

    abstract fun pieceType(): PieceType<*>
}