package com.blamejared.clumps.entities;

import java.util.List;
import java.util.Map;

import com.blamejared.clumps.Clumps;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityXPOrbBig extends ExperienceOrbEntity {
    
    /**
     * The closest EntityPlayer to this orb.
     */
    private PlayerEntity closestPlayer;
    /**
     * Threshold color for tracking players
     */
    private int xpTargetColor;
    
    public EntityXPOrbBig(World worldIn, double x, double y, double z, int expValue) {
        super(Clumps.BIG_ORB_ENTITY_TYPE, worldIn);
        this.setPosition(x, y, z);
        this.rotationYaw = (float) (this.rand.nextDouble() * 360.0D);
        this.setMotion((this.rand.nextDouble() * (double) 0.2F - (double) 0.1F) * 2.0D, this.rand.nextDouble() * 0.2D * 2.0D, (this.rand.nextDouble() * (double) 0.2F - (double) 0.1F) * 2.0D);
        this.xpValue = expValue;
    }
    
    public EntityXPOrbBig(EntityType<? extends ExperienceOrbEntity> p_i50382_1_, World p_i50382_2_) {
        super(Clumps.BIG_ORB_ENTITY_TYPE, p_i50382_2_);
    }
    
    @Override
    public void tick() {
        //        super.tick();
        if(!world.isRemote && this.xpValue == 0) {
            this.remove();
            return;
        }
        if(this.delayBeforeCanPickup > 0) {
            --this.delayBeforeCanPickup;
        }
        
        
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        
        if(this.areEyesInFluid(FluidTags.WATER)) {
            this.applyFloatMotion();
        } else if(!this.hasNoGravity()) {
            this.setMotion(this.getMotion().add(0.0D, -0.03D, 0.0D));
        }
        
        if(this.world.getFluidState(new BlockPos(this)).isTagged(FluidTags.LAVA)) {
            this.setMotion((double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F), (double) 0.2F, (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F));
            this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
        }
        
        if(!this.world.areCollisionShapesEmpty(this.getBoundingBox())) {
            this.pushOutOfBlocks(this.posX, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.posZ);
        }
        
        if(this.xpTargetColor < this.xpColor - 20 + this.getEntityId() % 100) {
            if(this.closestPlayer == null || this.closestPlayer.getDistance(this) > 64.0D) {
                this.closestPlayer = this.world.getClosestPlayer(this, 8.0D);
            }
            
            this.xpTargetColor = this.xpColor;
        }
        
        if(this.closestPlayer != null && this.closestPlayer.isSpectator()) {
            this.closestPlayer = null;
        }
        
        if(this.closestPlayer != null) {
            Vec3d vec3d = new Vec3d(this.closestPlayer.posX - this.posX, this.closestPlayer.posY + (double) this.closestPlayer.getEyeHeight() / 2.0D - this.posY, this.closestPlayer.posZ - this.posZ);
            double d1 = vec3d.lengthSquared();
            if(d1 < 64.0D) {
                double d2 = 1.0D - Math.sqrt(d1) / 8.0D;
                this.setMotion(this.getMotion().add(vec3d.normalize().scale(d2 * d2 * 0.1D)));
            }
        }
        
        this.move(MoverType.SELF, this.getMotion());
        float f = 0.98F;
        
        if(this.onGround) {
            BlockPos underPos = new BlockPos(this.posX, this.getBoundingBox().minY - 1.0D, this.posZ);
            f = this.world.getBlockState(underPos).getSlipperiness(this.world, underPos, this) * 0.98F;
        }
        
        this.setMotion(this.getMotion().mul((double) f, 0.98D, (double) f));
        if(this.onGround) {
            this.setMotion(this.getMotion().mul(1.0D, -0.9D, 1.0D));
        }
        
        if(this.xpOrbAge >= 6000) {
            this.remove();
        }
        if(world.getGameTime() % 5 == 0) {
            List<EntityXPOrbBig> orbs = world.getEntitiesWithinAABB(EntityXPOrbBig.class, new AxisAlignedBB(posX - 2, posY - 2, posZ - 2, posX + 2, posY + 2, posZ + 2), EntityPredicates.IS_ALIVE);
            int newSize = 0;
            if(orbs.size() > 0) {
                EntityXPOrbBig orb = orbs.get(world.rand.nextInt(orbs.size()));
                if(!orb.getUniqueID().equals(this.getUniqueID()) && orb.xpValue <= this.xpValue) {
                    newSize += orb.getXpValue() + xpValue;
                    orb.remove();
                }
                if(newSize > xpValue) {
                    if(!world.isRemote) {
                        EntityXPOrbBig norb = new EntityXPOrbBig(world, posX, posY, posZ, newSize);
                        world.addEntity(norb);
                        remove();
                    }
                }
                orbs.clear();
            }
        }
    }
    
    private void applyFloatMotion() {
        Vec3d vec3d = this.getMotion();
        this.setMotion(vec3d.x * (double) 0.99F, Math.min(vec3d.y + (double) 5.0E-4F, (double) 0.06F), vec3d.z * (double) 0.99F);
    }
    
    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(PlayerEntity entityIn) {
        if(!this.world.isRemote) {
            if(this.delayBeforeCanPickup == 0 && entityIn.xpCooldown == 0) {
                if(net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerPickupXpEvent(entityIn, this)))
                    return;
                entityIn.xpCooldown = 2;
                entityIn.onItemPickup(this, 1);
                Map.Entry<EquipmentSlotType, ItemStack> entry = EnchantmentHelper.func_222189_b(Enchantments.MENDING, entityIn);
                if(entry != null) {
                    ItemStack itemstack = entry.getValue();
                    if(!itemstack.isEmpty() && itemstack.isDamaged()) {
                        int i = Math.min((int) (this.xpValue * itemstack.getXpRepairRatio()), itemstack.getDamage());
                        this.xpValue -= this.durabilityToXp(i);
                        itemstack.setDamage(itemstack.getDamage() - i);
                    }
                }
                
                if(this.xpValue > 0) {
                    entityIn.giveExperiencePoints(this.xpValue);
                }
                
                this.remove();
            }
            
        }
    }
    
    private int durabilityToXp(int durability) {
        return durability / 2;
    }
    
    private int xpToDurability(int xp) {
        return xp * 2;
    }
    
    
}
