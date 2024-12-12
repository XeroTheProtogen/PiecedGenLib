package keno.piecedgenlib.impl.worldgen.nbt.pieces

import com.mojang.serialization.Lifecycle
import com.mojang.serialization.MapCodec
import keno.piecedgenlib.impl.PGLib
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry

data class PieceType<out T: AbstractNbtPieceImpl>(val codec: MapCodec<out T>) {
    companion object {
        @JvmField
        val REGISTRY: Registry<PieceType<*>> = SimpleRegistry(
            RegistryKey.ofRegistry(PGLib.modId("nbt_pieces")), Lifecycle.stable())
    }
}
