package com.github.tartaricacid.touhoulittlemaid.client.overlay;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.Maps;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MaidTipsOverlay implements LayeredDraw.Layer {
    private static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/gui/maid_tips_icon.png");
    private static final Map<Item, MutableComponent> TIPS = Maps.newHashMap();

    public static void init() {
        addTips("overlay.touhou_little_maid.compass.tips", Items.COMPASS);
        addTips("overlay.touhou_little_maid.golden_apple.tips", Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);
        addTips("overlay.touhou_little_maid.potion.tips", Items.POTION);
        addTips("overlay.touhou_little_maid.milk_bucket.tips", Items.MILK_BUCKET);
        addTips("overlay.touhou_little_maid.script_book.tips", Items.WRITABLE_BOOK, Items.WRITTEN_BOOK);
        addTips("overlay.touhou_little_maid.glass_bottle.tips", Items.GLASS_BOTTLE);
        addTips("overlay.touhou_little_maid.name_tag.tips", Items.NAME_TAG);
        addTips("overlay.touhou_little_maid.lead.tips", Items.LEAD);
        addTips("overlay.touhou_little_maid.debug_stick.tips", Items.DEBUG_STICK);
        addTips("overlay.touhou_little_maid.saddle.tips", Items.SADDLE);
    }

    private static MutableComponent checkSpecialTips(ItemStack mainhandItem, EntityMaid maid, LocalPlayer player) {
        if (!maid.isOwnedBy(player) && EntityMaid.getNtrItem().test(mainhandItem)) {
            return Component.translatable("overlay.touhou_little_maid.ntr_item.tips");
        }
        if (maid.isOwnedBy(player) && maid.hasBackpack() && mainhandItem.is(Tags.Items.TOOLS_SHEAR)) {
            return Component.translatable("overlay.touhou_little_maid.remove_backpack.tips");
        }
        return null;
    }

    private static void addTips(String key, Item... items) {
        for (Item item : items) {
            TIPS.put(item, Component.translatable(key));
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        Options options = minecraft.options;
        LocalPlayer player = minecraft.player;
        if (!options.getCameraType().isFirstPerson()) {
            return;
        }
        if (minecraft.gameMode == null || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        if (!(minecraft.hitResult instanceof EntityHitResult result)) {
            return;
        }
        if (!(result.getEntity() instanceof EntityMaid maid)) {
            return;
        }
        if (player == null) {
            return;
        }
        if (!maid.isAlive()) {
            return;
        }
        MutableComponent tip = null;
        if (maid.isOwnedBy(player)) {
            tip = TIPS.get(player.getMainHandItem().getItem());
        }
        if (tip == null) {
            tip = checkSpecialTips(player.getMainHandItem(), maid, player);
        }
        if (tip != null) {
            int screenHeight = guiGraphics.guiHeight();
            int screenWidth = guiGraphics.guiWidth();
            List<FormattedCharSequence> split = minecraft.font.split(tip, 150);
            int offset = (screenHeight / 2 - 5) - split.size() * 10;
            guiGraphics.renderItem(player.getMainHandItem(), screenWidth / 2 - 8, offset);
            guiGraphics.blit(ICON, screenWidth / 2 + 2, offset - 4, 16, 16, 16, 16, 16, 16);
            offset += 18;
            for (FormattedCharSequence sequence : split) {
                int width = minecraft.font.width(sequence);
                guiGraphics.drawString(minecraft.font, sequence, (screenWidth - width) / 2, offset, 0xFFFFFF);
                offset += 10;
            }
        }
    }
}
