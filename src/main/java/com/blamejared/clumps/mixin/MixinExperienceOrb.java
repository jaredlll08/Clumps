package com.blamejared.clumps.mixin;

import com.blamejared.clumps.IClumpedOrb;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;
import java.util.stream.*;

@Mixin(ExperienceOrb.class)
public abstract class MixinExperienceOrb extends Entity implements IClumpedOrb {
    
    @Shadow
    private int count;
    
    @Shadow
    private int age;
    
    @Shadow
    private int value;
    
    @Shadow
    protected abstract int repairPlayerItems(Player player, int i);
    
    @Shadow
    private static boolean canMerge(ExperienceOrb experienceOrb, int i, int j) {
        
        return false;
    }
    
    @Unique
    public Map<Integer, Integer> clumps$clumpedMap;
    
    public MixinExperienceOrb(EntityType<?> entityType, Level level) {
        
        super(entityType, level);
    }
    
    @Inject(method = "canMerge(Lnet/minecraft/world/entity/ExperienceOrb;)Z", at = @At("HEAD"), cancellable = true)
    private void canMerge(ExperienceOrb experienceOrb, CallbackInfoReturnable<Boolean> cir) {
        
        cir.setReturnValue(experienceOrb.isAlive() && !this.is(experienceOrb));
    }
    
    @Inject(method = "canMerge(Lnet/minecraft/world/entity/ExperienceOrb;II)Z", at = @At("HEAD"), cancellable = true)
    private static void canMerge(ExperienceOrb experienceOrb, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        
        cir.setReturnValue(experienceOrb.isAlive());
    }
    
    @Inject(method = "playerTouch(Lnet/minecraft/world/entity/player/Player;)V", at = @At(value = "HEAD"), cancellable = true)
    public void playerTouch(Player player, CallbackInfo ci) {
        
        if(!this.level.isClientSide) {
            player.takeXpDelay = 0;
            player.take(this, 1);
            
            clumps$getClumpedMap().forEach((value, amount) -> {
                for(int i = 0; i < amount; i++) {
                    int leftOver = this.repairPlayerItems(player, value);
                    if(leftOver > 0) {
                        player.giveExperiencePoints(leftOver);
                    }
                }
            });
            
            this.discard();
            ci.cancel();
        }
        
    }
    
    @Inject(method = "merge(Lnet/minecraft/world/entity/ExperienceOrb;)V", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/ExperienceOrb.discard()V", shift = At.Shift.BEFORE), cancellable = true)
    public void merge(ExperienceOrb experienceOrb, CallbackInfo ci) {
        
        Map<Integer, Integer> otherMap = ((IClumpedOrb) experienceOrb).clumps$getClumpedMap();
        clumps$clumpedMap = Stream.of(clumps$getClumpedMap(), otherMap)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum));
        this.count = clumps$getClumpedMap().values().stream().reduce(Integer::sum).orElse(1);
        this.age = Math.min(this.age, ((ExperienceOrbAccess) experienceOrb).getAge());
        experienceOrb.discard();
        ci.cancel();
    }
    
    
    @Inject(method = "tryMergeToExisting(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)Z", at = @At(value = "HEAD"), cancellable = true)
    private static void tryMergeToExisting(ServerLevel serverLevel, Vec3 vec3, int i, CallbackInfoReturnable<Boolean> cir) {
        
        AABB aABB = AABB.ofSize(vec3, 1.0D, 1.0D, 1.0D);
        int j = serverLevel.getRandom().nextInt(40);
        List<ExperienceOrb> list = serverLevel.getEntities(EntityTypeTest.forClass(ExperienceOrb.class), aABB, (experienceOrbx) -> canMerge(experienceOrbx, j, i));
        if(!list.isEmpty()) {
            ExperienceOrb experienceOrb = list.get(0);
            Map<Integer, Integer> clumpedMap = ((IClumpedOrb) experienceOrb).clumps$getClumpedMap();
            clumpedMap = Stream.of(clumpedMap, Collections.singletonMap(i, 1))
                    .flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum));
            ((IClumpedOrb) experienceOrb).clumps$setClumpedMap(clumpedMap);
            ((ExperienceOrbAccess) experienceOrb).setCount(clumpedMap.values().stream().reduce(Integer::sum).orElse(1));
            ((ExperienceOrbAccess) experienceOrb).setAge(0);
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }
    
    
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        
        CompoundTag map = new CompoundTag();
        clumps$getClumpedMap().forEach((value, count) -> {
            map.putInt(value + "", count);
        });
        compoundTag.put("clumpedMap", map);
    }
    
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        
        Map<Integer, Integer> map = new HashMap<>();
        if(compoundTag.contains("clumpedMap")) {
            CompoundTag clumpedMap = compoundTag.getCompound("clumpedMap");
            for(String s : clumpedMap.getAllKeys()) {
                map.put(Integer.parseInt(s), clumpedMap.getInt(s));
            }
        } else {
            map.put(value, count);
        }
        
        clumps$setClumpedMap(map);
    }
    
    
    @Override
    public Map<Integer, Integer> clumps$getClumpedMap() {
        
        if(clumps$clumpedMap == null) {
            clumps$clumpedMap = new HashMap<>();
            clumps$clumpedMap.put(this.value, 1);
        }
        return clumps$clumpedMap;
    }
    
    @Override
    public void clumps$setClumpedMap(Map<Integer, Integer> map) {
        
        clumps$clumpedMap = map;
    }
    
}
