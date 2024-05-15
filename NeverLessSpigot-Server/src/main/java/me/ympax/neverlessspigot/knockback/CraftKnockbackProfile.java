package me.ympax.neverlessspigot.knockback;

import dev.cobblesword.nachospigot.knockback.KnockbackProfile;
import net.md_5.bungee.api.ChatColor;

public class CraftKnockbackProfile implements KnockbackProfile {

	private String name;
	private final String saveProfilePath;

	private double horizontal = 0.33;
	private double vertical = 0.36;

	private boolean limitVertical = false;
	private double verticalMax = 0.4D;

	private boolean inheritHorizontal = true;
	private boolean inheritVertical = false;

	private double inheritHorizontalStrength = 1.0;
	private double inheritVerticalStrength = 1.0;

	private double groundHorizontalMultiplier = 1.1;
	private double groundVerticalMultiplier = 1.0;

	private double sprintHorizontalMultiplier = 1.2;
	private double sprintVerticalMultiplier = 1.0;

	private int hitDelay = 20;
   	private boolean comboMode = false;
   	private int comboTicks = 10;
   	private double comboVelocity = -0.05;
   	private double comboHeight = 2.5;

	private boolean stopSprint = true;

	private double rodHorizontal = 0.4D;
	private double rodVertical = 0.4D;
	private double arrowHorizontal = 0.4D;
	private double arrowVertical = 0.4D;
	private double pearlHorizontal = 0.4D;
	private double pearlVertical = 0.4D;
	private double snowballHorizontal = 0.4D;
	private double snowballVertical = 0.4D;
	private double eggHorizontal = 0.4D;
	private double eggVertical = 0.4D;

	public CraftKnockbackProfile(String name) {
		this.name = name;
		this.saveProfilePath = "knockback.profiles." + this.name;
	}

	@Override
	public void save() {
		save(false);
	}
	
	private void set(String savePath, Object value) {
		KnockbackConfig.set(saveProfilePath + savePath, value);
	}

	@Override
	public void save(boolean projectiles) {

		set(".stop-sprint", this.stopSprint);
		set(".inherit-horizontal", this.inheritHorizontal);
		set(".inherit-horizontal-strength", this.inheritHorizontalStrength);
		set(".inherit-vertical", this.inheritVertical);
		set(".inherit-vertical-strength", this.inheritVerticalStrength);
		set(".horizontal", this.horizontal);
		set(".vertical", this.vertical);
		set(".vertical-max", this.verticalMax);
		set(".limit-vertical", this.limitVertical);
		set(".ground-horizontal-multiplier", this.groundHorizontalMultiplier);
		set(".ground-vertical-multiplier", this.groundVerticalMultiplier);
		set(".sprint-horizontal-multiplier", this.sprintHorizontalMultiplier);
		set(".sprint-vertical-multiplier", this.sprintVerticalMultiplier);
		set(".hitdelay", this.hitDelay);
		set(".combo-mode", this.comboMode);
		set(".combo-ticks", this.comboTicks);
		set(".combo-velocity", this.comboVelocity);
		set(".combo-height", this.comboHeight);
		
		if (projectiles) {
			set(".projectiles.rod.horizontal", this.rodHorizontal);
			set(".projectiles.rod.vertical", this.rodVertical);
			set(".projectiles.arrow.horizontal", this.arrowHorizontal);
			set(".projectiles.arrow.vertical", this.arrowVertical);
			set(".projectiles.pearl.horizontal", this.pearlHorizontal);
			set(".projectiles.pearl.vertical", this.pearlVertical);
			set(".projectiles.snowball.horizontal", this.snowballHorizontal);
			set(".projectiles.snowball.vertical", this.snowballVertical);
			set(".projectiles.egg.horizontal", this.eggHorizontal);
			set(".projectiles.egg.vertical", this.eggVertical);
		}

		KnockbackConfig.save();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public double getHorizontal() {
		return horizontal;
	}

	@Override
	public void setHorizontal(double horizontal) {
		this.horizontal = horizontal;
	}

	@Override
	public double getVertical() {
		return vertical;
	}

	@Override
	public void setVertical(double vertical) {
		this.vertical = vertical;
	}

	@Override
	public double getVerticalMax() {
		return verticalMax;
	}

	@Override
	public void setVerticalMax(double verticalMax) {
		this.verticalMax = verticalMax;
	}

	@Override
	public boolean isStopSprint() {
		return stopSprint;
	}

	@Override
	public void setStopSprint(boolean stopSprint) {
		this.stopSprint = stopSprint;
	}

	@Override
	public double getRodHorizontal() {
		return rodHorizontal;
	}

	@Override
	public void setRodHorizontal(double rodHorizontal) {
		this.rodHorizontal = rodHorizontal;
	}

	@Override
	public double getRodVertical() {
		return rodVertical;
	}

	@Override
	public void setRodVertical(double rodVertical) {
		this.rodVertical = rodVertical;
	}

	@Override
	public double getArrowHorizontal() {
		return arrowHorizontal;
	}

	@Override
	public void setArrowHorizontal(double arrowHorizontal) {
		this.arrowHorizontal = arrowHorizontal;
	}

	@Override
	public double getArrowVertical() {
		return arrowVertical;
	}

	@Override
	public void setArrowVertical(double arrowVertical) {
		this.arrowVertical = arrowVertical;
	}

	@Override
	public double getPearlHorizontal() {
		return pearlHorizontal;
	}

	@Override
	public void setPearlHorizontal(double pearlHorizontal) {
		this.pearlHorizontal = pearlHorizontal;
	}

	@Override
	public double getPearlVertical() {
		return pearlVertical;
	}

	@Override
	public void setPearlVertical(double pearlVertical) {
		this.pearlVertical = pearlVertical;
	}

	@Override
	public double getSnowballHorizontal() {
		return snowballHorizontal;
	}

	@Override
	public void setSnowballHorizontal(double snowballHorizontal) {
		this.snowballHorizontal = snowballHorizontal;
	}

	@Override
	public double getSnowballVertical() {
		return snowballVertical;
	}

	@Override
	public void setSnowballVertical(double snowballVertical) {
		this.snowballVertical = snowballVertical;
	}

	@Override
	public double getEggHorizontal() {
		return eggHorizontal;
	}

	@Override
	public void setEggHorizontal(double eggHorizontal) {
		this.eggHorizontal = eggHorizontal;
	}

	@Override
	public double getEggVertical() {
		return eggVertical;
	}

	@Override
	public void setEggVertical(double eggVertical) {
		this.eggVertical = eggVertical;
	}

	@Override
	public String[] getKnockbackValues() {
		return new String[] { "Horizontal" + ChatColor.GOLD + ": " + this.horizontal,
				"Vertical" + ChatColor.GOLD + ": " + this.vertical,
				"Limit-Vertical" + ChatColor.GOLD + ": " + this.limitVertical,
				"Vertical-Max" + ChatColor.GOLD + ": " + this.verticalMax,
				"Inherit-Horizontal" + ChatColor.GOLD + ": " + this.inheritHorizontal,
				"Inherit-Vertical" + ChatColor.GOLD + ": " + this.inheritVertical,
				"Inherit-Horizontal-Strength" + ChatColor.GOLD + ": " + this.inheritHorizontalStrength,
				"Inherit-Vertical-Strength" + ChatColor.GOLD + ": " + this.inheritVerticalStrength,
				"Stop-Sprint" + ChatColor.GOLD + ": " + this.stopSprint,
				"Ground-Horizontal-Multiplier" + ChatColor.GOLD + ": " + this.groundHorizontalMultiplier,
				"Ground-Vertical-Multiplier" + ChatColor.GOLD + ": " + this.groundVerticalMultiplier,
				"Sprint-Horizontal-Multiplier" + ChatColor.GOLD + ": " + this.sprintHorizontalMultiplier,
				"Sprint-Vertical-Multiplier" + ChatColor.GOLD + ": " + this.sprintVerticalMultiplier,
				"HitDelay" + ChatColor.GOLD + ": " + this.hitDelay,
				"Combo-Mode" + ChatColor.GOLD + ": " + this.comboMode,
				"Combo-Ticks" + ChatColor.GOLD + ": " + this.comboTicks,
				"Combo-Velocity" + ChatColor.GOLD + ": " + this.comboVelocity,
				"Combo-Height" + ChatColor.GOLD + ": " + this.comboHeight };
	}

	@Override
	public String[] getProjectilesValues() {
		return new String[] { "Rod-Horizontal" + ChatColor.GOLD + ": " + this.rodHorizontal,
				"Rod-Vertical" + ChatColor.GOLD + ": " + this.rodVertical,
				"Arrow-Horizontal" + ChatColor.GOLD + ": " + this.arrowHorizontal,
				"Arrow-Vertical" + ChatColor.GOLD + ": " + this.arrowVertical,
				"Pearl-Horizontal" + ChatColor.GOLD + ": " + this.pearlHorizontal,
				"Pearl-Vertical" + ChatColor.GOLD + ": " + this.pearlVertical,
				"Snowball-Horizontal" + ChatColor.GOLD + ": " + this.snowballHorizontal,
				"Snowball-Vertical" + ChatColor.GOLD + ": " + this.snowballVertical,
				"Egg-Horizontal" + ChatColor.GOLD + ": " + this.eggHorizontal,
				"Egg-Vertical" + ChatColor.GOLD + ": " + this.eggVertical, };
	}

	@Override
	public boolean getLimitVertical() {
		return this.limitVertical;
	}

	@Override
	public void setLimitVertical(boolean limitVertical) {
		this.limitVertical = limitVertical;
	}

	@Override
	public boolean getInheritHorizontal() {
		return this.inheritHorizontal;
	}

	@Override
	public void setInheritHorizontal(boolean inheritHorizontal) {
		this.inheritHorizontal = inheritHorizontal;
	}

	@Override
	public boolean getInheritVertical() {
		return this.inheritVertical;
	}

	@Override
	public void setInheritVertical(boolean inheritVertical) {
		this.inheritVertical = inheritVertical;
	}

	@Override
	public double getInheritHorizontalStrength() {
		return this.inheritHorizontalStrength;
	}

	@Override
	public void setInheritHorizontalStrength(double inheritHorizontalStrength) {
		this.inheritHorizontalStrength = inheritHorizontalStrength;
	}

	@Override
	public double getInheritVerticalStrength() {
		return this.inheritVerticalStrength;
	}

	@Override
	public void setInheritVerticalStrength(double inheritVerticalStrength) {
		this.inheritVerticalStrength = inheritVerticalStrength;
	}

	@Override
	public double getGroundHorizontalMultiplier() {
		return this.groundHorizontalMultiplier;
	}

	@Override
	public void setGroundHorizontalMultiplier(double groundHorizontalMultiplier) {
		this.groundHorizontalMultiplier = groundHorizontalMultiplier;
	}

	@Override
	public double getGroundVerticalMultiplier() {
		return this.groundVerticalMultiplier;
	}

	@Override
	public void setGroundVerticalMultiplier(double groundVerticalMultiplier) {
		this.groundVerticalMultiplier = groundVerticalMultiplier;
	}

	@Override
	public double getSprintHorizontalMultiplier() {
		return this.sprintHorizontalMultiplier;
	}

	@Override
	public void setSprintHorizontalMultiplier(double sprintHorizontalMultiplier) {
		this.sprintHorizontalMultiplier = sprintHorizontalMultiplier;
	}

	@Override
	public double getSprintVerticalMultiplier() {
		return this.sprintVerticalMultiplier;
	}

	@Override
	public void setSprintVerticalMultiplier(double sprintVerticalMultiplier) {
		this.sprintVerticalMultiplier = sprintVerticalMultiplier;
	}

	@Override
	public int getHitDelay() {
		return this.hitDelay;
	}

	@Override
	public void setHitDelay(int hitDelay) {
		this.hitDelay = hitDelay;
	}

	@Override
	public boolean getComboMode() {
		return this.comboMode;
	}

	@Override
	public void setComboMode(boolean comboMode) {
		this.comboMode = comboMode;
	}

	@Override
	public int getComboTicks() {
		return this.comboTicks;
	}

	@Override
	public void setComboTicks(int comboTicks) {
		this.comboTicks = comboTicks;
	}

	@Override
	public double getComboVelocity() {
		return this.comboVelocity;
	}

	@Override
	public void setComboVelocity(double comboVelocity) {
		this.comboVelocity = comboVelocity;
	}

	@Override
	public double getComboHeight() {
		return this.comboHeight;
	}

	@Override
	public void setComboHeight(double comboHeight) {
		this.comboHeight = comboHeight;
	}

}
