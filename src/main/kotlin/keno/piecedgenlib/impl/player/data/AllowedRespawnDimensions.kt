package keno.piecedgenlib.impl.player.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance
import com.mojang.serialization.codecs.RecordCodecBuilder.create
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.World

class AllowedRespawnDimensions(private val dimensionIds: List<Identifier>) {
    private val dimensions: ArrayList<RegistryKey<World>> by lazy {
        val arrayList: ArrayList<RegistryKey<World>> = ArrayList()
        for (id in dimensionIds) {
            val dimensionKey : RegistryKey<World> = RegistryKey.of(RegistryKeys.WORLD, id)
            arrayList.add(dimensionKey)
        }

        return@lazy arrayList
    }

    companion object {
        @JvmStatic
        val codec: Codec<AllowedRespawnDimensions> = create { instance : Instance<AllowedRespawnDimensions> ->
            instance.group(
                Identifier.CODEC.listOf().fieldOf("dimensions").forGetter(AllowedRespawnDimensions::encryptDimensions)
            ).apply(instance, ::AllowedRespawnDimensions)
        }
    }

    fun isDimensionAllowed(dimensionId: Identifier): Boolean {
        return isDimensionAllowed(RegistryKey.of(RegistryKeys.WORLD, dimensionId))
    }

    fun isDimensionAllowed(dimensionKey: RegistryKey<World>): Boolean {
        return dimensions.contains(dimensionKey)
    }

    fun encryptDimensions(): List<Identifier> {
        val identifiers: ArrayList<Identifier> = ArrayList()
        for (key in dimensions) {
            val id: Identifier = key.value
            identifiers.add(id)
        }
        return identifiers
    }

    fun addDimension(dimensionKey: RegistryKey<World>): AllowedRespawnDimensions {
        if (!dimensions.contains(dimensionKey)) {
            dimensions.add(dimensionKey)
        }
        return this
    }

    fun addDimension(dimensionId: Identifier): AllowedRespawnDimensions {
        val dimensionKey = RegistryKey.of(RegistryKeys.WORLD, dimensionId)
        return addDimension(dimensionKey)
    }

    fun removeDimension(dimensionKey: RegistryKey<World>): AllowedRespawnDimensions {
        dimensions.remove(dimensionKey)
        return this
    }

    fun removeDimension(dimensionId: Identifier): AllowedRespawnDimensions {
        val dimensionKey = RegistryKey.of(RegistryKeys.WORLD, dimensionId)
        return removeDimension(dimensionKey)
    }
}
