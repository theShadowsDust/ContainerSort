package de.uniquegame.containersort.configuration;

import org.bukkit.World;
import org.bukkit.block.Container;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ContainerSortConfiguration {

    private final String pluginVersion;
    private final List<String> disabledWorlds;
    private final List<String> allowedContainers;
    private final List<ContainerSortProperty<?>> containerSortProperties;
    private List<String> signLayout;

    public ContainerSortConfiguration(@NotNull String pluginVersion,
                                      @NotNull List<String> disabledWorlds,
                                      @NotNull List<String> allowedContainers,
                                      @NotNull List<ContainerSortProperty<?>> containerSortProperties,
                                      @NotNull List<String> signLayout) {
        this.pluginVersion = pluginVersion;
        this.disabledWorlds = disabledWorlds;
        this.allowedContainers = allowedContainers;
        this.containerSortProperties = containerSortProperties;
        this.signLayout = signLayout;
        checkSignLayoutLength();
    }

    @NotNull
    public String getPluginVersion() {
        return pluginVersion;
    }

    @NotNull
    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    @SuppressWarnings("java:S1452")
    @NotNull
    public List<ContainerSortProperty<?>> getContainerSortProperties() {
        return containerSortProperties;
    }

    @Nullable
    public <T> ContainerSortProperty<T> getProperty(@NotNull ContainerSortProperty<T> property, @NotNull Class<T> target) {
        ContainerSortProperty<T> sortProperty = null;
        for (int i = 0; i < this.containerSortProperties.size() && sortProperty == null; i++) {
            ContainerSortProperty<?> containerSortProperty = this.containerSortProperties.get(i);
            if (containerSortProperty.getKey().equalsIgnoreCase(property.getKey())) {
                sortProperty = new ContainerSortProperty<>(property.getKey(), target.cast(containerSortProperty.getValue()));
            }
        }

        return sortProperty;
    }

    @NotNull
    public <T> T getPropertyValue(@NotNull ContainerSortProperty<T> property, @NotNull Class<T> target) {
        ContainerSortProperty<?> sortProperty = getProperty(property, target);
        if (sortProperty == null) return property.getValue();
        return target.cast(sortProperty.getValue());
    }

    public <T> void setPropertyValue(ContainerSortProperty<T> property, T value, @NotNull Class<T> target) {
        ContainerSortProperty<T> sortProperty = getProperty(property, target);
        if (sortProperty == null) return;
        sortProperty.setValue(value);
    }

    @NotNull
    public List<String> getAllowedContainers() {
        return allowedContainers;
    }

    public void addAllowedContainer(@NotNull Container container) {
        this.allowedContainers.add(container.getInventory().getType().name().toLowerCase());
    }

    public void removeAllowedContainer(@NotNull Container container) {
        this.allowedContainers.remove(container.getInventory().getType().name().toLowerCase());
    }

    @NotNull
    public List<String> getSignLayout() {
        return signLayout;
    }

    public void setSignLayout(@NotNull List<String> signLayout) {
        checkSignLayoutLength();
        this.signLayout = signLayout;
    }

    public boolean isWorldDisabled(@NotNull World world) {
        return this.disabledWorlds.contains(world.getName());
    }

    public void addDisabledWorld(@NotNull World world) {
        this.disabledWorlds.add(world.getName());
    }

    public void removeDisabledWorld(@NotNull World world) {
        this.disabledWorlds.remove(world.getName());
    }

    private void checkSignLayoutLength() {
        if (this.signLayout.size() > 4) {
            throw new IllegalStateException("The Size of the Layout is greater than 4");
        }
    }
}
