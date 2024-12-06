package keno.piecedgenlib.impl.player.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance
import com.mojang.serialization.codecs.RecordCodecBuilder.create
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

data class DefaultRespawnPos(val pos: BlockPos, private val dimensionId: Identifier) {
    val dimensionKey: RegistryKey<World> by lazy {
        RegistryKey.of(RegistryKeys.WORLD, dimensionId)
    }

    companion object {
        @JvmStatic
        val codec: Codec<DefaultRespawnPos> = create { instance: Instance<DefaultRespawnPos> ->
            instance.group(
                BlockPos.CODEC.stable().fieldOf("position").forGetter(DefaultRespawnPos::pos),
                Identifier.CODEC.fieldOf("dimensionId").forGetter(DefaultRespawnPos::encryptDimensionKey)
            ).apply(instance, ::DefaultRespawnPos)
        }
    }

    fun changeDimension(id: Identifier): DefaultRespawnPos {
        return DefaultRespawnPos(this.pos, id)
    }

    fun changeDimension(registryKey: RegistryKey<World>): DefaultRespawnPos {
        return changeDimension(registryKey.value)
    }

    fun encryptDimensionKey(): Identifier {
        return dimensionKey.value
    }
}
