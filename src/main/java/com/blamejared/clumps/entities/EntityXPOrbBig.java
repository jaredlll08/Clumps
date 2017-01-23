package com.blamejared.clumps.entities;

import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EntityXPOrbBig extends EntityXPOrb {
	
	/**
	 * The closest EntityPlayer to this orb.
	 */
	private EntityPlayer closestPlayer;
	/**
	 * The closest EntityXPOrbBig to this orb.
	 */
	private EntityXPOrbBig closestOrb;
	/**
	 * Threshold color for tracking players
	 */
	private int xpTargetColor;
	
	
	public EntityXPOrbBig(World worldIn, double x, double y, double z, int expValue) {
		super(worldIn);
		this.setSize(0.5f, 0.5f);
		this.setPosition(x, y, z);
		this.rotationYaw = (float) (Math.random() * 360.0D);
		this.motionX = (double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
		this.motionY = (double) ((float) (Math.random() * 0.2D) * 2.0F);
		this.motionZ = (double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
		this.xpValue = expValue;
	}
	
	public EntityXPOrbBig(World worldIn, double x, double y, double z, int expValue, float sizeX, float sizeY) {
		super(worldIn);
		this.setSize(sizeX, sizeY);
		this.setPosition(x, y, z);
		this.rotationYaw = (float) (Math.random() * 360.0D);
		this.motionX = (double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
		this.motionY = (double) ((float) (Math.random() * 0.2D) * 2.0F);
		this.motionZ = (double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
		this.xpValue = expValue;
	}
	
	
	public EntityXPOrbBig(World worldIn) {
		super(worldIn);
		this.setSize(0.5f, 0.5f);
	}
	
	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		super.onUpdate();
		if(this.delayBeforeCanPickup > 0) {
			--this.delayBeforeCanPickup;
		}
		if(!worldObj.isRemote && this.xpValue == 0) {
			this.setDead();
		}
		
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		
		if(!this.hasNoGravity()) {
			this.motionY -= 0.029999999329447746D;
		}
		
		if(this.worldObj.getBlockState(new BlockPos(this)).getMaterial() == Material.LAVA) {
			this.motionY = 0.20000000298023224D;
			this.motionX = (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			this.motionZ = (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
		}
		
		this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);
		double d0 = 8.0D;
		
		if(this.xpTargetColor < this.xpColor - 20 + this.getEntityId() % 100) {
			if(this.closestPlayer == null || this.closestPlayer.getDistanceSqToEntity(this) > 64.0D) {
				this.closestPlayer = this.worldObj.getClosestPlayerToEntity(this, 8.0D);
				this.closestOrb = null;
			}
			
			if((closestPlayer == null && closestOrb == null) || (this.closestOrb != null && this.closestOrb.getDistanceSqToEntity(this) > 64)) {
				this.closestOrb = getClosestOrb(posX, posY, posZ, 8);
			}
			this.xpTargetColor = this.xpColor;
		}
		
		if(this.closestPlayer != null && this.closestPlayer.isSpectator()) {
			this.closestPlayer = null;
		}
		
		if(this.closestPlayer != null) {
			double d1 = (this.closestPlayer.posX - this.posX) / 8.0D;
			double d2 = (this.closestPlayer.posY + (double) this.closestPlayer.getEyeHeight() / 2.0D - this.posY) / 8.0D;
			double d3 = (this.closestPlayer.posZ - this.posZ) / 8.0D;
			double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
			double d5 = 1.0D - d4;
			
			if(d5 > 0.0D) {
				d5 = d5 * d5;
				this.motionX += d1 / d4 * d5 * 0.1D;
				this.motionY += d2 / d4 * d5 * 0.1D;
				this.motionZ += d3 / d4 * d5 * 0.1D;
			}
		} else if(this.closestPlayer == null && this.closestOrb != null) {
			double d1 = (this.closestOrb.posX - this.posX) / 8.0D;
			double d2 = (this.closestOrb.posY + (double) this.closestOrb.getEyeHeight() / 2.0D - this.posY) / 8.0D;
			double d3 = (this.closestOrb.posZ - this.posZ) / 8.0D;
			double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
			double d5 = 1.0D - d4;
			
			if(d5 > 0.0D) {
				d5 = d5 * d5;
				this.motionX += d1 / d4 * d5 * 0.1D;
				this.motionY += d2 / d4 * d5 * 0.1D;
				this.motionZ += d3 / d4 * d5 * 0.1D;
			}
		}
		
		
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		float f = 0.98F;
		
		if(this.onGround) {
			f = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.98F;
		}
		
		this.motionX *= (double) f;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= (double) f;
		
		if(this.onGround) {
			this.motionY *= -0.8999999761581421D;
		}
		++this.xpColor;
		++this.xpOrbAge;
		
		if(this.xpOrbAge >= 6000) {
			this.setDead();
		}
		if(worldObj.getTotalWorldTime() % 1 == 0) {
			List<EntityXPOrbBig> orbs = worldObj.getEntitiesWithinAABB(EntityXPOrbBig.class, new AxisAlignedBB(posX - 2.5, posY - 2.5, posZ - 2.5, posX + 2.5, posY + 2.5, posZ + 2.5));
			int newSize = 0;
			if(orbs.size() > 0) {
				EntityXPOrbBig orb = orbs.get(worldObj.rand.nextInt(orbs.size()));
				if(!orb.getUniqueID().equals(this.getUniqueID()) && orb.xpValue < this.xpValue) {
					newSize += orb.getXpValue() + xpValue;
					orb.setDead();
				}
				if(newSize > xpValue) {
					EntityXPOrbBig norb = new EntityXPOrbBig(worldObj, posX, posY, posZ, newSize);
					if(!worldObj.isRemote) {
						worldObj.spawnEntityInWorld(norb);
						setDead();
					}
				}
				orbs.clear();
			}
		}
	}
	
	
	@Nullable
	public EntityXPOrbBig getClosestOrb(double posX, double posY, double posZ, double distance) {
		double d0 = -1.0D;
		EntityXPOrbBig returnedOrb = null;
		List<EntityXPOrbBig> orbs = worldObj.getEntitiesWithinAABB(EntityXPOrbBig.class, new AxisAlignedBB(posX - distance, posY - distance, posZ - distance, posX + distance, posY + distance, posZ + distance), input -> !input.getUniqueID().equals(getUniqueID()));
		for(EntityXPOrbBig orb : orbs) {
			double d1 = orb.getDistanceSq(posX, posY, posZ);
			
			if((distance < 0.0D || d1 < distance * distance) && (d0 == -1.0D || d1 < d0)) {
				d0 = d1;
				returnedOrb = orb;
			}
		}
		
		return returnedOrb;
	}
	
	
	/**
	 * Called by a player entity when they collide with an entity
	 */
	public void onCollideWithPlayer(EntityPlayer entityIn) {
		if(!this.worldObj.isRemote) {
			if(net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerPickupXpEvent(entityIn, this)))
				return;
			entityIn.xpCooldown = 0;
			this.worldObj.playSound(null, entityIn.posX, entityIn.posY, entityIn.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, SoundCategory.PLAYERS, 0.1F, 0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
			entityIn.onItemPickup(this, 1);
			ItemStack itemstack = EnchantmentHelper.getEnchantedItem(Enchantments.MENDING, entityIn);
			
			if(itemstack != null && itemstack.isItemDamaged()) {
				int i = Math.min(this.xpToDurability(this.xpValue), itemstack.getItemDamage());
				this.xpValue -= this.durabilityToXp(i);
				itemstack.setItemDamage(itemstack.getItemDamage() - i);
			}
			
			if(this.xpValue > 0) {
				entityIn.addExperience(this.xpValue);
			}
			
			this.setDead();
		}
	}
	
	private int durabilityToXp(int durability) {
		return durability / 2;
	}
	
	private int xpToDurability(int xp) {
		return xp * 2;
	}
}
