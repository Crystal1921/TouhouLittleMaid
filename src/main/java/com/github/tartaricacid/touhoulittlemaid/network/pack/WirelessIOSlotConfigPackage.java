package com.github.tartaricacid.touhoulittlemaid.network.pack;

import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.item.ItemWirelessIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLoactionUtil.getResourceLocation;

public record WirelessIOSlotConfigPackage(byte[] configData) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WirelessIOSlotConfigPackage> TYPE = new CustomPacketPayload.Type<>(getResourceLocation("wireless_slot_config"));
    public static final StreamCodec<ByteBuf, WirelessIOSlotConfigPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE_ARRAY,
            WirelessIOSlotConfigPackage::configData,
            WirelessIOSlotConfigPackage::new
    );

    public static void handle(WirelessIOSlotConfigPackage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer sender = (ServerPlayer) context.player();
            if (sender == null) {
                return;
            }
            ItemStack handItem = sender.getMainHandItem();
            if (handItem.getItem() == InitItems.WIRELESS_IO.get()) {
                if (message.configData.length > 0) {
                    ItemWirelessIO.setSlotConfig(handItem, message.configData);
                }
                //TODO : 打开界面
                //NetworkHooks.openScreen(sender, (ItemWirelessIO) handItem.getItem(), (buffer) -> buffer.writeItem(handItem));
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
