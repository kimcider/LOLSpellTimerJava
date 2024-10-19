package org.example.liner.spell.impl;

import lombok.Getter;
import lombok.Setter;
import org.example.liner.spell.Spell;

@Getter
@Setter
public class Ignite extends Spell {
    public Ignite() {
        super(180, "ignite.jpg");
    }
}
