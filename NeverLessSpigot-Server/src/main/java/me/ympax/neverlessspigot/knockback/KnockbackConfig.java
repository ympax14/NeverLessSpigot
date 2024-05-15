package me.ympax.neverlessspigot.knockback;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.sugarcanemc.sugarcane.util.yaml.YamlCommenter;

import com.google.common.base.Throwables;

import dev.cobblesword.nachospigot.knockback.KnockbackProfile;
import me.ympax.neverlessspigot.NeverLessSpigot;

public class KnockbackConfig {
	private static final Logger LOGGER = LogManager.getLogger(KnockbackConfig.class);
	private static File CONFIG_FILE;
	protected static final YamlCommenter c = new YamlCommenter();
	private static final String HEADER = "This is the knockback configuration file for NeverLessSpigot.\n";
	static YamlConfiguration config;

	private static volatile KnockbackProfile currentKb;
	private static volatile Set<KnockbackProfile> kbProfiles = new HashSet<>();

	public static void init(File configFile) {
		CONFIG_FILE = configFile;
		config = new YamlConfiguration();
		try {
			NeverLessSpigot.LOGGER.info("Loading NeverLessSpigot knockback config from " + configFile.getName());
			config.load(CONFIG_FILE);
		} catch (IOException ignored) {
		} catch (InvalidConfigurationException ex) {
			LOGGER.log(Level.ERROR, "Could not load knockback.yml, please correct your syntax errors", ex);
			throw Throwables.propagate(ex);
		}
		config.options().copyDefaults(true);
		c.setHeader(HEADER);

		Set<String> keys = getKeys("knockback.profiles");
		
		if (!keys.contains("Vanilla")) {
			final KnockbackProfile vanillaProfile = new CraftKnockbackProfile("Vanilla");
			vanillaProfile.save(true);
		}
		
		if (!keys.contains("Zenith")) {
			final KnockbackProfile zenithProfile = new CraftKnockbackProfile("Zenith");

			zenithProfile.setStopSprint(true);
			
			zenithProfile.setRodHorizontal(1.2);
			zenithProfile.setRodVertical(1.0);
			zenithProfile.setSnowballHorizontal(1.2);
			zenithProfile.setSnowballHorizontal(1.0);
			zenithProfile.setEggHorizontal(1.2);
			zenithProfile.setEggVertical(1.0);
			zenithProfile.setArrowHorizontal(1.2);
			zenithProfile.setArrowVertical(1.0);
			
			zenithProfile.setHorizontal(0.55);
			zenithProfile.setVertical(0.34);
			
			zenithProfile.setInheritHorizontal(true);
			zenithProfile.setInheritVertical(false);

			zenithProfile.setInheritHorizontalStrength(1.2);
			zenithProfile.setInheritVerticalStrength(0.025);
			
			zenithProfile.setGroundHorizontalMultiplier(1.1);
			zenithProfile.setGroundVerticalMultiplier(1.0);

			zenithProfile.setSprintHorizontalMultiplier(1.2);
			zenithProfile.setSprintVerticalMultiplier(1.0);

			zenithProfile.setHitDelay(20);

			zenithProfile.setComboMode(false);
			zenithProfile.setComboTicks(10);
			zenithProfile.setComboVelocity(-0.05);
			zenithProfile.setComboHeight(2.5);
			
			zenithProfile.save(true);
		}
		
		// Reload keys
		keys = getKeys("knockback.profiles");
		
		for (String key : keys) {
			final String path = "knockback.profiles." + key;
			CraftKnockbackProfile profile = (CraftKnockbackProfile) getKbProfileByName(key);
			if (profile == null) {
				profile = new CraftKnockbackProfile(key);
				kbProfiles.add(profile);
			}
			profile.setStopSprint(getBoolean(path + ".stop-sprint", true));
			profile.setHorizontal(getDouble(path + ".horizontal", 0.4D));
			profile.setVertical(getDouble(path + ".vertical", 0.4D));
			profile.setVerticalMax(getDouble(path + ".vertical-max", 0.4D));
			profile.setLimitVertical(getBoolean(path + ".limit-vertical", false));

			profile.setInheritHorizontal(getBoolean(path + ".inherit-horizontal", true));
			profile.setInheritVertical(getBoolean(path + ".inherit-vertical", false));
			profile.setInheritHorizontalStrength(getDouble(path + ".inherit-horizontal-strength", 1.0));
			profile.setInheritVerticalStrength(getDouble(path + ".inherit-vertical-strength", 1.0));

			profile.setGroundHorizontalMultiplier(getDouble(path + ".ground-horizontal-multiplier", 1.1));
			profile.setGroundVerticalMultiplier(getDouble(path + ".ground-vertical-multiplier", 1.0));

			profile.setSprintHorizontalMultiplier(getDouble(path + ".sprint-horizontal-multiplier", 1.2));
			profile.setSprintVerticalMultiplier(getDouble(path + ".sprint-vertical-multiplier", 1.0));

			profile.setHitDelay(getInt(path + ".hitdelay", 20));
			profile.setComboMode(getBoolean(path + ".combo-mode", false));
			profile.setComboTicks(getInt(path + ".combo-ticks", 10));
			profile.setComboVelocity(getDouble(path + ".combo-velocity", -0.05));
			profile.setComboHeight(getDouble(path + ".combo-height", 2.5));

			profile.setRodHorizontal(getDouble(path + ".projectiles.rod.horizontal", 0.4D));
			profile.setRodVertical(getDouble(path + ".projectiles.rod.vertical", 0.4D));
			profile.setArrowHorizontal(getDouble(path + ".projectiles.arrow.horizontal", 0.4D));
			profile.setArrowVertical(getDouble(path + ".projectiles.arrow.vertical", 0.4D));
			profile.setPearlHorizontal(getDouble(path + ".projectiles.pearl.horizontal", 0.4D));
			profile.setPearlVertical(getDouble(path + ".projectiles.pearl.vertical", 0.4D));
			profile.setSnowballHorizontal(getDouble(path + ".projectiles.snowball.horizontal", 0.4D));
			profile.setSnowballVertical(getDouble(path + ".projectiles.snowball.vertical", 0.4D));
			profile.setEggHorizontal(getDouble(path + ".projectiles.egg.horizontal", 0.4D));
			profile.setEggVertical(getDouble(path + ".projectiles.egg.vertical", 0.4D));
		}
		currentKb = getKbProfileByName(getString("knockback.current", "Zenith"));
		if (currentKb == null) {
			NeverLessSpigot.LOGGER.warn("Knockback profile selected was not found, using profile 'Zenith' for now!");
			currentKb = getKbProfileByName("Zenith");
			
			NeverLessSpigot.LOGGER.info("Setting default knockback as 'Zenith'...");
			set("knockback.current", "Zenith");
		}
		save();
	}

	public static KnockbackProfile getCurrentKb() {
		if (currentKb == null) {
			setCurrentKb(getKbProfileByName("Zenith"));
		}
		return currentKb;
	}

	public static void setCurrentKb(KnockbackProfile kb) {
		currentKb = kb;
	}

	public static KnockbackProfile getKbProfileByName(String name) {
		for (KnockbackProfile profile : kbProfiles) {
			if (profile.getName().equalsIgnoreCase(name)) {
				return profile;
			}
		}
		return null;
	}

	public static Set<KnockbackProfile> getKbProfiles() {
		return kbProfiles;
	}

	public static void save() {
		try {
			config.save(CONFIG_FILE);
		} catch (IOException ex) {
			LOGGER.log(Level.ERROR, "Could not save " + CONFIG_FILE, ex);
		}
	}

	public static void set(String path, Object val) {
		config.set(path, val);

		save();
	}

	public static Set<String> getKeys(String path) {
		if (!config.isConfigurationSection(path)) {
			config.createSection(path);
			return new HashSet<>();
		}

		return config.getConfigurationSection(path).getKeys(false);
	}

	private static boolean getBoolean(String path, boolean def) {
		config.addDefault(path, def);
		return config.getBoolean(path, config.getBoolean(path));
	}

	private static double getDouble(String path, double def) {
		config.addDefault(path, def);
		return config.getDouble(path, config.getDouble(path));
	}

	private static float getFloat(String path, float def) {
		config.addDefault(path, def);
		return config.getFloat(path, config.getFloat(path));
	}

	private static int getInt(String path, int def) {
		config.addDefault(path, def);
		return config.getInt(path, config.getInt(path));
	}

	private static <T> List getList(String path, T def) {
		config.addDefault(path, def);
		return config.getList(path, config.getList(path));
	}

	private static String getString(String path, String def) {
		config.addDefault(path, def);
		return config.getString(path, config.getString(path));
	}
}
