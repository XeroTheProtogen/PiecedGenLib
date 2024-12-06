package keno.piecedgenlib.mixin;

import keno.piecedgenlib.impl.player.data.DefaultRespawnPos;
import keno.piecedgenlib.impl.player.data.PGLDataAttachments;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Unique
    public boolean playerLacksData(ServerPlayerEntity player) {
        return !player.hasAttached(PGLDataAttachments.getDefaultRespawnPos()) && !player.hasAttached(PGLDataAttachments.getAllowedRespawnDimensions());
    }

    @Inject(method = "getWorldSpawnPos",
    at = @At("HEAD"), cancellable = true)
    public void PGLib$getWorldSpawnPos(ServerWorld world, BlockPos basePos, CallbackInfoReturnable<BlockPos> cir) {
        if (!world.isClient()) {
            if ((Entity) (Object) this instanceof ServerPlayerEntity player) {
                if (!playerLacksData(player)) {
                    DefaultRespawnPos pos = player.getAttached(PGLDataAttachments.getDefaultRespawnPos());
                    cir.setReturnValue(pos.getPos());
                }
            }
        }
    }
}
