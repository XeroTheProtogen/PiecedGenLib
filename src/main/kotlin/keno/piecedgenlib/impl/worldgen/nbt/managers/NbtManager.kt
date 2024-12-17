package keno.piecedgenlib.impl.worldgen.nbt.managers

import keno.piecedgenlib.impl.worldgen.nbt.pieces.NbtPiece
import net.minecraft.util.Identifier
import net.minecraft.util.InvalidIdentifierException
import net.minecraft.util.math.BlockPos
import net.minecraft.world.StructureWorldAccess
import kotlin.random.Random

open class NbtManager(private val nbtPieces: HashMap<Identifier, ArrayList<NbtPiece>>) {
    fun addNbtPool(poolId: Identifier, pool: ArrayList<NbtPiece>): Boolean {
        if (!nbtPieces.containsKey(poolId)) {
            nbtPieces[poolId] = pool
            return true
        }
        return false
    }

    fun addNbtPiece(poolId: Identifier, piece: NbtPiece): Boolean {
        if (nbtPieces.containsKey(poolId)) {
            nbtPieces[poolId]!!.add(piece)
            return true
        }
        return false
    }

    @JvmOverloads
    fun placeRandomNbtPiece(world: StructureWorldAccess, seed: Long = world.seed, poolId: Identifier,
                            pos: BlockPos, offset: BlockPos? = BlockPos(0,0,0)): Boolean {
        val nbtPiece: NbtPiece = getRandomNbtPiece(poolId, seed)
        return nbtPiece.placeNbtPiece(world, pos, offset)
    }

    fun getRandomNbtPiece(poolId: Identifier, seed: Long): NbtPiece {
        if (!nbtPieces.containsKey(poolId)) {
            throw InvalidIdentifierException("This NbtManager doesn't have a piece pool mapped to $poolId")
        }

        val list = nbtPieces[poolId]
        val rand = Random(seed)

        return list!![rand.nextInt(0, list.size)]
    }
}