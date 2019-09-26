package com.ngxdev.anticheat.utils.world.types;

import com.ngxdev.anticheat.utils.world.CollisionBox;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import org.bukkit.block.Block;

public interface CollisionFactory {
    CollisionBox fetch(ProtocolVersion version, Block block);
}