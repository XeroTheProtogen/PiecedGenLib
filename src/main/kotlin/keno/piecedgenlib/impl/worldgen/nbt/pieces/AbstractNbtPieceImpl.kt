package keno.piecedgenlib.impl.worldgen.nbt.pieces

import com.mojang.serialization.Codec

interface AbstractNbtPieceImpl {
    companion object {
        @JvmField
        val CODEC: Codec<AbstractNbtPieceImpl> = PieceType.REGISTRY.codec.dispatch("piece", AbstractNbtPieceImpl::getPieceType,
            PieceType<*>::codec)
    }

    fun getPieceType(): PieceType<*>
}