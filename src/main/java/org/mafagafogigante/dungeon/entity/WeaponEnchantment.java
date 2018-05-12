package org.mafagafogigante.dungeon.entity;

import org.mafagafogigante.dungeon.io.Version;

import java.io.Serializable;

public class WeaponEnchantment implements Serializable {

  private static final long serialVersionUID = Version.MAJOR;
  private final String name;
  private final DamageAmount amount;

  WeaponEnchantment(String name, DamageAmount amount) {
    this.name = name;
    this.amount = amount;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return "+" + amount.getDescription();
  }

  public void modifyAttackDamage(Damage damage) {
    damage.getAmounts().add(amount);
  }

}
