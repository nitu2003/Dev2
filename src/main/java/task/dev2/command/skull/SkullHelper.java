package task.dev2.command.skull;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import task.dev2.Dev2;
import task.dev2.api.CreativeTabAPI;
import task.dev2.util.ItemUtils;

public class SkullHelper {

	public static String getSkullOwner(ItemStack stack) {
		if(isPlayerSkull(stack)) {
			NBTTagCompound nbt = ItemUtils.getOrCreateTag(stack);
			String owner = nbt.getString("SkullOwner");
			if(owner.equals("")) {
				NBTTagCompound skullowner = (NBTTagCompound) nbt.getTag("SkullOwner");
				if(skullowner != null) owner = skullowner.getString("Name");
			}
			return owner;
		} else {
			return "";
		}
	}
	
	public static ItemStack setSkullOwner(ItemStack stack, String skullowner) {
		if(stack == null) stack = new ItemStack(Items.SKULL, 1, 3);
		if(isPlayerSkull(stack)) {
			NBTTagCompound nbt = ItemUtils.getOrCreateTag(stack);
			nbt.setString("SkullOwner", skullowner);
		}
		return stack;
	}
	
	public static ItemStack setSkullOwner(ItemStack stack, String id, String texture) {
		if(stack == null) stack = new ItemStack(Items.SKULL, 1, 3);
		if(isPlayerSkull(stack)) {
			NBTTagCompound root = ItemUtils.getOrCreateTag(stack);
			NBTTagCompound skullowner = new NBTTagCompound();
			NBTTagCompound properties = new NBTTagCompound();
			NBTTagList textures = new NBTTagList();
			NBTTagCompound value = new NBTTagCompound();
			if(root.hasKey("SkullOwner")) skullowner = (NBTTagCompound) root.getTag("SkullOwner");
			skullowner.setString("Id", id);
			value.setString("Value", texture);
			textures.appendTag(value);
			properties.setTag("textures", textures);
			skullowner.setTag("Properties", properties);
			root.setTag("SkullOwner", skullowner);
		}
		
		return stack;
	}
	
	public static String serialize(ItemStack skull) {
		NBTTagCompound root = ItemUtils.getOrCreateTag(skull);
		NBTTagCompound skullowner = root.getCompoundTag("SkullOwner");
		String skullid = skullowner.getString("Id");
		NBTTagCompound properties = skullowner.getCompoundTag("Properties");
		NBTTagList textures = properties.getTagList("textures", Constants.NBT.TAG_COMPOUND);
		NBTTagCompound property = textures.getCompoundTagAt(0);
		String texture = property.getString("Value");
		return skullid+"|"+texture+"|"+skull.getDisplayName();
	}
	
	public static ItemStack deserialize(String skullcode) {
		String[] parts = skullcode.split("\\|");
		if(parts.length > 3) {
			Dev2.warn("Failed to deserialize the skull code, too many parts: {}", skullcode);
		}
		else if(parts.length < 2) {
			Dev2.warn("Failed to deserialize the skull code, too few parts: {}", skullcode);
		}
		else {
			String skullid = parts[0];
			String texture = parts[1];
			String display = null;
			if(parts.length == 3) display = parts[2];
			ItemStack skull = SkullHelper.setSkullOwner(null, skullid, texture);
			if(display != null) skull.setStackDisplayName(display);
			return skull;
		}
		return ItemStack.EMPTY;
	}
	
	public static boolean isPlayerSkull(ItemStack stack) {
		return stack.getItem() == Items.SKULL && stack.getItemDamage() == 3;
	}
	
}
