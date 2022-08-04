package de.uniquegame.containersort.configuration;

import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SerializableAs("settings")
public class ContainerSortSettings implements ConfigurationSerializable {

    private final Sound sortSuccessSound;
    private final List<String> disabledWorlds;

    private List<String> allowedContainers;

    private final boolean protectSigns;
    private final List<String> signLayout;
    private final int maxSignDistance;

    public ContainerSortSettings(@NotNull Sound sortSuccessSound,
                                 @NotNull List<String> disabledWorlds,
                                 @NotNull List<String> allowedContainers,
                                 boolean protectSigns,
                                 @NotNull List<String> signLayout,
                                 int maxSignDistance) {

        this.sortSuccessSound = sortSuccessSound;
        this.disabledWorlds = disabledWorlds;
        this.allowedContainers = allowedContainers;
        this.protectSigns = protectSigns;
        this.signLayout = signLayout;
        this.maxSignDistance = maxSignDistance;

        if (this.signLayout.size() > 4) {
            throw new IllegalStateException("The Size of the Layout is greater than 4");
        }
    }

    @NotNull
    public List<String> getSignLayout() {
        return signLayout;
    }

    @NotNull
    public Sound getSortSuccessSound() {
        return sortSuccessSound;
    }

    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    public boolean signsProtected() {
        return protectSigns;
    }

    @NotNull
    public List<String> getAllowedContainers() {
        return allowedContainers;
    }


    public void setAllowedContainers(@NotNull List<String> allowedContainers) {
        this.allowedContainers = allowedContainers;
    }

    public boolean isWorldDisabled(@NotNull World world) {
        return this.disabledWorlds.contains(world.getName());
    }

    public int getMaxSignDistance() {
        return maxSignDistance;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("protectSigns", this.protectSigns);
        map.put("disabledWorlds", this.disabledWorlds);
        map.put("allowedContainers", this.allowedContainers);
        map.put("signLayout", this.signLayout);
        map.put("sortSuccessSound", this.sortSuccessSound.name());
        map.put("maxSignDistance", this.maxSignDistance);
        return map;
    }

    public static ContainerSortSettings deserialize(@NotNull Map<String, Object> map) {

        boolean protectSigns = (boolean) map.get("protectSigns");

        List<String> disabledWorlds = (List<String>) map.getOrDefault("disabledWorlds", List.of());
        List<String> signLayout = (List<String>) map.getOrDefault("signLayout", List.of());

        Sound sortSuccessSound = Sound.valueOf(
                ((String) map.getOrDefault("sortSuccessSound", "BLOCK_NOTE_BLOCK_PLING")).toUpperCase());

        List<String> allowedContainerNames = (List<String>) map.getOrDefault("allowedContainers", List.of());
        List<String> allowedContainers = allowedContainerNames.stream().map(String::toLowerCase).collect(Collectors.toList());
        int maxSignDistance = (int) map.getOrDefault("maxSignDistance", -1);

        return new ContainerSortSettings(sortSuccessSound, disabledWorlds, allowedContainers, protectSigns, signLayout, maxSignDistance);
    }
}
