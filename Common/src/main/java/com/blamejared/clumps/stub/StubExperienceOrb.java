package com.blamejared.clumps.stub;

import com.blamejared.clumps.ClumpsCommon;
import com.blamejared.clumps.helper.IClumpedOrb;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// The actual mixin needs to be done in each project, so it can actually be recognized and have a refmap generated.
// I put very minimal effort into actually getting it to work, the issue is that MixinGradle requires ForgeGradle,
// so it isn't easy to just make it use the common sourceset.
// If anyone knows how to get around this, please let me know.

// So lets just make a stub to hold the logic.
public class StubExperienceOrb {
    
    public static void canMerge(ExperienceOrb thisOrb, ExperienceOrb experienceOrb, CallbackInfoReturnable<Boolean> cir) {
        
        cir.setReturnValue(experienceOrb.isAlive() && !thisOrb.is(experienceOrb));
    }
    
    private static boolean canMerge(ExperienceOrb experienceOrb, int i, int j) {
        
        return experienceOrb.isAlive();
    }
    
    public static void canMerge(ExperienceOrb experienceOrb, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        
        cir.setReturnValue(canMerge(experienceOrb, i, j));
    }
    
    public static void playerTouch(ExperienceOrb thisOrb, Player player, CallbackInfo ci) {
        
        if(!thisOrb.level.isClientSide) {
            // Fire the Forge event
            if(ClumpsCommon.orbHelper.fireXPPickup(player, thisOrb)) {
                return;
            }
            player.takeXpDelay = 0;
            player.take(thisOrb, 1);
            
            ((IClumpedOrb) thisOrb).clumps$getClumpedMap().forEach((value, amount) -> {
                for(int i = 0; i < amount; i++) {
                    int leftOver = ClumpsCommon.orbHelper.repairPlayerItems(thisOrb, player, value);
                    if(leftOver > 0) {
                        player.giveExperiencePoints(leftOver);
                    }
                }
            });
            
            thisOrb.discard();
            ci.cancel();
        }
        
    }
    
    public static void merge(ExperienceOrb thisOrb, ExperienceOrb secondaryOrb, CallbackInfo ci) {
        
        Map<Integer, Integer> otherMap = ((IClumpedOrb) secondaryOrb).clumps$getClumpedMap();
        ((IClumpedOrb) thisOrb).clumps$setClumpedMap(Stream.of(((IClumpedOrb) thisOrb).clumps$getClumpedMap(), otherMap)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum)));
        ClumpsCommon.orbHelper.setCount(thisOrb, ((IClumpedOrb) thisOrb).clumps$getClumpedMap()
                .values()
                .stream()
                .reduce(Integer::sum)
                .orElse(1));
        ClumpsCommon.orbHelper.setAge(thisOrb, Math.min(ClumpsCommon.orbHelper.getAge(thisOrb), ClumpsCommon.orbHelper.getAge(secondaryOrb)));
        secondaryOrb.discard();
        ci.cancel();
    }
    
    public static void tryMergeToExisting(ServerLevel serverLevel, Vec3 vec3, int i, CallbackInfoReturnable<Boolean> cir) {
        
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
            ClumpsCommon.orbHelper.setCount(experienceOrb, clumpedMap.values().stream().reduce(Integer::sum).orElse(1));
            ClumpsCommon.orbHelper.setAge(experienceOrb, 0);
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }
    
    public static void addAdditionalSaveData(ExperienceOrb thisOrb, CompoundTag compoundTag, CallbackInfo ci) {
        
        CompoundTag map = new CompoundTag();
        ((IClumpedOrb) thisOrb).clumps$getClumpedMap().forEach((value, count) -> map.putInt(value + "", count));
        compoundTag.put("clumpedMap", map);
    }
    
    public static void readAdditionalSaveData(ExperienceOrb thisOrb, CompoundTag compoundTag, CallbackInfo ci) {
        
        Map<Integer, Integer> map = new HashMap<>();
        if(compoundTag.contains("clumpedMap")) {
            CompoundTag clumpedMap = compoundTag.getCompound("clumpedMap");
            for(String s : clumpedMap.getAllKeys()) {
                map.put(Integer.parseInt(s), clumpedMap.getInt(s));
            }
        } else {
            map.put(ClumpsCommon.orbHelper.getValue(thisOrb), ClumpsCommon.orbHelper.getCount(thisOrb));
        }
        
        ((IClumpedOrb) thisOrb).clumps$setClumpedMap(map);
    }
    
    
}
