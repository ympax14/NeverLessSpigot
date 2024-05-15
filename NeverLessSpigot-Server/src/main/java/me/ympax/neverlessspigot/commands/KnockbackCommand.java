package me.ympax.neverlessspigot.commands;

import java.util.Arrays;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.cobblesword.nachospigot.knockback.KnockbackProfile;
import me.ympax.neverlessspigot.commons.ClickableBuilder;
import me.ympax.neverlessspigot.knockback.CraftKnockbackProfile;
import me.ympax.neverlessspigot.knockback.KnockbackConfig;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class KnockbackCommand extends Command {

	private final String separator = ChatColor.DARK_PURPLE.toString() + ChatColor.STRIKETHROUGH
			+ "-=-------------------------=-";

	public KnockbackCommand() {
		super("knockback");
		this.description = "Assists in knockback configuration.";
		this.setAliases(Arrays.asList("kb"));
		this.setPermission("neverlessspigot.knockback");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!testPermission(sender) || !(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;

		switch (args.length) {
			case 2: {
				switch (args[0].toLowerCase()) {
					case "create": {
						if (!isProfileName(args[1])) {
							CraftKnockbackProfile profile = new CraftKnockbackProfile(args[1]);
							KnockbackConfig.getKbProfiles().add(profile);
							profile.save();
							knockbackCommandMain(player);
							player.sendMessage(ChatColor.DARK_PURPLE + "The profile " + ChatColor.LIGHT_PURPLE + args[1]
									+ ChatColor.DARK_PURPLE + " has been created.");
							return true;
						} else {
							player.sendMessage(ChatColor.RED + "knockback profile with that name already exists.");
						}
						break;
					}
					case "delete": {
						if (KnockbackConfig.getCurrentKb().getName().equalsIgnoreCase(args[1])) {
							knockbackCommandMain(player);
							player.sendMessage(ChatColor.RED + "You cannot delete the profile that is being used.");
							return false;
						}
						if (KnockbackConfig.getKbProfiles()
								.removeIf(profile -> profile.getName().equalsIgnoreCase(args[1]))) {
							KnockbackConfig.set("knockback.profiles." + args[1], null);
							knockbackCommandMain(player);
							player.sendMessage(ChatColor.DARK_PURPLE + "The profile " + ChatColor.LIGHT_PURPLE + args[1]
									+ ChatColor.DARK_PURPLE + " has been removed.");
							return true;
						} else {
							player.sendMessage(ChatColor.RED + "This profile doesn't exist.");
						}
						break;
					}
					case "load": {
						KnockbackProfile profile = KnockbackConfig.getKbProfileByName(args[1]);
						if (profile != null) {
							if (KnockbackConfig.getCurrentKb().getName().equalsIgnoreCase(args[1])) {
								player.sendMessage(ChatColor.RED + "This profile is loaded.");
								return false;
							}
							KnockbackConfig.setCurrentKb(profile);
							KnockbackConfig.set("knockback.current", profile.getName());
							KnockbackConfig.save();
							knockbackCommandMain(player);
							player.sendMessage(ChatColor.DARK_PURPLE + "The profile " + ChatColor.LIGHT_PURPLE + args[1]
									+ ChatColor.DARK_PURPLE + " has been loaded.");
							return true;
						} else {
							player.sendMessage(ChatColor.RED + "This profile doesn't exist.");
						}
						break;
					}
					case "view": {
						KnockbackProfile profile = KnockbackConfig.getKbProfileByName(args[1]);
						if (profile != null) {
							knockbackCommandView(player, profile);
							return true;
						}
						player.sendMessage(ChatColor.RED + "This profile doesn't exist.");
						break;
					}
					case "projectile": {
						KnockbackProfile profile = KnockbackConfig.getKbProfileByName(args[1]);
						if (profile != null) {
							knockbackCommandViewProjectiles(player, profile);
							return true;
						}
						player.sendMessage(ChatColor.RED + "This profile doesn't exist.");
						break;
					}
					default: {
						knockbackCommandMain(player);
					}
				}
				break;
			}
			case 3: {
				switch (args[0].toLowerCase()) {
					case "set": {
						KnockbackProfile profile = KnockbackConfig.getKbProfileByName(args[1]);
						if (profile == null) {
							sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
							return false;
						}
						Player target = Bukkit.getPlayer(args[2]);
						if (target == null) {
							sender.sendMessage(ChatColor.RED + "That player is not online.");
							return false;
						}
						target.setKnockbackProfile(profile);
						break;
					}
				}
				break;
			}
			case 4: {
				if ("edit".equalsIgnoreCase(args[0])) {
					KnockbackProfile profile = KnockbackConfig.getKbProfileByName(args[1].toLowerCase());
					if (profile == null) {
						player.sendMessage(ChatColor.RED + "This profile doesn't exist.");
						return false;
					}
					switch (args[2].toLowerCase()) {
						case "inherit-horizontal-strength": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setInheritHorizontalStrength(value);
							profile.save();
							knockbackCommandView(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "inherit-vertical-strength": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setInheritVerticalStrength(value);
							profile.save();
							knockbackCommandView(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "horizontal": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setHorizontal(value);
							profile.save();
							knockbackCommandView(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "vertical": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setVertical(value);
							profile.save();
							knockbackCommandView(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "inherit-horizontal": {
							if (!args[3].equalsIgnoreCase("true") && !args[3].equalsIgnoreCase("false")) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a boolean.");
								return false;
							}
							boolean value = Boolean.parseBoolean(args[3]);
							profile.setInheritHorizontal(value);
							profile.save();
							knockbackCommandView(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "inherit-vertical": {
							if (!args[3].equalsIgnoreCase("true") && !args[3].equalsIgnoreCase("false")) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a boolean.");
								return false;
							}
							boolean value = Boolean.parseBoolean(args[3]);
							profile.setInheritVertical(value);
							profile.save();
							knockbackCommandView(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "vertical-max": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setVerticalMax(value);
							profile.save();
							knockbackCommandView(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "limit-vertical": {
							if (!args[3].equalsIgnoreCase("true") && !args[3].equalsIgnoreCase("false")) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a boolean.");
								return false;
							}
							boolean value = Boolean.parseBoolean(args[3]);
							profile.setLimitVertical(value);
							profile.save();
							knockbackCommandView(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "stop-sprint": {
							if ("true".equalsIgnoreCase(args[3]) || "false".equalsIgnoreCase(args[3])) {
								profile.setStopSprint(Boolean.parseBoolean(args[3]));
								profile.save();
								knockbackCommandView(player, profile);
								player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
								return true;
							} else {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a boolean.");
							}
							break;
						}
						case "rod-horizontal": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setRodHorizontal(value);
							profile.save(true);
							knockbackCommandViewProjectiles(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "rod-vertical": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setRodVertical(value);
							profile.save(true);
							knockbackCommandViewProjectiles(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "arrow-horizontal": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setArrowHorizontal(value);
							profile.save(true);
							knockbackCommandViewProjectiles(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "arrow-vertical": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setArrowVertical(value);
							profile.save(true);
							knockbackCommandViewProjectiles(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "pearl-horizontal": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setPearlHorizontal(value);
							profile.save(true);
							knockbackCommandViewProjectiles(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "pearl-vertical": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setPearlVertical(value);
							profile.save(true);
							knockbackCommandViewProjectiles(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "snowball-horizontal": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setSnowballHorizontal(value);
							profile.save(true);
							knockbackCommandViewProjectiles(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "snowball-vertical": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setSnowballVertical(value);
							profile.save(true);
							knockbackCommandViewProjectiles(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "egg-horizontal": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setEggHorizontal(value);
							profile.save(true);
							knockbackCommandViewProjectiles(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "egg-vertical": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setEggVertical(value);
							profile.save(true);
							knockbackCommandViewProjectiles(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "ground-horizontal-multiplier": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setGroundHorizontalMultiplier(value);
							profile.save();
							knockbackCommandView(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "ground-vertical-multiplier": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setGroundVerticalMultiplier(value);
							profile.save();
							knockbackCommandView(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "sprint-horizontal-multiplier": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setSprintHorizontalMultiplier(value);
							profile.save();
							knockbackCommandView(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "sprint-vertical-multiplier": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setSprintVerticalMultiplier(value);
							profile.save();
							knockbackCommandView(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "hitdelay": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							int value = Integer.parseInt(args[3]);
							profile.setHitDelay(value);
							profile.save();
							knockbackCommandView(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "combo-mode": {
							if ("true".equalsIgnoreCase(args[3]) || "false".equalsIgnoreCase(args[3])) {
								profile.setComboMode(Boolean.parseBoolean(args[3]));
								profile.save();
								knockbackCommandView(player, profile);
								player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
								return true;
							} else {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a boolean.");
							}
							break;
						}
						case "combo-ticks": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							int value = Integer.parseInt(args[3]);
							profile.setComboTicks(value);
							profile.save();
							knockbackCommandView(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "combo-velocity": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setComboVelocity(value);
							profile.save();
							knockbackCommandView(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						case "combo-height": {
							if (!NumberUtils.isNumber(args[3])) {
								player.sendMessage(ChatColor.DARK_RED + args[3] + ChatColor.RED + " is not a number.");
								return false;
							}
							double value = Double.parseDouble(args[3]);
							profile.setComboHeight(value);
							profile.save();
							knockbackCommandView(player, profile);
							player.sendMessage(ChatColor.GREEN + "Value edited and saved.");
							break;
						}
						//combo
					}
				}
				break;
			}
			default: {
				knockbackCommandMain(player);
			}
		}
		return false;
	}

	private void knockbackCommandMain(Player player) {
		player.sendMessage(separator + "\n" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Knockback profile list:\n");

		for (KnockbackProfile profile : KnockbackConfig.getKbProfiles()) {
			boolean current = KnockbackConfig.getCurrentKb().getName().equals(profile.getName());

			TextComponent line = new ClickableBuilder(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "("
					+ ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "➜" + ChatColor.DARK_GRAY + ChatColor.BOLD + ") ")
					.setHover(ChatColor.LIGHT_PURPLE + "[Click here to apply this profile to a player] ")
					.setClick("/kb set " + profile.getName() + " ", ClickEvent.Action.SUGGEST_COMMAND).build();
			TextComponent load = new ClickableBuilder(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "("
					+ (current ? ChatColor.RED : ChatColor.GREEN) + "✔" + ChatColor.DARK_GRAY + ChatColor.BOLD + ") ")
					.setHover(current ? ChatColor.RED + "[This profile is loaded] "
							: ChatColor.GREEN + "[Click here to load this profile]")
					.setClick("/kb load " + profile.getName(), ClickEvent.Action.RUN_COMMAND).build();
			TextComponent delete = new ClickableBuilder(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "("
					+ ChatColor.RED + ChatColor.BOLD + "✖" + ChatColor.DARK_GRAY + ChatColor.BOLD + ")  ")
					.setHover(ChatColor.RED + "[Click here to delete this profile] ")
					.setClick("/kb delete " + profile.getName(), ClickEvent.Action.RUN_COMMAND).build();
			TextComponent edit = new ClickableBuilder(
					ChatColor.LIGHT_PURPLE + profile.getName() + ChatColor.DARK_GRAY + ChatColor.BOLD + " ["
							+ ChatColor.GOLD + ChatColor.BOLD + "✎" + ChatColor.DARK_GRAY + ChatColor.BOLD + "]")
					.setHover(ChatColor.GOLD + "[Click here to edit this profile]")
					.setClick("/kb view " + profile.getName(), ClickEvent.Action.RUN_COMMAND).build();
			player.spigot().sendMessage(line, load, delete, edit);
		}

		player.spigot()
				.sendMessage(new ClickableBuilder(
						"\n" + ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "[" + ChatColor.DARK_PURPLE
								+ ChatColor.BOLD + "Create new profile" + ChatColor.DARK_GRAY + ChatColor.BOLD + "]")
						.setHover(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD
								+ "[Click here to create a new profile]")
						.setClick("/kb create ", ClickEvent.Action.SUGGEST_COMMAND).build());
		player.sendMessage(separator);
	}

	private void knockbackCommandView(Player player, KnockbackProfile profile) {
		player.sendMessage(separator + "\n" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Knockback values:\n");
		for (String values : profile.getKnockbackValues()) {
			TextComponent value = new TextComponent(ChatColor.GOLD + "» " + ChatColor.LIGHT_PURPLE + values);
			TextComponent edit = new ClickableBuilder(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + " ["
					+ ChatColor.GOLD + ChatColor.BOLD + "✎" + ChatColor.DARK_GRAY + ChatColor.BOLD + "]")
					.setHover(ChatColor.YELLOW + "[Click to edit " + ChatColor.LIGHT_PURPLE + values.split(":")[0]
							+ ChatColor.YELLOW + " value]")
					.setClick(
							"/kb edit " + profile.getName() + " "
									+ values.replace(ChatColor.GOLD.toString() + ": ", " "),
							ClickEvent.Action.SUGGEST_COMMAND)
					.build();
			player.spigot().sendMessage(value, edit);
		}
		TextComponent page = new ClickableBuilder("\n" + ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "["
				+ ChatColor.RED + ChatColor.BOLD + "⬑" + ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "] ")
				.setHover(ChatColor.YELLOW + "[Click to back]")
				.setClick("/kb", ClickEvent.Action.RUN_COMMAND).build();
		TextComponent projectiles = new ClickableBuilder(
				ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + " [" + ChatColor.DARK_PURPLE + ChatColor.BOLD
						+ "Edit projectiles" + ChatColor.DARK_GRAY + ChatColor.BOLD + "]")
				.setClick("/kb projectile " + profile.getName(), ClickEvent.Action.RUN_COMMAND)
				.setHover(ChatColor.YELLOW + "[Click to edit projectiles]").build();
		player.spigot().sendMessage(page, projectiles);
		player.sendMessage(separator);
	}

	private void knockbackCommandViewProjectiles(Player player, KnockbackProfile profile) {
		player.sendMessage(separator + "\n" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Projectiles values: \n");
		for (String values : profile.getProjectilesValues()) {
			TextComponent value = new TextComponent(ChatColor.GOLD + "» " + ChatColor.LIGHT_PURPLE + values);
			TextComponent edit = new ClickableBuilder(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + " ["
					+ ChatColor.GOLD + ChatColor.BOLD + "✎" + ChatColor.DARK_GRAY + ChatColor.BOLD + "]")
					.setHover(ChatColor.YELLOW + "[Click to edit " + ChatColor.LIGHT_PURPLE + values.split(":")[0]
							+ ChatColor.YELLOW + " value]")
					.setClick(
							"/kb edit " + profile.getName() + " "
									+ values.replace(ChatColor.GOLD.toString() + ": ", " "),
							ClickEvent.Action.SUGGEST_COMMAND)
					.build();
			player.sendMessage(value, edit);
		}
		TextComponent page = new ClickableBuilder("\n" + ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "["
				+ ChatColor.RED + ChatColor.BOLD + "⬑" + ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "] ")
				.setHover(ChatColor.YELLOW + "[Click to back]")
				.setClick("/kb", ClickEvent.Action.RUN_COMMAND).build();
		TextComponent knockback = new ClickableBuilder(
				ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + " [" + ChatColor.DARK_PURPLE + ChatColor.BOLD
						+ "Edit knockback" + ChatColor.DARK_GRAY + ChatColor.BOLD + "]")
				.setHover(ChatColor.YELLOW + "[Click here to edit knockback]")
				.setClick("/kb view " + profile.getName(), ClickEvent.Action.RUN_COMMAND).build();
		player.spigot().sendMessage(page, knockback);
		player.sendMessage(separator);

	}

	private boolean isProfileName(String name) {
		for (KnockbackProfile profile : KnockbackConfig.getKbProfiles()) {
			if (profile.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
}
