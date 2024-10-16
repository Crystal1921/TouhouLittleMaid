package com.github.tartaricacid.touhoulittlemaid.config.subconfig;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

import static com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil.getItemId;

public final class MaidConfig {
    private static final String TRANSLATE_KEY = "config.touhou_little_maid.maid";
    public static final String TAG_PREFIX = "#";
    public static ModConfigSpec.ConfigValue<String> MAID_TAMED_ITEM;
    public static ModConfigSpec.ConfigValue<String> MAID_TEMPTATION_ITEM;
    public static ModConfigSpec.ConfigValue<String> MAID_NTR_ITEM;
    public static ModConfigSpec.IntValue MAID_WORK_RANGE;
    public static ModConfigSpec.IntValue MAID_IDLE_RANGE;
    public static ModConfigSpec.IntValue MAID_SLEEP_RANGE;
    public static ModConfigSpec.IntValue MAID_NON_HOME_RANGE;
    public static ModConfigSpec.IntValue FEED_ANIMAL_MAX_NUMBER;
    public static ModConfigSpec.BooleanValue MAID_CHANGE_MODEL;
    public static ModConfigSpec.BooleanValue MAID_GOMOKU_OWNER_LIMIT;
    public static ModConfigSpec.IntValue OWNER_MAX_MAID_NUM;
    public static ModConfigSpec.DoubleValue REPLACE_ALLAY_PERCENT;

    public static ModConfigSpec.ConfigValue<List<String>> MAID_BACKPACK_BLACKLIST;
    public static ModConfigSpec.ConfigValue<List<String>> MAID_ATTACK_IGNORE;
    public static ModConfigSpec.ConfigValue<List<String>> MAID_RANGED_ATTACK_IGNORE;

    public static ModConfigSpec.ConfigValue<List<String>> MAID_WORK_MEALS_BLOCK_LIST;
    public static ModConfigSpec.ConfigValue<List<String>> MAID_HOME_MEALS_BLOCK_LIST;
    public static ModConfigSpec.ConfigValue<List<String>> MAID_HEAL_MEALS_BLOCK_LIST;

    public static ModConfigSpec.ConfigValue<List<String>> MAID_WORK_MEALS_BLOCK_LIST_REGEX;
    public static ModConfigSpec.ConfigValue<List<String>> MAID_HOME_MEALS_BLOCK_LIST_REGEX;
    public static ModConfigSpec.ConfigValue<List<String>> MAID_HEAL_MEALS_BLOCK_LIST_REGEX;
    public static ModConfigSpec.ConfigValue<List<List<String>>> MAID_EATEN_RETURN_CONTAINER_LIST;

    public static void init(ModConfigSpec.Builder builder) {
        builder.translation(TRANSLATE_KEY).push("maid");

        builder.comment("The item that can tamed maid", "Use the registered name of the item directly or write tag name with # as prefix")
                .translation(translateKey("maid_tamed_item"));
        MAID_TAMED_ITEM = builder.define("MaidTamedItem", "minecraft:cake", MaidConfig::checkItemAndTag);

        builder.comment("The item that can temptation maid", "Use the registered name of the item directly or write tag name with # as prefix")
                .translation(translateKey("maid_temptation_item"));
        MAID_TEMPTATION_ITEM = builder.define("MaidTemptationItem", "minecraft:cake", MaidConfig::checkItemAndTag);

        builder.comment("The item that can NTR maid", "Use the registered name of the item directly or write tag name with # as prefix")
                .translation(translateKey("maid_ntr_item"));
        MAID_NTR_ITEM = builder.define("MaidNtrItem", "minecraft:structure_void", MaidConfig::checkItemAndTag);

        builder.comment("The max range of maid work mode")
                .translation(translateKey("maid_work_range"));
        MAID_WORK_RANGE = builder.defineInRange("MaidWorkRange", 12, 3, 64);

        builder.comment("The max range of maid idle mode")
                .translation(translateKey("maid_idle_range"));
        MAID_IDLE_RANGE = builder.defineInRange("MaidIdleRange", 6, 3, 32);

        builder.comment("The max range of maid sleep mode")
                .translation(translateKey("maid_sleep_range"));
        MAID_SLEEP_RANGE = builder.defineInRange("MaidSleepRange", 6, 3, 32);

        builder.comment("The max range of maid's Non-Home mode")
                .translation(translateKey("maid_non_home_range"));
        MAID_NON_HOME_RANGE = builder.defineInRange("MaidNonHomeRange", 8, 3, 32);

        builder.comment("The max number of animals around when the maid breeds animals")
                .translation(translateKey("feed_animal_max_number"));
        FEED_ANIMAL_MAX_NUMBER = builder.defineInRange("FeedAnimalMaxNumber", 50, 6, 65536);

        builder.comment("Maid can switch models freely")
                .translation(translateKey("maid_change_model"));
        MAID_CHANGE_MODEL = builder.define("MaidChangeModel", true);

        builder.comment("Maid can only play gomoku with her owner")
                .translation(translateKey("maid_gomoku_owner_limit"));
        MAID_GOMOKU_OWNER_LIMIT = builder.define("MaidGomokuOwnerLimit", true);

        builder.comment("The maximum number of maids the player own")
                .translation(translateKey("owner_max_maid_num"));
        OWNER_MAX_MAID_NUM = builder.defineInRange("OwnerMaxMaidNum", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

        builder.comment("These items cannot be placed in the maid backpack")
                .translation(translateKey("maid_backpack_blacklist"));
        MAID_BACKPACK_BLACKLIST = builder.define("MaidBackpackBlackList", Lists.newArrayList(), MaidConfig::checkItemIds);

        builder.comment("The entity that the maid will not recognize as targets for attack")
                .translation(translateKey("maid_attack_ignore"));
        MAID_ATTACK_IGNORE = builder.define("MaidAttackIgnore", Lists.newArrayList());

        builder.comment("The entity that the maid will not hurt when in ranged attack")
                .translation(translateKey("maid_ranged_attack_ignore"));
        MAID_RANGED_ATTACK_IGNORE = builder.define("MaidRangedAttackIgnore", Lists.newArrayList());

        builder.comment("Percentage chance of replace Allays spawn in pillager outposts with Maids")
                .translation(translateKey("replace_allay_percent"));
        REPLACE_ALLAY_PERCENT = builder.defineInRange("ReplaceAllayPercent", 0.2, 0, 1);

        builder.comment("These items cannot be used as a maid's work meals")
                .translation(translateKey("maid_work_meals_block_list"));
        MAID_WORK_MEALS_BLOCK_LIST = builder.define("MaidWorkMealsBlockList", Lists.newArrayList(getItemId(Items.PUFFERFISH), getItemId(Items.POISONOUS_POTATO), getItemId(Items.ROTTEN_FLESH), getItemId(Items.SPIDER_EYE), getItemId(Items.CHORUS_FRUIT)), MaidConfig::checkItemIds);

        builder.comment("These items cannot be used as a maid's home meals")
                .translation(translateKey("maid_home_meals_block_list"));
        MAID_HOME_MEALS_BLOCK_LIST = builder.define("MaidHomeMealsBlockList", Lists.newArrayList(getItemId(Items.PUFFERFISH), getItemId(Items.POISONOUS_POTATO), getItemId(Items.ROTTEN_FLESH), getItemId(Items.SPIDER_EYE), getItemId(Items.CHORUS_FRUIT)), MaidConfig::checkItemIds);

        builder.comment("These items cannot be used as a maid's heal meals")
                .translation(translateKey("maid_heal_meals_block_list"));
        MAID_HEAL_MEALS_BLOCK_LIST = builder.define("MaidHealMealsBlockList", Lists.newArrayList(getItemId(Items.PUFFERFISH), getItemId(Items.POISONOUS_POTATO), getItemId(Items.ROTTEN_FLESH), getItemId(Items.SPIDER_EYE)), MaidConfig::checkItemIds);

        builder.comment("These items cannot be used as a maid's work meals which match the regex")
                .translation(translateKey("maid_work_meals_block_list_regex"));
        MAID_WORK_MEALS_BLOCK_LIST_REGEX = builder.define("MaidWorkMealsBlockListRegEx", Lists.newArrayList());

        builder.comment("These items cannot be used as a maid's home meals which match the regex")
                .translation(translateKey("maid_home_meals_block_list_regex"));
        MAID_HOME_MEALS_BLOCK_LIST_REGEX = builder.define("MaidHomeMealsBlockListRegEx", Lists.newArrayList());

        builder.comment("These items cannot be used as a maid's heal meals which match the regex")
                .translation(translateKey("maid_heal_meals_block_list_regex"));
        MAID_HEAL_MEALS_BLOCK_LIST_REGEX = builder.define("MaidHealMealsBlockListRegEx", Lists.newArrayList());

        builder.comment("These entries configure the container returned after a maid has eaten", "Eg: [\"minecraft:beetroot_soup\", \"minecraft:bowl\"]")
                .translation(translateKey("maid_eaten_return_container_list"));
        MAID_EATEN_RETURN_CONTAINER_LIST = builder.define("MaidEatenReturnContainerList", Lists.newArrayList());

        builder.pop();
    }

    private static String translateKey(String key) {
        return TRANSLATE_KEY + "." + key;
    }

    private static boolean checkItemIds(Object obj) {
        if (obj instanceof List<?> list) {
            return list.stream().allMatch(MaidConfig::checkItemId);
        }
        return false;
    }

    private static boolean checkItemAndTag(Object obj) {
        if (obj instanceof String text) {
            if (text.startsWith("#")) {
                text = text.substring(1);
                return ResourceLocation.tryParse(text) != null;
            } else {
                return checkItemId(text);
            }
        }
        return false;
    }

    private static boolean checkItemId(Object obj) {
        if (obj instanceof String text) {
            return ResourceLocation.tryParse(text) != null;
        }
        return false;
    }
}
