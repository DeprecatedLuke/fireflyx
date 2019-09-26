package com.ngxdev.anticheat.checks.movement.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;

import static api.CheckType.Type.MOVEMENT;

@CheckType(id = "novelocity", name = "NoVelocity", type = MOVEMENT, state = CheckType.State.EXPERIMENTAL)
public class NoVelocity extends Check {

}
