package drones.util;

import net.minecraft.nbt.CompoundTag;

import java.util.Objects;
import java.util.UUID;

public class DroneSettings {

    private static final DroneSettings DEFAULT = DroneSettings.of(null);

    public final UUID linkId;

    private DroneSettings(UUID linkId) {
        this.linkId = linkId;
    }

    public DroneSettings withLinkId(UUID linkId) {
        return DroneSettings.of(linkId);
    }

    public CompoundTag toTag() {
        return this.toTag(new CompoundTag());
    }

    public CompoundTag toTag(CompoundTag tag) {
        if (linkId != null) {
            tag.putUuid("LinkId", linkId);
        } else {
            tag.removeUuid("LinkId");
        }
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DroneSettings that = (DroneSettings) o;
        return Objects.equals(linkId, that.linkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkId);
    }

    public static DroneSettings ofDefault() {
        return DroneSettings.DEFAULT;
    }

    public static DroneSettings of(UUID linkId) {
        return new DroneSettings(linkId);
    }

    public static DroneSettings fromTag(CompoundTag tag) {
        UUID linkId = null;
        if (tag.containsUuid("LinkId")) {
            linkId = tag.getUuid("LinkId");
        }
        return DroneSettings.of(linkId);
    }

}
