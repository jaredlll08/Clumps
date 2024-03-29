package com.blamejared.clumps.mixin;

import com.blamejared.clumps.ClumpsCommon;
import com.blamejared.clumps.api.events.IRepairEvent;
import com.blamejared.clumps.api.events.IValueEvent;
import com.blamejared.clumps.helper.IClumpedOrb;
import com.blamejared.clumps.platform.Services;
import com.mojang.datafixers.util.Either;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(value = ExperienceOrb.class, priority = 1001)
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
    private static boolean canMerge(ExperienceOrb experienceOrb, int id, int value) {
        
        return false;
    }
    
    @Shadow
    protected abstract int xpToDurability(int $$0);
    
    @Shadow
    protected abstract int durabilityToXp(int $$0);
    
    @Unique
    public Map<Integer, Integer> clumps$clumpedMap;
    
    @Unique
    public Map.Entry<EquipmentSlot, ItemStack> clumps$currentEntry;
    
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
        
        if(!this.level().isClientSide) {
            // Fire the Forge event
            if(ClumpsCommon.pickupXPEvent.test(player, (ExperienceOrb) (Entity) this)) {
                return;
            }
            player.takeXpDelay = 0;
            player.take(this, 1);
            
            if(this.value != 0 || clumps$resolve()) {
                AtomicInteger toGive = new AtomicInteger();
                clumps$getClumpedMap().forEach((value, amount) -> {
                    Either<IValueEvent, Integer> result = Services.EVENT.fireValueEvent(player, value);
                    int actualValue = result.map(IValueEvent::getValue, UnaryOperator.identity());
                    
                    for(int i = 0; i < amount; i++) {
                        int leftOver = Services.EVENT.fireRepairEvent(player, actualValue)
                                .map(IRepairEvent::getValue, UnaryOperator.identity());
                        if(leftOver == actualValue) {
                            leftOver = this.repairPlayerItems(player, actualValue);
                        }
                        if(leftOver > 0) {
                            toGive.addAndGet(leftOver);
                        }
                    }
                });
                if(toGive.get() > 0) {
                    player.giveExperiencePoints(toGive.get());
                }
            }
            this.discard();
            ci.cancel();
        }
    }
    
    @ModifyVariable(index = 3, method = "repairPlayerItems", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getRandomItemWith(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Predicate;)Ljava/util/Map$Entry;"))
    public Map.Entry<EquipmentSlot, ItemStack> clumps$captureCurrentEntry(Map.Entry<EquipmentSlot, ItemStack> entry) {
        
        clumps$currentEntry = entry;
        return entry;
    }
    
    @Inject(method = "repairPlayerItems", cancellable = true, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getRandomItemWith(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Predicate;)Ljava/util/Map$Entry;"))
    public void clumps$repairPlayerItems(Player player, int actualValue, CallbackInfoReturnable<Integer> cir) {
        
        cir.setReturnValue(Optional.ofNullable(clumps$currentEntry)
                .map(Map.Entry::getValue)
                .map(foundItem -> {
                    BiFunction<ItemStack, Integer, Float> repairRatio = Services.PLATFORM.getRepairRatio((itemStack, integer) -> (float) this.xpToDurability(integer));
                    int toRepair = Math.min(repairRatio.apply(foundItem, actualValue)
                            .intValue(), foundItem.getDamageValue());
                    foundItem.setDamageValue(foundItem.getDamageValue() - toRepair);
                    int used = actualValue - this.durabilityToXp(toRepair);
                    return used > 0 ? this.repairPlayerItems(player, used) : 0;
                })
                .orElse(actualValue));
    }
    
    @Inject(method = "merge(Lnet/minecraft/world/entity/ExperienceOrb;)V", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/ExperienceOrb.discard()V", shift = At.Shift.BEFORE), cancellable = true)
    public void merge(ExperienceOrb secondaryOrb, CallbackInfo ci) {
        
        Map<Integer, Integer> otherMap = ((IClumpedOrb) secondaryOrb).clumps$getClumpedMap();
        this.count = clumps$getClumpedMap().values().stream().reduce(Integer::sum).orElse(1);
        this.age = Math.min(this.age, ((ExperienceOrbAccess) secondaryOrb).clumps$getAge());
        clumps$setClumpedMap(Stream.of(clumps$getClumpedMap(), otherMap)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum)));
        secondaryOrb.discard();
        ci.cancel();
    }
    
    
    @Inject(method = "tryMergeToExisting(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)Z", at = @At(value = "HEAD"), cancellable = true)
    private static void tryMergeToExisting(ServerLevel serverLevel, Vec3 vec3, int value, CallbackInfoReturnable<Boolean> cir) {
        
        AABB aABB = AABB.ofSize(vec3, 1.0D, 1.0D, 1.0D);
        int id = serverLevel.getRandom().nextInt(40);
        List<ExperienceOrb> list = serverLevel.getEntities(EntityTypeTest.forClass(ExperienceOrb.class), aABB, (experienceOrbx) -> canMerge(experienceOrbx, id, value));
        if(!list.isEmpty()) {
            ExperienceOrb experienceOrb = list.get(0);
            Map<Integer, Integer> clumpedMap = ((IClumpedOrb) experienceOrb).clumps$getClumpedMap();
            ((IClumpedOrb) experienceOrb).clumps$setClumpedMap(Stream.of(clumpedMap, Collections.singletonMap(value, 1))
                    .flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum)));
            ((ExperienceOrbAccess) experienceOrb).clumps$setCount(clumpedMap.values()
                    .stream()
                    .reduce(Integer::sum)
                    .orElse(1));
            ((ExperienceOrbAccess) experienceOrb).clumps$setAge(0);
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }
    
    
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        
        if(clumps$clumpedMap != null) {
            CompoundTag map = new CompoundTag();
            clumps$getClumpedMap().forEach((value, count) -> map.putInt(String.valueOf(value), count));
            compoundTag.put("clumpedMap", map);
        }
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
        clumps$resolve();
    }
    
    @Override
    public boolean clumps$resolve() {
        
        value = clumps$getClumpedMap().entrySet()
                .stream()
                .map(entry -> entry.getKey() * entry.getValue())
                .reduce(Integer::sum)
                .orElse(1);
        return value > 0;
    }
    
}
