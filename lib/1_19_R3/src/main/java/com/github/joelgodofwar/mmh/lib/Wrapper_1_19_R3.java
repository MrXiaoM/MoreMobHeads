package com.github.joelgodofwar.mmh.lib;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Frog;

public final class Wrapper_1_19_R3 implements VersionWrapper {
    public String getName(Entity entity) {
        String name = entity.getName();
        switch (name) {
            case "FROG":
                Frog daFrog = (Frog) entity;
                String daFrogVariant = daFrog.getVariant().toString();

                return name + "_" + daFrogVariant;
        }
        return name;
    }
}