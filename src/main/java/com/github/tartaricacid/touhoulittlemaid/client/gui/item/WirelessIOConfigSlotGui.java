package com.github.tartaricacid.touhoulittlemaid.client.gui.item;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button.WirelessIOSlotButton;
import com.github.tartaricacid.touhoulittlemaid.item.ItemWirelessIO;
import com.github.tartaricacid.touhoulittlemaid.network.message.WirelessIOSlotConfigPackage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class WirelessIOConfigSlotGui extends Screen {
    private static final ResourceLocation SLOT = ResourceLocation.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/gui/wireless_io_slot_config.png");
    private static final int SLOT_NUM = 38;
    private List<Boolean> configData;
    protected int imageWidth = 155;
    protected int imageHeight = 160;
    protected int leftPos;
    protected int topPos;

    protected WirelessIOConfigSlotGui(ItemStack wirelessIO) {
        super(Component.literal("Wireless IO Config Slot GUI"));
        configData = ItemWirelessIO.getSlotConfig(wirelessIO);
        if (configData == null) {
            configData = Lists.newArrayList();
        }
        int configDataSize = configData.size();
        if (configDataSize < SLOT_NUM) {
            for (int i = configDataSize; i < SLOT_NUM; i++) {
                configData.add(false);
            }
        }
    }

    @Override
    protected void init() {
        this.clearWidgets();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        int index = 0;
        for (int col = 0; col < 6; col++) {
            WirelessIOSlotButton button = new WirelessIOSlotButton(index, leftPos + 8 + 18 * col, topPos + 8, 16, 16, configData);
            button.initTextureValues(158, 0, 16, 16, SLOT);
            addRenderableWidget(button);
            index++;
        }

        for (int col = 0; col < 6; col++) {
            WirelessIOSlotButton button = new WirelessIOSlotButton(index, leftPos + 8 + 18 * col, topPos + 30, 16, 16, configData);
            button.initTextureValues(158, 0, 16, 16, SLOT);
            addRenderableWidget(button);
            index++;
        }

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 6; col++) {
                WirelessIOSlotButton button = new WirelessIOSlotButton(index, leftPos + 8 + 18 * col, topPos + 53 + 18 * row, 16, 16, configData);
                button.initTextureValues(158, 0, 16, 16, SLOT);
                addRenderableWidget(button);
                index++;
            }
        }

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 6; col++) {
                WirelessIOSlotButton button = new WirelessIOSlotButton(index, leftPos + 8 + 18 * col, topPos + 94 + 18 * row, 16, 16, configData);
                button.initTextureValues(158, 0, 16, 16, SLOT);
                addRenderableWidget(button);
                index++;
            }
        }

        for (int row = 0; row < 2; row++) {
            WirelessIOSlotButton button = new WirelessIOSlotButton(index, leftPos + 131, topPos + 8 + 18 * row, 16, 16, configData);
            button.initTextureValues(158, 0, 16, 16, SLOT);
            addRenderableWidget(button);
            index++;
        }

        Button confirm = Button.builder(Component.translatable("gui.done"), b -> PacketDistributor.sendToServer(new WirelessIOSlotConfigPackage(this.configData)))
                .pos(leftPos, topPos + 140).size(60, 20).build();
        Button cancel = Button.builder(Component.translatable("gui.cancel"), b -> PacketDistributor.sendToServer(new WirelessIOSlotConfigPackage()))
                .pos(leftPos + 62, topPos + 140).size(60, 20).build();
        addRenderableWidget(confirm);
        addRenderableWidget(cancel);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBg(graphics, mouseX, mouseY, partialTicks);
        for (Renderable renderable : this.renderables) {
            renderable.render(graphics, mouseX, mouseY, partialTicks);
        }
    }

    private void renderBg(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.blit(SLOT, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
