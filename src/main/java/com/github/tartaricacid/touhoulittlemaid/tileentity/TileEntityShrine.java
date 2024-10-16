package com.github.tartaricacid.touhoulittlemaid.tileentity;

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class TileEntityShrine extends BlockEntity {
    public static final BlockEntityType<TileEntityShrine> TYPE = BlockEntityType.Builder.of(TileEntityShrine::new, InitBlocks.SHRINE.get()).build(null);
    private static final String STORAGE_ITEM = "StorageItem";
    private final ItemStackHandler handler = new ItemStackHandler() {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.getItem() == InitItems.FILM.get();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    public TileEntityShrine(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        getPersistentData().put(STORAGE_ITEM, handler.serializeNBT(pRegistries));
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        handler.deserializeNBT(pRegistries, getPersistentData().getCompound(STORAGE_ITEM));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return this.saveWithoutMetadata(pRegistries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void refresh() {
        this.setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    public ItemStack getStorageItem() {
        return handler.getStackInSlot(0);
    }

    public void insertStorageItem(ItemStack stack) {
        handler.insertItem(0, stack, false);
    }

    public ItemStack extractStorageItem() {
        return handler.extractItem(0, 1, false);
    }

    public boolean isEmpty() {
        return handler.getStackInSlot(0).isEmpty();
    }

    public boolean canInsert(ItemStack stack) {
        return handler.isItemValid(0, stack);
    }
}
