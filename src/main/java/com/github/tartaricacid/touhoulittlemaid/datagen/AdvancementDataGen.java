package com.github.tartaricacid.touhoulittlemaid.datagen;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.advancements.GiveSmartSlabConfigTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementDataGen extends AdvancementProvider {
    public AdvancementDataGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
        super(packOutput, provider, helper, List.of(
                new GiveSmartSlab()
        ));
    }

    private static final class GiveSmartSlab implements AdvancementGenerator {
        @Override
        public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> saver, ExistingFileHelper helper) {
            Advancement.Builder.advancement()
                    .addCriterion("tick", GiveSmartSlabConfigTrigger.Instance.instance())
                    .rewards(AdvancementRewards.Builder.loot(LootTableGenerator.GIVE_SMART_SLAB))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "give_smart_slab"), helper);
        }
    }
}
