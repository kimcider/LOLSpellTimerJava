package org.example.liner.spell.impl;


import lombok.Getter;
import lombok.Setter;
import org.example.liner.spell.Spell;

@Getter
@Setter
public class Teleport extends Spell {
    public Teleport() {
        super(360, "tp.jpg");
    }
}
