package keno.piecedgenlib.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.authlib.GameProfile;
import keno.piecedgenlib.impl.player.data.AllowedRespawnDimensions;
import keno.piecedgenlib.impl.player.data.DefaultRespawnPos;
import keno.piecedgenlib.impl.player.data.PGLDataAttachments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnstableApiUsage")
@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Shadow
    private BlockPos spawnPointPosition;

    @Shadow
    private RegistryKey<World> spawnPointDimension;

    @Final
    @Shadow
    public MinecraftServer server;

    @Shadow public abstract void sendMessageToClient(Text message, boolean overlay);

    @Unique
    public boolean PGLib$lacksData() {
        return !hasAttached(PGLDataAttachments.getDefaultRespawnPos()) && !hasAttached(PGLDataAttachments.getAllowedRespawnDimensions());
    }

    @Unique
    public boolean PGLib$lacksData(ServerPlayerEntity player) {
        return !player.hasAttached(PGLDataAttachments.getDefaultRespawnPos()) && !player.hasAttached(PGLDataAttachments.getAllowedRespawnDimensions());
    }

    @Inject(method = "setSpawnPoint",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;equals(Ljava/lang/Object;)Z",
            shift = At.Shift.BEFORE), cancellable = true)
    public void PGLib$setSpawnPoint(RegistryKey<World> dimension, BlockPos pos, float angle,
                                     boolean forced, boolean sendMessage, CallbackInfo ci) {
        if (!PGLib$lacksData()) {
            AllowedRespawnDimensions allowedRespawnDimensions = getAttached(PGLDataAttachments.getAllowedRespawnDimensions());
            if (!allowedRespawnDimensions.isDimensionAllowed(dimension) && !forced) {
                sendMessageToClient(Text.translatable("piecedgenlib.cannot_set_spawn"), true);
                ci.cancel();
            }
        }
    }


    @Definition(id = "spawnPointDimension", field = "Lnet/minecraft/server/network/ServerPlayerEntity;spawnPointDimension:Lnet/minecraft/registry/RegistryKey;")
    @Definition(id = "OVERWORLD", field = "Lnet/minecraft/world/World;OVERWORLD:Lnet/minecraft/registry/RegistryKey;")
    @Expression("this.spawnPointDimension = OVERWORLD")
    @WrapWithCondition(method = "setSpawnPoint", at = @At("MIXINEXTRAS:EXPRESSION"))
    public boolean PGL$checkSpawnPointDimension(ServerPlayerEntity instance, RegistryKey<World> value) {
        if (PGLib$lacksData(instance)) {
               return true;
        }
        AllowedRespawnDimensions dimensions = instance.getAttached(PGLDataAttachments.getAllowedRespawnDimensions());
        if (dimensions.isDimensionAllowed(value) && dimensions.isDimensionAllowed(World.OVERWORLD)) {
            return true;
        }

        DefaultRespawnPos pos = instance.getAttached(PGLDataAttachments.getDefaultRespawnPos());

        instance.sendMessageToClient(Text.translatable("piecedgenlib.set_to_default_spawn"), false);
        this.spawnPointDimension = pos.getDimensionKey();

        return false;
    }

    @Definition(id = "spawnPointPosition", field = "Lnet/minecraft/server/network/ServerPlayerEntity;spawnPointPosition:Lnet/minecraft/util/math/BlockPos;")
    @Expression("this.spawnPointPosition = null")
    @WrapWithCondition(method = "setSpawnPoint", at = @At("MIXINEXTRAS:EXPRESSION"))
    public boolean PGLib$checkSpawnPointPosition(ServerPlayerEntity instance, BlockPos value) {
        if (PGLib$lacksData(instance)) {
            return true;
        }

        DefaultRespawnPos pos = instance.getAttached(PGLDataAttachments.getDefaultRespawnPos());

        this.spawnPointPosition = pos.getPos();
        return false;
    }

    @ModifyArg(method = "getRespawnTarget",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TeleportTarget;missingSpawnBlock(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/world/TeleportTarget$PostDimensionTransition;)Lnet/minecraft/world/TeleportTarget;"),
    index = 0)
    private ServerWorld PGLib$changeToDefaultWorld(ServerWorld world) {
        if (PGLib$lacksData()) {
            return world;
        }

        AllowedRespawnDimensions dimensions = getAttached(PGLDataAttachments.getAllowedRespawnDimensions());
        if (dimensions.isDimensionAllowed(world.getRegistryKey())) {
            return world;
        }
        DefaultRespawnPos defaultRespawnPos = getAttached(PGLDataAttachments.getDefaultRespawnPos());
        return this.server.getWorld(defaultRespawnPos.getDimensionKey());
    }

    @ModifyReturnValue(method = "getRespawnTarget",
    at = @At(value = "RETURN", ordinal = 2))
    public TeleportTarget PGLib$changeToDefaultWorld2(TeleportTarget original) {
        if (PGLib$lacksData()) {
            return original;
        }
        RegistryKey<World> worldKey = original.world().getRegistryKey();
        AllowedRespawnDimensions dimensions = getAttached(PGLDataAttachments.getAllowedRespawnDimensions());
        if (dimensions.isDimensionAllowed(worldKey)) {
            return original;
        }
        DefaultRespawnPos pos = getAttached(PGLDataAttachments.getDefaultRespawnPos());
        return new TeleportTarget(this.server.getWorld(pos.getDimensionKey()), (ServerPlayerEntity)(Object)this, original.postDimensionTransition());
    }
}
