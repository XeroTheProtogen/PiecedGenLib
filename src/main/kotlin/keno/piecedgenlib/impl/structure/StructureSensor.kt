package keno.piecedgenlib.impl.structure

import net.minecraft.predicate.entity.LocationPredicate
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.gen.structure.Structure

typealias StructureType = RegistryEntry<Structure>

/** https://github.com/Emafire003/StructurePlacerAPI */
object StructureSensor {
    @JvmStatic
    fun isStructurePresent(serverWorld: ServerWorld, blockPos: BlockPos, structure: StructureType): Boolean {
        return isStructurePresent(serverWorld, blockPos.toCenterPos(), structure)
    }

    @JvmStatic
    fun isStructurePresent(serverWorld: ServerWorld, vec3d: Vec3d, structure: StructureType): Boolean {
        return LocationPredicate.Builder.createStructure(structure).build().test(serverWorld, vec3d.getX(), vec3d.getY(), vec3d.getZ())
    }
}