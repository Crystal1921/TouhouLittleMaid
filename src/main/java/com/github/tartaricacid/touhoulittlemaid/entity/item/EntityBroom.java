package com.github.tartaricacid.touhoulittlemaid.entity.item;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.OWNER_UUID_TAG_NAME;

public class EntityBroom extends AbstractEntityFromItem implements OwnableEntity, HasCustomInventoryScreen {
    public static final EntityType<EntityBroom> TYPE = EntityType.Builder.<EntityBroom>of(EntityBroom::new, MobCategory.MISC)
            .sized(1.375F, 0.5625F)
            .clientTrackingRange(10)
            .ridingOffset(0)
            .build("broom");

    private static final EntityDataAccessor<Optional<UUID>> OWNER_ID = SynchedEntityData.defineId(EntityBroom.class, EntityDataSerializers.OPTIONAL_UUID);

    private boolean keyForward = false;
    private boolean keyBack = false;
    private boolean keyLeft = false;
    private boolean keyRight = false;

    public EntityBroom(EntityType<EntityBroom> entityType, Level worldIn) {
        super(entityType, worldIn);
        this.setNoGravity(true);
    }

    public EntityBroom(Level worldIn) {
        this(TYPE, worldIn);
    }

    @OnlyIn(Dist.CLIENT)
    private static boolean keyForward() {
        return Minecraft.getInstance().options.keyUp.isDown();
    }

    @OnlyIn(Dist.CLIENT)
    private static boolean keyBack() {
        return Minecraft.getInstance().options.keyDown.isDown();
    }

    @OnlyIn(Dist.CLIENT)
    private static boolean keyLeft() {
        return Minecraft.getInstance().options.keyLeft.isDown();
    }

    @OnlyIn(Dist.CLIENT)
    private static boolean keyRight() {
        return Minecraft.getInstance().options.keyRight.isDown();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER_ID, Optional.empty());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(OWNER_UUID_TAG_NAME)) {
            setOwnerUUID(NbtUtils.loadUUID(Objects.requireNonNull(compound.get(OWNER_UUID_TAG_NAME))));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.entityData.get(OWNER_ID).ifPresent(uuid -> compound.putUUID(OWNER_UUID_TAG_NAME, uuid));
    }

    @Override
    public void travel(Vec3 vec3) {
        Entity entity = this.getControllingPassenger();
        if (entity instanceof Player player && this.isVehicle() && this.hasPassenger(e -> e instanceof EntityMaid)) {
            if (level.isClientSide) {
                // 不要问我为什么客户端数据能跑到服务端来
                // 一定是玄学
                keyForward = keyForward();
                keyBack = keyBack();
                keyLeft = keyLeft();
                keyRight = keyRight();
            }

            // 按键控制扫帚各个方向速度
            float strafe = keyLeft ? 0.5f : (keyRight ? -0.5f : 0);
            float vertical = keyForward ? -(player.getXRot() - 10) / 22.5f : 0;
            float forward = keyForward ? 3 : (keyBack ? -0.5f : 0);

            this.moveRelative(0.02f, new Vec3(strafe, vertical, forward));
            this.move(MoverType.SELF, this.getDeltaMovement());
            return;
        }
        if (!this.onGround()) {
            // 玩家没有坐在扫帚上，那就让它掉下来
            super.travel(new Vec3(0, -0.3f, 0));
            return;
        }
        super.travel(vec3);
    }

    @Override
    protected void pushEntities() {
        // 已经坐满两人，不执行
        if (this.getPassengers().size() >= 2) {
            return;
        }
        // 已经坐了一人，但不是玩家，不执行
        if (!this.getPassengers().isEmpty() && !(this.getControllingPassenger() instanceof Player)) {
            return;
        }
        if (!level.isClientSide) {
            List<EntityMaid> list = level.getEntitiesOfClass(EntityMaid.class, getBoundingBox().expandTowards(0.5, 0.1, 0.5), this::canMaidRide);
            list.stream().findFirst().ifPresent(entity -> entity.startRiding(this));
        }
    }

    private boolean canMaidRide(EntityMaid maid) {
        if (maid.canBrainMoving() && !maid.isVehicle() && EntitySelector.pushableBy(this).test(maid)) {
            UUID maidOwnerUUID = maid.getOwnerUUID();
            UUID broomOwnerUUID = this.getOwnerUUID();
            if (maidOwnerUUID == null || broomOwnerUUID == null) {
                return false;
            }
            return maidOwnerUUID.equals(broomOwnerUUID);
        }
        return false;
    }

    @Override
    protected void tickRidden(Player player, Vec3 pTravelVector) {
        // 记得将 fall distance 设置为 0，否则会摔死
        this.fallDistance = 0;

        // 施加上下晃动
        if (!this.onGround()) {
            this.addDeltaMovement(new Vec3(0, 0.01 * Math.sin(this.tickCount * Math.PI / 18), 0));
        }

        // 与旋转有关系的一堆东西，用来控制扫帚朝向
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        this.setRot(player.getYRot(), player.getXRot());
        super.tickRidden(player, pTravelVector);
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimension, float partialTick) {
        double xOffset = passenger instanceof EntityMaid ? -0.5 : 0;
        if (this.getPassengers().size() > 1) {
            if (this.getPassengers().indexOf(passenger) == 0) {
                xOffset = 0.35;
            } else {
                xOffset = -0.35;
            }
        }
        Vec3 hOffset = new Vec3(xOffset, -0.3125, 0).yRot((float) (-(this.getYRot() + 90) * Math.PI / 180));
        return super.getPassengerAttachmentPoint(passenger, dimension, partialTick).add(hOffset);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!player.isDiscrete() && !this.isPassenger() && !(this.getControllingPassenger() instanceof Player)) {
            if (this.getPassengers().size() > 1) {
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
            if (!level.isClientSide) {
                player.startRiding(this);
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return super.interact(player, hand);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player player) {
            return player;
        }
        return null;
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        List<Entity> passengers = this.getPassengers();
        boolean hasPlayer = false;
        EntityMaid maidOpen = null;
        for (int i = 0; i < Math.max(passengers.size(), 2); i++) {
            Entity entity = passengers.get(i);
            if (entity.equals(player)) {
                hasPlayer = true;
            }
            if (entity instanceof EntityMaid maid && maid.isOwnedBy(player)) {
                maidOpen = maid;
            }
        }
        if (hasPlayer && maidOpen != null) {
            maidOpen.openMaidGui(serverPlayer);
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    protected boolean canAddPassenger(Entity entity) {
        return this.getPassengers().size() < 2;
    }

    @Override
    protected boolean canKillEntity(Player player) {
        return true;
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.WOOL_BREAK;
    }

    @Override
    protected Item getWithItem() {
        return InitItems.BROOM.get();
    }

    @Override
    protected ItemStack getKilledStack() {
        return new ItemStack(this.getWithItem());
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
        this.resetFallDistance();
    }

    @Override
    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_ID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(OWNER_ID, Optional.ofNullable(uuid));
    }
}
