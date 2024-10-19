package org.example.liner.spell.impl;

import lombok.Getter;
import lombok.Setter;
import org.example.liner.spell.Spell;

@Getter
@Setter
public class Exhaustion extends Spell {
    public Exhaustion() {
        super(240, "exhaustion.jpg");
    }
}
