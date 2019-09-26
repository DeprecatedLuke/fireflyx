/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.tinyprotocol.api;

import com.ngxdev.tinyprotocol.reflection.Reflection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProtocolVersion {
    V1_7   (4,   "v1_7_R3" ),
    V1_7_10(5,   "v1_7_R4" ),
    V1_8   (-1,  "v1_8_R1" ),
    V1_8_5 (-1,  "v1_8_R2" ),
    V1_8_9 (47,  "v1_8_R3" ),
    V1_9   (107, "v1_9_R1" ),
    V1_9_1 (108, null      ),
    V1_9_2 (109, "v1_9_R2" ),
    V1_9_4 (110, "v1_9_R3" ),
    V1_10  (-1,  "v1_10_R1"),
    V1_10_2(210, "v1_10_R2"),
    V1_11  (316, "v1_11_R1"),
    V1_12  (335, "v1_12_R1"),
    V1_12_1(338, null      ),
    V1_12_2(340, "v1_12_R2"),
    UNKNOWN(-1 , "UNKNOWN" )
    ;

    @Getter
    private static ProtocolVersion gameVersion = fetchGameVersion();

    private static ProtocolVersion fetchGameVersion() {
        for (ProtocolVersion version : values()) {
            if (version.getServerVersion() != null && version.getServerVersion().equals(Reflection.VERSION)) return version;
        }
        return UNKNOWN;
    }

    public static ProtocolVersion getVersion(int versionId) {
        for (ProtocolVersion version : values()) {
            if (version.getVersion() == versionId) return version;
        }
        return UNKNOWN;
    }


    private int version;
    private String serverVersion;


    public boolean isBelow(ProtocolVersion version) {
        return this.getVersion() < version.getVersion();
    }

    public boolean isAbove(ProtocolVersion version) {
        return this.getVersion() > version.getVersion();
    }
    public boolean isEqOrAbove(ProtocolVersion version) {
        return this.getVersion() >= version.getVersion();
    }
}
