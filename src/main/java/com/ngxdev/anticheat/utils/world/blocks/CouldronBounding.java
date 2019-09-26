package com.ngxdev.anticheat.utils.world.blocks;

import com.ngxdev.anticheat.utils.world.types.ComplexCollisionBox;
import com.ngxdev.anticheat.utils.world.types.SimpleCollisionBox;

import static com.ngxdev.anticheat.utils.Utils.EIGHTH;

public class CouldronBounding extends ComplexCollisionBox {

    public CouldronBounding() {
        this.add(new SimpleCollisionBox(0,0,0,1, 0.3125,1));
        double thickness = EIGHTH;
        this.add(new SimpleCollisionBox(0, 0.3125, 0, thickness, 1, 1));
        this.add(new SimpleCollisionBox(1-thickness, 0.3125, 0, 1, 1, 1));
        this.add(new SimpleCollisionBox(0, 0.3125, 0, 1, 1, thickness));
        this.add(new SimpleCollisionBox(0, 0.3125, 1-thickness, 1, 1, 1));
    }
}
