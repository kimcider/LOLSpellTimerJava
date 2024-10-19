package org.example.liner.spell.impl;


import lombok.Getter;
import lombok.Setter;
import org.example.liner.spell.Spell;

@Getter
@Setter
public class Flash extends Spell {
    public Flash() {
        super(300, "flash.jpg");
    }
}
