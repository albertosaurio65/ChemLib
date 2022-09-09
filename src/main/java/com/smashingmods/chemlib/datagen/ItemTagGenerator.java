package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ItemTagGenerator extends ForgeRegistryTagsProvider<Item> {

    public ItemTagGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ForgeRegistries.ITEMS, ChemLib.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        ItemRegistry.getChemicalItems().forEach(item -> {
            String type = item.getItemType().getSerializedName();
            String name = item.getChemicalName();
            TagKey<Item> key = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).createTagKey(new ResourceLocation("forge", String.format("%ss/%s", type, name)));
            tag(key).add(item);
        });

        ItemRegistry.getChemicalItemByNameAndType("potassium_nitrate", ChemicalItemType.COMPOUND).ifPresent(compound -> {
            ResourceLocation niter = new ResourceLocation("forge", "dusts/niter");
            TagKey<Item> key = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).createTagKey(niter);
            tag(key).add(compound);
        });

        ItemRegistry.getChemicalItemByNameAndType("hydroxylapatite", ChemicalItemType.COMPOUND).ifPresent(compound -> {
            ResourceLocation niter = new ResourceLocation("forge", "dusts/apatite");
            TagKey<Item> key = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).createTagKey(niter);
            tag(key).add(compound);
        });

        ItemRegistry.getChemicalItemByNameAndType("cellulose", ChemicalItemType.COMPOUND).ifPresent(compound -> {
            ResourceLocation sawdust = new ResourceLocation("forge", "sawdust");
            TagKey<Item> key = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).createTagKey(sawdust);
            tag(key).add(compound);
        });

        ItemRegistry.getChemicalItemByNameAndType("mercury_sulfide", ChemicalItemType.COMPOUND).ifPresent(compound -> {
            ResourceLocation sawdust = new ResourceLocation("forge", "dusts/cinnabar");
            TagKey<Item> key = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).createTagKey(sawdust);
            tag(key).add(compound);
        });
    }

    @Override
    @Nonnull
    public String getName() {
        return ChemLib.MODID + ":tags";
    }
}
