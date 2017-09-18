package net.pl3x.forge.discord.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.translation.I18n;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemUtil {
    private static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.##");
    private static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    public static List<String> getTooltip(EntityPlayerMP playerIn, ItemStack stack) {
        List<String> list = Lists.newArrayList();
        String name = stack.getDisplayName();

        if (!stack.hasDisplayName() && stack.getItem() == Items.FILLED_MAP) {
            name = name + " #" + stack.getItemDamage();
        }

        list.add(name);
        int hideFlags = 0;

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("HideFlags", 99)) {
            hideFlags = stack.getTagCompound().getInteger("HideFlags");
        }

        if (stack.hasTagCompound()) {
            if ((hideFlags & 1) == 0) {
                NBTTagList tagList = stack.getEnchantmentTagList();
                if (stack.getItem() == Items.ENCHANTED_BOOK) {
                    tagList = ItemEnchantedBook.getEnchantments(stack);
                }

                for (int j = 0; j < tagList.tagCount(); ++j) {
                    NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(j);
                    int k = nbttagcompound.getShort("id");
                    int l = nbttagcompound.getShort("lvl");
                    Enchantment enchantment = Enchantment.getEnchantmentByID(k);

                    if (enchantment != null) {
                        list.add("  " + enchantment.getTranslatedName(l));
                    }
                }
            }

            if (stack.getTagCompound().hasKey("display", 10)) {
                NBTTagCompound tag1 = stack.getTagCompound().getCompoundTag("display");

                if (tag1.hasKey("color", 3)) {
                    list.add("  " + I18n.translateToLocal("item.dyed"));
                }

                if (tag1.getTagId("Lore") == 9) {
                    NBTTagList tagList3 = tag1.getTagList("Lore", 8);
                    if (!tagList3.hasNoTags()) {
                        for (int l1 = 0; l1 < tagList3.tagCount(); ++l1) {
                            list.add(tagList3.getStringTagAt(l1));
                        }
                    }
                }
            }
        }

        for (EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values()) {
            Multimap<String, AttributeModifier> multimap = stack.getAttributeModifiers(entityequipmentslot);

            if (!multimap.isEmpty() && (hideFlags & 2) == 0) {
                list.add("");
                list.add("  " + I18n.translateToLocal("item.modifiers." + entityequipmentslot.getName()));

                for (Map.Entry<String, AttributeModifier> entry : multimap.entries()) {
                    AttributeModifier attributemodifier = entry.getValue();
                    double d0 = attributemodifier.getAmount();
                    boolean flag = false;

                    if (playerIn != null) {
                        System.out.println("ATTRIBUTE MODIFIER ID: " + attributemodifier.getID());
                        System.out.println("ATTRIBUTE MODIFIER AMOUNT: " + d0);
                        if (ATTACK_DAMAGE_MODIFIER.equals(attributemodifier.getID())) {
                            System.out.println("DAMAGE");
                            d0 = d0 + playerIn.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
                            d0 = d0 + (double) EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
                            flag = true;
                        } else if (ATTACK_SPEED_MODIFIER.equals(attributemodifier.getID())) {
                            System.out.println("SPEED");
                            d0 += playerIn.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue();
                            flag = true;
                        }
                    }

                    double d1;

                    if (attributemodifier.getOperation() != 1 && attributemodifier.getOperation() != 2) {
                        d1 = d0;
                    } else {
                        d1 = d0 * 100.0D;
                    }

                    System.out.println("FINAL VALUE: " + d1);

                    if (flag) {
                        list.add("  " + I18n.translateToLocalFormatted("attribute.modifier.equals." + attributemodifier.getOperation(), DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + entry.getKey())));
                    } else if (d0 > 0.0D) {
                        list.add("  " + I18n.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier.getOperation(), DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + entry.getKey())));
                    } else if (d0 < 0.0D) {
                        d1 = d1 * -1.0D;
                        list.add("  " + I18n.translateToLocalFormatted("attribute.modifier.take." + attributemodifier.getOperation(), DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + entry.getKey())));
                    }
                }
            }
        }

        if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("Unbreakable") && (hideFlags & 4) == 0) {
            list.add("  " + I18n.translateToLocal("item.unbreakable"));
        }

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("CanDestroy", 9) && (hideFlags & 8) == 0) {
            NBTTagList tagList1 = stack.getTagCompound().getTagList("CanDestroy", 8);

            if (!tagList1.hasNoTags()) {
                list.add("");
                list.add("  " + I18n.translateToLocal("item.canBreak"));

                for (int j1 = 0; j1 < tagList1.tagCount(); ++j1) {
                    Block block = Block.getBlockFromName(tagList1.getStringTagAt(j1));

                    if (block != null) {
                        list.add("  " + block.getLocalizedName());
                    } else {
                        list.add("  " + "missingno");
                    }
                }
            }
        }

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("CanPlaceOn", 9) && (hideFlags & 16) == 0) {
            NBTTagList tagList2 = stack.getTagCompound().getTagList("CanPlaceOn", 8);

            if (!tagList2.hasNoTags()) {
                list.add("");
                list.add("  " + I18n.translateToLocal("item.canPlace"));

                for (int k1 = 0; k1 < tagList2.tagCount(); ++k1) {
                    Block block1 = Block.getBlockFromName(tagList2.getStringTagAt(k1));

                    if (block1 != null) {
                        list.add(block1.getLocalizedName());
                    } else {
                        list.add("missingno");
                    }
                }
            }
        }

        return list;
    }
}
