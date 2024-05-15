package dev.cobblesword.nachospigot.knockback;

public interface KnockbackProfile {

	void save();

	void save(boolean projectiles);

	String getName();

	void setName(String name);

	double getHorizontal();

	void setHorizontal(double horizontal);

	double getVertical();

	void setVertical(double vertical);

	boolean getLimitVertical();

	void setLimitVertical(boolean limitVertical);

	double getVerticalMax();

	void setVerticalMax(double verticalMax);

	boolean getInheritHorizontal();

	void setInheritHorizontal(boolean inheritHorizontal);

	boolean getInheritVertical();

	void setInheritVertical(boolean inheritVertical);

	double getInheritHorizontalStrength();

	void setInheritHorizontalStrength(double inheritHorizontalStrength);

	double getInheritVerticalStrength();

	void setInheritVerticalStrength(double inheritVerticalStrength);

	double getGroundHorizontalMultiplier();

	void setGroundHorizontalMultiplier(double groundHorizontalMultiplier);

	double getGroundVerticalMultiplier();

	void setGroundVerticalMultiplier(double groundVerticalMultiplier);

	double getSprintHorizontalMultiplier();

	void setSprintHorizontalMultiplier(double sprintHorizontalMultiplier);

	double getSprintVerticalMultiplier();

	void setSprintVerticalMultiplier(double sprintVerticalMultiplier);

	int getHitDelay();

	void setHitDelay(int hitDelay);

	boolean getComboMode();
	
	void setComboMode(boolean comboMode);

	int getComboTicks();
	
	void setComboTicks(int comboTicks);

	double getComboVelocity();
	
	void setComboVelocity(double comboVelocity);

	double getComboHeight();

	void setComboHeight(double comboHeight);

	boolean isStopSprint();

	void setStopSprint(boolean stopSprint);

	double getRodHorizontal();

	void setRodHorizontal(double rodHorizontal);

	double getRodVertical();

	void setRodVertical(double rodVertical);

	double getArrowHorizontal();

	void setArrowHorizontal(double arrowHorizontal);

	double getArrowVertical();

	void setArrowVertical(double arrowVertical);

	double getPearlHorizontal();

	void setPearlHorizontal(double pearlHorizontal);

	double getPearlVertical();

	void setPearlVertical(double pearlVertical);

	double getSnowballHorizontal();

	void setSnowballHorizontal(double snowballHorizontal);

	double getSnowballVertical();

	void setSnowballVertical(double snowballVertical);

	double getEggHorizontal();

	void setEggHorizontal(double eggHorizontal);

	double getEggVertical();

	void setEggVertical(double eggVertical);

	String[] getKnockbackValues();

	String[] getProjectilesValues();
}
