package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.tileentity.TileEntityEntityPlaceholderRenderer;
import com.github.tartaricacid.touhoulittlemaid.crafting.AltarRecipe;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.init.InitRecipes;
import com.github.tartaricacid.touhoulittlemaid.inventory.AltarRecipeInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;

public class ItemEntityPlaceholder extends Item {
    public static final IClientItemExtensions itemExtensions = new IClientItemExtensions() {
        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            Minecraft minecraft = Minecraft.getInstance();
            return new TileEntityEntityPlaceholderRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
        }
    };

    public ItemEntityPlaceholder() {
        super(new Item.Properties().stacksTo(1));
    }

    @SuppressWarnings("all")
    public static ItemStack setRecipeId(ItemStack stack, ResourceLocation id) {
        stack.set(InitDataComponent.RECIPES_ID_TAG, id.toString());
        return stack;
    }

    @SuppressWarnings("all")
    @Nullable
    public static ResourceLocation getRecipeId(ItemStack stack) {
        if (stack.has(InitDataComponent.RECIPES_ID_TAG)) {
            return ResourceLocation.parse(stack.get(InitDataComponent.RECIPES_ID_TAG));
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public static void fillItemCategory(CreativeModeTab.Output items) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }
        world.getRecipeManager().getAllRecipesFor(InitRecipes.ALTAR_CRAFTING.get()).forEach(recipe -> {
            if (!recipe.value().isItemCraft()) {
                items.accept(setRecipeId(new ItemStack(InitItems.ENTITY_PLACEHOLDER.get()), recipe.value().getId()));
            }
        });
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getClickedFace() == Direction.UP) {
            ResourceLocation id = getRecipeId(context.getItemInHand());
            Level world = context.getLevel();
            if (id != null && world instanceof ServerLevel) {
                Optional<RecipeHolder<?>> recipe = context.getLevel().getRecipeManager().byKey(id);
                if (recipe.isPresent() && recipe.get().value() instanceof AltarRecipe altarRecipe) {
                    altarRecipe.spawnOutputEntity((ServerLevel) world, context.getClickedPos().above(), null);
                    context.getItemInHand().shrink(1);
                }
            }
        }
        return super.useOn(context);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getName(ItemStack stack) {
        ResourceLocation recipeId = getRecipeId(stack);
        if (recipeId != null) {
            Path path = Paths.get(recipeId.getPath().toLowerCase(Locale.US));
            String namespace = recipeId.getNamespace().toLowerCase(Locale.US);
            String langKey = String.format("jei.%s.altar_craft.%s.result", namespace, path.getFileName());
            return Component.translatable(langKey);
        }
        return Component.translatable("item.touhou_little_maid.entity_placeholder");
    }
}
