package com.blamejared.clumps.mixin;

import com.blamejared.clumps.helper.IClumpedOrb;
import com.blamejared.clumps.stub.StubExperienceOrb;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(ExperienceOrb.class)
public abstract class MixinExperienceOrb extends Entity implements IClumpedOrb {
    
    @Unique
    public Map<Integer, Integer> clumps$clumpedMap;
    
    public MixinExperienceOrb(EntityType<?> entityType, Level level) {
        
        super(entityType, level);
    }
    
    @Inject(method = "canMerge(Lnet/minecraft/world/entity/ExperienceOrb;)Z", at = @At("HEAD"), cancellable = true)
    private void canMerge(ExperienceOrb experienceOrb, CallbackInfoReturnable<Boolean> cir) {
        
        StubExperienceOrb.canMerge(asOrb(), experienceOrb, cir);
    }
    
    @Inject(method = "canMerge(Lnet/minecraft/world/entity/ExperienceOrb;II)Z", at = @At("HEAD"), cancellable = true)
    private static void canMerge(ExperienceOrb experienceOrb, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        
        StubExperienceOrb.canMerge(experienceOrb, i, j, cir);
    }
    
    @Inject(method = "playerTouch(Lnet/minecraft/world/entity/player/Player;)V", at = @At(value = "HEAD"), cancellable = true)
    public void playerTouch(Player player, CallbackInfo ci) {
        
        StubExperienceOrb.playerTouch(asOrb(), player, ci);
        
    }
    
    @Inject(method = "merge(Lnet/minecraft/world/entity/ExperienceOrb;)V", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/ExperienceOrb.discard()V", shift = At.Shift.BEFORE), cancellable = true)
    public void merge(ExperienceOrb secondaryOrb, CallbackInfo ci) {
        
        StubExperienceOrb.merge(asOrb(), secondaryOrb, ci);
    }
    
    
    @Inject(method = "tryMergeToExisting(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)Z", at = @At(value = "HEAD"), cancellable = true)
    private static void tryMergeToExisting(ServerLevel serverLevel, Vec3 vec3, int i, CallbackInfoReturnable<Boolean> cir) {
        
        StubExperienceOrb.tryMergeToExisting(serverLevel, vec3, i, cir);
    }
    
    
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        
        StubExperienceOrb.addAdditionalSaveData(asOrb(), compoundTag, ci);
    }
    
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        
        StubExperienceOrb.readAdditionalSaveData(asOrb(), compoundTag, ci);
    }
    
    @Override
    public Map<Integer, Integer> clumps$getClumpedMap() {
        
        if(clumps$clumpedMap == null) {
            clumps$clumpedMap = new HashMap<>();
            clumps$clumpedMap.put(((ExperienceOrbAccess) this).getValue(), 1);
        }
        return clumps$clumpedMap;
    }
    
    @Override
    public void clumps$setClumpedMap(Map<Integer, Integer> map) {
        
        clumps$clumpedMap = map;
    }
    
    private ExperienceOrb asOrb() {
        
        return (ExperienceOrb) (Entity) this;
    }
    
}
