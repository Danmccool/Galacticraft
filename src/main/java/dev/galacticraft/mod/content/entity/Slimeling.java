package dev.galacticraft.mod.content.entity;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import com.google.common.collect.ImmutableList;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.GCSounds;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.tag.GCTags;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class Slimeling extends TamableAnimal implements ContainerListener, HasCustomInventoryScreen {
    protected SimpleContainer inventory;

    private static final EntityDataAccessor<Boolean> BAG = SynchedEntityData.defineId(Slimeling.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Vector3f> COLOR = SynchedEntityData.defineId(Slimeling.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<ItemStack> FAVORITE_FOOD = SynchedEntityData.defineId(Slimeling.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> KILLS = SynchedEntityData.defineId(Slimeling.class, EntityDataSerializers.INT);

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.MAX_HEALTH, 8.0).add(Attributes.ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BAG, false);
        this.entityData.define(COLOR, new Vector3f());
        this.entityData.define(FAVORITE_FOOD, ItemStack.EMPTY);
        this.entityData.define(KILLS, 0);
    }

    public int getKillCount()
    {
        return this.entityData.get(KILLS);
    }

    public void setKillCount(int killCount)
    {
        this.entityData.set(KILLS, killCount);
    }

    public Vector3f getColor() {
        return this.entityData.get(COLOR);
    }

    public void setColor(Vector3f color) {
        this.entityData.set(COLOR, color);
    }

    public ItemStack getFavoriteFood() {
        return this.entityData.get(FAVORITE_FOOD);
    }

    public void setFavoriteFood(ItemStack itemStack) {
        this.entityData.set(FAVORITE_FOOD, itemStack);
    }

    public final int MAX_AGE = 100000;

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        var randomSource = level.getRandom();

        this.setColor(this.getRandomColor(randomSource));
        this.setFavoriteFood(this.getRandomFavoriteFood(randomSource));

        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    public Vector3f getRandomColor(RandomSource randomSource) {
        return switch (randomSource.nextInt(3)) {
            case 0 -> new Vector3f(1.0f, 0.0f, 0.0f); // Red
            case 1 -> new Vector3f(0.0f, 0.0f, 1.0f); // Blue
            case 2 -> new Vector3f(1.0f, 1.0f, 0.0f); // Yellow
            default -> new Vector3f();
        };
    }

    public ItemStack getRandomFavoriteFood(RandomSource randomSource) {
        return new ItemStack(Util.getRandom(ImmutableList.copyOf(BuiltInRegistries.ITEM.getTagOrEmpty(GCTags.SLIMELING_FAVORITE_FOODS)), randomSource).value());
    }

    public Slimeling(EntityType<? extends Slimeling> entityType, Level level)
    {
        super(entityType, level);
        this.createInventory();
        //        this.tasks.addTask(1, new EntityAISwimming(this));
        //        this.aiSit = new EntityAISitGC(this);
        //        this.tasks.addTask(2, this.aiSit);
        //        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        //        this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, true));
        //        this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
        //        this.tasks.addTask(6, new EntityAIMate(this, 1.0D));
        //        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        //        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        //        this.tasks.addTask(9, new EntityAILookIdle(this));
        //        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        //        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        //        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
        //        this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntitySludgeling.class, false, p_apply_1_ -> p_apply_1_ instanceof EntitySludgeling));
    }

    public boolean hasBag() {
        return this.entityData.get(BAG);
    }

    public void setHasBag(boolean hasBag) {
        this.entityData.set(BAG, hasBag);
    }

    protected int getInventorySize() {
        return this.hasBag() ? 30 : 0;
    }

    protected void createInventory() {
        var simpleContainer = this.inventory;
        this.inventory = new SimpleContainer(this.getInventorySize());
        if (simpleContainer != null) {
            simpleContainer.removeListener(this);
            var i = Math.min(simpleContainer.getContainerSize(), this.inventory.getContainerSize());

            for (var j = 0; j < i; ++j) {
                var itemStack = simpleContainer.getItem(j);
                if (!itemStack.isEmpty()) {
                    this.inventory.setItem(j, itemStack.copy());
                }
            }
        }

        this.inventory.addListener(this);
        //        this.updateContainerEquipment();
    }

    @Override
    public boolean canBreatheUnderwater()
    {
        return true;
    }

    public float getSlimelingSize()
    {
        return this.getScale() * 2.0F;
    }

    private static final String HAS_BAG_TAG = "HasBag";
    private static final String FAVORITE_FOOD_TAG = "FavoriteFood";
    private static final String COLOR_TAG = "Color";
    private static final String KILL_COUNT_TAG = "KillCount";

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean(HAS_BAG_TAG, this.hasBag());
        compound.put(FAVORITE_FOOD_TAG, this.getFavoriteFood().save(new CompoundTag()));
        compound.put(COLOR_TAG, this.newFloatList(this.getColor().x(), this.getColor().y(), this.getColor().z()));
        compound.putInt(KILL_COUNT_TAG, this.getKillCount());

        if (this.hasBag()) {
            var listTag = new ListTag();

            for (var i = 2; i < this.inventory.getContainerSize(); ++i) {
                var itemStack = this.inventory.getItem(i);
                if (!itemStack.isEmpty()) {
                    var compoundTag = new CompoundTag();
                    compoundTag.putByte("Slot", (byte) i);
                    itemStack.save(compoundTag);
                    listTag.add(compoundTag);
                }
            }

            compound.put("Items", listTag);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setHasBag(compound.getBoolean(HAS_BAG_TAG));
        this.setFavoriteFood(ItemStack.of(compound.getCompound(FAVORITE_FOOD_TAG)));
        this.setKillCount(compound.getInt(KILL_COUNT_TAG));

        var colorListTag = compound.getList(COLOR_TAG, Tag.TAG_FLOAT);
        this.setColor(new Vector3f(colorListTag.getFloat(0), colorListTag.getFloat(1), colorListTag.getFloat(2)));

        this.createInventory();

        if (this.hasBag()) {
            var listTag = compound.getList("Items", 10);

            for (var i = 0; i < listTag.size(); ++i) {
                var compoundTag = listTag.getCompound(i);
                var j = compoundTag.getByte("Slot") & 255;
                if (j >= 2 && j < this.inventory.getContainerSize()) {
                    this.inventory.setItem(j, ItemStack.of(compoundTag));
                }
            }
        }

        //        this.updateContainerEquipment();
    }

    @Override
    public SlotAccess getSlot(int slot) {
        return slot == 499 ? new SlotAccess() {
            @Override
            public ItemStack get() {
                return Slimeling.this.hasBag() ? new ItemStack(GCItems.SLIMELING_INVENTORY_BAG) : ItemStack.EMPTY;
            }

            @Override
            public boolean set(ItemStack carried) {
                if (carried.isEmpty()) {
                    if (Slimeling.this.hasBag()) {
                        Slimeling.this.setHasBag(false);
                        Slimeling.this.createInventory();
                    }
                    return true;
                } else if (carried.is(GCItems.SLIMELING_INVENTORY_BAG)) {
                    if (!Slimeling.this.hasBag()) {
                        Slimeling.this.setHasBag(true);
                        Slimeling.this.createInventory();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        } : super.getSlot(slot);
    }

    //    @Override
    //    public void setScaleForAge(boolean par1)
    //    {
    //        this.setScale(this.getSlimelingSize());
    //    }
    //
    //    @Override
    //    public boolean isChild()
    //    {
    //        return this.getAge() / (float) this.MAX_AGE < 0.33F;
    //    }

    //    @Override
    //    protected void applyEntityAttributes()
    //    {
    //        super.applyEntityAttributes();
    //        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
    //        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.getMaxHealthSlimeling());
    //    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource)
    {
        return SoundEvents.SLIME_BLOCK_STEP;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return GCSounds.ENTITY_SLIME_DEATH;
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        if (!this.level().isClientSide()) {
        }
    }

    private double getMaxHealthSlimeling()
    {
        if (this.isTame()) {
            return 20.001D + 30.0 * ((double) this.age / (double) this.MAX_AGE);
        } else {
            return 8.0D;
        }
    }

    @Override
    public float getStandingEyeHeight(Pose pose, EntityDimensions dimensions)
    {
        return dimensions.height * 0.8F;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            var entity = source.getEntity();
            if (!this.level().isClientSide) {
                this.setOrderedToSit(false);
            }

            if (entity != null && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
                amount = (amount + 1.0F) / 2.0F;
            }

            return super.hurt(source, amount);
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        var bl = target.hurt(this.damageSources().mobAttack(this), (float) (int) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        if (bl) {
            this.doEnchantDamageEffects(this, target);
        }

        return bl;
    }

    public float getDamage()
    {
        var i = this.isTame() ? 5 : 2;
        return (float) (i * this.getAttributeValue(Attributes.ATTACK_DAMAGE));
    }

    //    @Override
    //    public void setTamed(boolean par1)
    //    {
    //        super.setTamed(par1);
    //        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.getMaxHealthSlimeling());
    //    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        var itemStack = player.getItemInHand(hand);

        if (this.isTame()) {
            if (!itemStack.isEmpty()) {
                if (ItemStack.isSameItem(itemStack, this.getFavoriteFood())) {
                    if (this.isOwnedBy(player)) {
                        itemStack.shrink(1);

                        if (this.random.nextInt(3) == 0) {
                            this.setFavoriteFood(this.getRandomFavoriteFood(this.random));
                        }
                    } else {
                        if (player instanceof ServerPlayer serverPlayer) {
                            //                            GCPlayerStats stats = GCPlayerStats.get(player);
                            //                            if (stats.getChatCooldown() == 0) {
                            //                                player.sendMessage(new TextComponentString(GCCoreUtil.translate("gui.slimeling.chat.wrong_player")));
                            //                                stats.setChatCooldown(100);
                            //                            }
                        }
                    }
                } else {
                    //                    if (this.world.isRemote) {
                    //                        MarsModuleClient.openSlimelingGui(this, 0);
                    //                    }
                }
            } else {
                //                if (this.world.isRemote) {
                //                    MarsModuleClient.openSlimelingGui(this, 0);
                //                }
            }

            return InteractionResult.SUCCESS;
        } else if (!itemStack.isEmpty() && this.isFood(itemStack)) {
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }

            if (this.random.nextInt(3) == 0) {
                this.tame(player);
                this.navigation.stop();
                this.setTarget(null);
                this.setOrderedToSit(true);
                this.level().broadcastEntityEvent(this, (byte) 7);
            } else {
                this.level().broadcastEntityEvent(this, (byte) 6);
            }

            //            if (!this.level().isClientSide()) {
            //                if (this.rand.nextInt(3) == 0) {
            //                    this.setTamed(true);
            //                    this.getNavigator().clearPath();
            //                    this.setAttackTarget(null);
            //                    this.setSittingAI(true);
            //                    this.setHealth(20.0F);
            //                    this.setOwnerId(player.getUniqueID());
            //                    this.setOwnerUsername(player.getName());
            //                    this.playTameEffect(true);
            //                    this.world.setEntityState(this, (byte) 7);
            //                }
            //                else {
            //                    this.playTameEffect(false);
            //                    this.world.setEntityState(this, (byte) 6);
            //                }
            //            }

            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    //    public String getOwnerUsername()
    //    {
    //        String s = this.dataManager.get(OWNER_USERNAME);
    //        return s == null || s.length() == 0 ? "" : s;
    //    }
    //
    //    public void setOwnerUsername(String username)
    //    {
    //        this.dataManager.set(OWNER_USERNAME, username);
    //    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(GCTags.SLIMELING_FOODS);
    }

    @Override
    @Nullable
    public Slimeling getBreedOffspring(ServerLevel level, AgeableMob otherSlimeling) {
        var slimeling = GCEntityTypes.SLIMELING.create(level);
        if (slimeling != null) {
            var uUID = this.getOwnerUUID();
            if (uUID != null) {
                slimeling.setOwnerUUID(uUID);
                slimeling.setTame(true);
            }
        }
        return slimeling;
    }

    //    public Slimeling spawnBabyAnimal(EntityAgeable par1EntityAgeable)
    //    {
    //        if (par1EntityAgeable instanceof Slimeling) {
    //            Slimeling otherSlimeling = (Slimeling) par1EntityAgeable;
    //
    //            Vector3 colorParentA = new Vector3(this.getColorRed(), this.getColorGreen(), this.getColorBlue());
    //            Vector3 colorParentB = new Vector3(otherSlimeling.getColorRed(), otherSlimeling.getColorGreen(), otherSlimeling.getColorBlue());
    //            Vector3 newColor = ColorUtil.addColorsRealistically(colorParentA, colorParentB);
    //            newColor.x = Math.max(Math.min(newColor.x, 1.0F), 0);
    //            newColor.y = Math.max(Math.min(newColor.y, 1.0F), 0);
    //            newColor.z = Math.max(Math.min(newColor.z, 1.0F), 0);
    //            Slimeling newSlimeling = new Slimeling(this.world, (float) newColor.x, (float) newColor.y, (float) newColor.z);
    //
    //            UUID s = this.getOwnerId();
    //
    //            if (s != null) {
    //                newSlimeling.setOwnerId(s);
    //                newSlimeling.setTamed(true);
    //            }
    //
    //            return newSlimeling;
    //        }
    //
    //        return null;
    //    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        if (otherAnimal == this) {
            return false;
        } else if (!this.isTame()) {
            return false;
        } else if (!(otherAnimal instanceof Slimeling slimeling)) {
            return false;
        } else {
            if (!slimeling.isTame()) {
                return false;
            } else if (slimeling.isInSittingPose()) {
                return false;
            } else {
                return this.isInLove() && slimeling.isInLove();
            }
        }
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target instanceof Creeper || target instanceof Ghast) {
            return false;
        } else if (target instanceof Slimeling slimeling) {
            return !slimeling.isTame() || slimeling.getOwner() != owner;
        } else if (target instanceof Player playerTarget && owner instanceof Player playerOwner && !playerOwner.canHarmPlayer(playerTarget)) {
            return false;
        } else if (target instanceof AbstractHorse targetHorse && targetHorse.isTamed()) {
            return false;
        } else {
            return !(target instanceof TamableAnimal tamableAnimal) || !tamableAnimal.isTame();
        }
    }

    public float getScale()
    {
        return this.getAge() / (float) this.MAX_AGE * 0.5F + 0.5F;
    }

    @Override
    public void containerChanged(Container container) {
        //        boolean bl = this.hasBag();
        //        this.updateContainerEquipment();
        //        if (this.tickCount > 20 && !bl && this.hasBag()) {
        //            this.playSound(this.getSaddleSoundEvent(), 0.5F, 1.0F);
        //        }
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        //        if (!this.level().isClientSide && (!this.isVehicle() || this.hasPassenger(player)) && this.isTame()) {
        //            player.openHorseInventory(this, this.inventory);
        //        }
    }

    //    @Override
    //    public void onDeath(DamageSource p_70645_1_)
    //    {
    //        super.onDeath(p_70645_1_);
    //
    //        if (!this.world.isRemote) {
    //            ItemStack bag = this.slimelingInventory.getStackInSlot(1);
    //            if (bag != null && bag.getItem() == MarsItems.marsItemBasic && bag.getItemDamage() == 4) {
    //                this.slimelingInventory.decrStackSize(1, 64);
    //                this.entityDropItem(bag, 0.5F);
    //            }
    //        }
    //    }

    //    public static class EntityAISitGC extends EntityAISit {
    //
    //        private EntityTameable theEntity;
    //        private boolean isSitting;
    //
    //        public EntityAISitGC(EntityTameable theEntity)
    //        {
    //            super(theEntity);
    //            this.theEntity = theEntity;
    //            this.setMutexBits(5);
    //        }
    //
    //        @Override
    //        public boolean shouldExecute()
    //        {
    //            if (!this.theEntity.isTamed()) {
    //                return false;
    //            }
    //            else if (this.theEntity.isInWater()) {
    //                return false;
    //            }
    //            else {
    //                Entity e = this.theEntity.getOwner();
    //                if (e instanceof EntityLivingBase) {
    //                    EntityLivingBase living = (EntityLivingBase) e;
    //                    return living == null ? true : (this.theEntity.getDistanceSq(living) < 144.0D && living.getRevengeTarget() != null ? false : this.isSitting);
    //                }
    //                return false;
    //            }
    //        }
    //
    //        @Override
    //        public void startExecuting()
    //        {
    //            this.theEntity.getNavigator().clearPath();
    //            this.theEntity.setSitting(true);
    //        }
    //
    //        @Override
    //        public void resetTask()
    //        {
    //            this.theEntity.setSitting(false);
    //        }
    //
    //        @Override
    //        public void setSitting(boolean isSitting)
    //        {
    //            this.isSitting = isSitting;
    //        }
    //    }
    //
    //    @Override
    //    protected void jump()
    //    {
    //        this.motionY = 0.48D / WorldUtil.getGravityFactor(this);
    //        if (this.motionY < 0.28D) {
    //            this.motionY = 0.28D;
    //        }
    //
    //        if (this.isPotionActive(MobEffects.JUMP_BOOST)) {
    //            this.motionY += (this.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F;
    //        }
    //
    //        if (this.isSprinting()) {
    //            float f = this.rotationYaw / Constants.RADIANS_TO_DEGREES;
    //            this.motionX -= MathHelper.sin(f) * 0.2F;
    //            this.motionZ += MathHelper.cos(f) * 0.2F;
    //        }
    //
    //        this.isAirBorne = true;
    //        ForgeHooks.onLivingJump(this);
    //    }
}