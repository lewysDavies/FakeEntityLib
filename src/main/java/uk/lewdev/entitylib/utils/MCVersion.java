package uk.lewdev.entitylib.utils;

import org.bukkit.Bukkit;
import uk.lewdev.entitylib.utils.nms.EntityIdProvider1_14;
import uk.lewdev.entitylib.utils.nms.EntityIdProvider1_17;
import uk.lewdev.entitylib.utils.nms.EntityIdProvider1_9;

public enum MCVersion {

    V1_10("1.10", new EntityIdProvider1_9()),
    V1_11("1.11", new EntityIdProvider1_9()),
    V1_12("1.12", new EntityIdProvider1_9()),
    V1_13("1.13", new EntityIdProvider1_9()),
    V1_14("1.14", new EntityIdProvider1_14()),
    V1_15("1.15", new EntityIdProvider1_14()),
    V1_16("1.16", new EntityIdProvider1_14()),
    V1_17("1.17", new EntityIdProvider1_17());

    private static MCVersion CUR_VERSION = V1_10;

    static {
        String curVerStr = Bukkit.getBukkitVersion();

        for (MCVersion version : MCVersion.values()) {
            if (curVerStr.toLowerCase().contains(version.versionString)) {
                CUR_VERSION = version;
                break;
            }
        }
    }

    private final String versionString;
    private final EntityIdProvider provider;

    MCVersion(String verStr, EntityIdProvider provider) {
        this.versionString = verStr;
        this.provider = provider;
    }

    public static MCVersion getCurrentVersion() {
        return CUR_VERSION;
    }

    public String toMinecraftVersionStr() {
        return this.versionString;
    }

    public EntityIdProvider getProvider() {
        return this.provider;
    }

    public boolean isAfter1_14() {
        return this.ordinal() > MCVersion.V1_14.ordinal();
    }

    public boolean isAfterOr1_16() {
        return this.ordinal() >= MCVersion.V1_16.ordinal();
    }

    public boolean isAfterOr1_17() {
        return this.ordinal() >= MCVersion.V1_17.ordinal();
    }
}