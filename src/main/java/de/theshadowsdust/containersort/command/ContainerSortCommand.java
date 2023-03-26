package de.theshadowsdust.containersort.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import de.theshadowsdust.containersort.api.ContainerSortApi;
import de.theshadowsdust.containersort.configuration.ContainerSortProperty;
import de.theshadowsdust.containersort.service.LanguageService;
import de.theshadowsdust.containersort.util.Permissions;
import de.theshadowsdust.containersort.util.SignUtil;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;

@SuppressWarnings("unused")
public final class ContainerSortCommand {

    private final ContainerSortApi containerSortApi;
    private final LanguageService languageService;
    private final String pluginPrefix;

    public ContainerSortCommand(@NotNull ContainerSortApi containerSortApi) {
        this.containerSortApi = containerSortApi;
        this.languageService = containerSortApi.getLanguageService();
        this.pluginPrefix = languageService.pluginPrefix();
    }


    @CommandMethod("containersort|cs reload config")
    @CommandPermission("containersort.command.reload.config")
    @CommandDescription("Reload the configuration file")
    public void reloadConfigCommand(CommandSender commandSender) {
        try {
            this.containerSortApi.reloadConfig();
            commandSender.sendMessage(this.languageService.getMessage(
                    "config-reload-successfully", commandSender, this.pluginPrefix));
        } catch (IOException e) {
            commandSender.sendMessage(this.languageService.getMessage(
                    "cannot-reload-config", commandSender, this.pluginPrefix));
            this.containerSortApi.getPlugin().getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @CommandMethod("containersort|cs add language")
    @CommandPermission("containersort.command.addlanguage")
    @CommandDescription("Create a new language")
    public void addLanguageCommand(Player player) {
        var locale = player.locale();
        this.languageService.updateLocale(locale).thenAcceptAsync(localeUpdateResult -> {
            switch (localeUpdateResult) {
                case CANNOT_SAVE -> player.sendMessage(this.languageService.getMessage(
                        "language-cannot-save", player, this.pluginPrefix, locale));
                case ALREADY_EXISTS -> player.sendMessage(this.languageService.getMessage(
                        "language-already-exists", player, this.pluginPrefix, locale));
                default -> player.sendMessage(this.languageService.getMessage(
                        "language-successfully-created", player, this.pluginPrefix, locale));
            }
        });
    }

    @SuppressWarnings("java:S1874")
    @CommandMethod("containersort|cs editsign")
    @CommandPermission("containersort.command.editsign")
    public void executeEditSignCommand(@NotNull Player player) {

        Block targetBlock = player.getTargetBlockExact(this.containerSortApi.
                getPropertyValue(ContainerSortProperty.MAX_SIGN_DISTANCE, Double.class).intValue());
        if (targetBlock == null || !(targetBlock.getState() instanceof Sign sign)) {
            player.sendMessage(this.languageService.getMessage("no-sign-in-line-of-sight", player, this.pluginPrefix));
            return;
        }

        Container container = SignUtil.findConnectedContainer(sign);
        if(container == null || !this.containerSortApi.isValidContainer(container.getBlock().getState())) {
            player.sendMessage(this.languageService.getMessage("no-connected-container-found", player, this.pluginPrefix));
            return;
        }

        if (!this.containerSortApi.isSortSign(sign)) {
            player.sendMessage(this.languageService.getMessage("invalid-sign", player, this.pluginPrefix));
            return;
        }

        if (!this.containerSortApi.isSignOwner(player.getUniqueId(), sign) &&
                !player.hasPermission(Permissions.PERMISSION_SORT_CREATE_OTHERS)) {
            player.sendMessage(this.languageService.getMessage("invalid-sign-owner", player, this.pluginPrefix));
            return;
        }

        player.openSign(sign);
    }
}
