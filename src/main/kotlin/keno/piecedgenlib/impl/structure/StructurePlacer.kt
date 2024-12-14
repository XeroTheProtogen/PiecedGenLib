package keno.piecedgenlib.impl.structure

import keno.piecedgenlib.impl.PGLib
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.inventory.Inventory
import net.minecraft.server.MinecraftServer
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.StructureTemplate
import net.minecraft.structure.StructureTemplateManager
import net.minecraft.structure.processor.BlockRotStructureProcessor
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3i
import net.minecraft.util.math.random.Random
import net.minecraft.world.StructureWorldAccess
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**@author Emafire <a href="https://github.com/Emafire003/StructurePlacerAPI">Original</a> */
class StructurePlacer(private val world: StructureWorldAccess, private val templateName: Identifier?,
    private val blockPos: BlockPos, private val mirror: BlockMirror, private val rotation: BlockRotation,
    private val ignoreEntities: Boolean, private val integrity: Float, private val offset: BlockPos) {
    private var size: Vec3i = Vec3i.ZERO

    companion object {
        @JvmStatic
        fun createRandom(seed: Long): Random {
            return if (seed == 0L) Random.create(Util.getMeasuringTimeMs()) else Random.create(seed)
        }
    }

    fun loadStructure(): Boolean {
        if (this.templateName != null && this.world.server != null) {
            val templateManager = world.server!!.structureTemplateManager
            val optional: Optional<StructureTemplate>
            try {
                optional = templateManager.getTemplate(this.templateName)
            } catch (e: InvalidIdentifierException) {
                PGLib.LOGGER.warn("Identifier '$templateName' is invalid")
                return false
            }

            return optional.isPresent && this.place(optional.get())
        }
        PGLib.LOGGER.warn("Identifier '$templateName' is invalid #2")
        return false
    }

    fun loadAndRestoreStructure(restoreTicks: Int): Boolean {
        if (this.templateName != null && world.server != null) {
            val templateManager = world.server!!.structureTemplateManager
            val optional: Optional<StructureTemplate>
            try {
                optional = templateManager.getTemplate(this.templateName)
            } catch (e: InvalidIdentifierException) {
                return false
            }

            optional.ifPresent {template -> this.size = template.size}

            if (!world.isClient) {
                schedulePlacement(restoreTicks, saveFromWorld(world, blockPos.add(offset), size))
            }

            val isPresent: Boolean = optional.isPresent
            val canPlace: Boolean = this.place(optional.get())

            PGLib.LOGGER.info("Optional present: $isPresent\nCan place: $canPlace")

            return isPresent && canPlace
        } else {
            return false
        }
    }

    private fun place(template: StructureTemplate): Boolean {
        try {
            val data: StructurePlacementData = StructurePlacementData().setMirror(this.mirror).setRotation(this.rotation).setIgnoreEntities(ignoreEntities)
            if (this.integrity < 1.0f) {
                data.clearProcessors().addProcessor(BlockRotStructureProcessor(MathHelper.clamp(this.integrity, 0.0f, 1.0f))).setRandom(
                    createRandom(this.world.seed))
            }

            val pos = blockPos.add(offset)
            template.place(world, pos, pos, data, createRandom(this.world.seed), 2)
            unloadStructure()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun unloadStructure() {
        if (this.templateName != null && world.server != null) {
            val templateManager: StructureTemplateManager = world.server!!.structureTemplateManager
            templateManager.unloadTemplate(this.templateName)
        }
    }

    private fun schedulePlacement(ticks: Int, blockInfoList: List<StructureTemplate.StructureBlockInfo>) {
        val counter = AtomicInteger()
        counter.set(0)

        ServerTickEvents.END_SERVER_TICK.register {server: MinecraftServer ->
            run {
                if (counter.get() != -1) {
                    if (counter.get() == ticks) {
                        for (info in blockInfoList) {
                            world.setBlockState(info.pos, info.state, Block.NOTIFY_ALL)

                            if (info.nbt != null) {
                                server.execute {
                                    val blockEntity: BlockEntity? = world.getBlockEntity(info.pos)
                                    if (blockEntity != null) {
                                        if (blockEntity is LootableContainerBlockEntity) {
                                            info.nbt!!.putLong(
                                                "LootTableSeed",
                                                Objects.requireNonNull(blockEntity.getWorld())!!.getRandom().nextLong()
                                            )
                                        }

                                        blockEntity.read(info.nbt, world.registryManager)
                                    }
                                }
                            }
                        }
                        counter.set(-1)

                    } else {
                        counter.getAndIncrement()
                    }
                }
            }
        }
    }

    private fun saveFromWorld(worldAccess: StructureWorldAccess, start: BlockPos, dimensions: Vec3i): List<StructureTemplate.StructureBlockInfo> {
        val blockList: ArrayList<StructureTemplate.StructureBlockInfo> = ArrayList()
        val startTime: Instant = Instant.now()
        PGLib.LOGGER.info("Saving terrain to later restore it...")
        val blockPos: BlockPos = start.add(dimensions).add(-1,-1,-1)
        val minPos = BlockPos(start.x.coerceAtMost(blockPos.x),
            start.y.coerceAtMost(blockPos.y), start.z.coerceAtMost(blockPos.z))
        val maxPos = BlockPos(start.x.coerceAtLeast(blockPos.x),
            start.y.coerceAtLeast(blockPos.y), start.z.coerceAtLeast(blockPos.z))

        BlockPos.iterate(minPos, maxPos).iterator().forEachRemaining {pos: BlockPos ->
            run {
                val savePos = BlockPos(pos.x, pos.y, pos.z)

                val blockEntity: BlockEntity? = world.getBlockEntity(pos)
                val info: StructureTemplate.StructureBlockInfo

                if (blockEntity != null) {
                    val hasInventory: Boolean = Inventory::class.java.isAssignableFrom(blockEntity.javaClass)

                    info = if (hasInventory) {
                        StructureTemplate.StructureBlockInfo(savePos, world.getBlockState(savePos), null)
                    } else {
                        StructureTemplate.StructureBlockInfo(
                            savePos,
                            world.getBlockState(savePos),
                            blockEntity.createNbtWithId(world.registryManager)
                        )
                    }
                } else {
                    info = StructureTemplate.StructureBlockInfo(savePos, world.getBlockState(savePos), null)
                }
                blockList.add(info)
            }
        }

        val endTime: Instant = Instant.now()
        val duration = Duration.between(startTime, endTime)

        PGLib.LOGGER.info("Terrain save! It took: ${duration}ms")
        return blockList.toList()
    }
}