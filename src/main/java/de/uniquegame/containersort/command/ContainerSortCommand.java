package de.uniquegame.containersort.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import de.uniquegame.containersort.api.ContainerSortApi;
import de.uniquegame.containersort.service.LanguageService;
import de.uniquegame.containersort.util.Permissions;
import de.uniquegame.containersort.util.SignUtil;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ContainerSortCommand {

    private final ContainerSortApi containerSortApi;
    private final LanguageService languageService;
    private final String pluginPrefix;

    public ContainerSortCommand(@NotNull ContainerSortApi containerSortApi) {
        this.containerSortApi = containerSortApi;
        this.languageService = containerSortApi.getLanguageService();
        this.pluginPrefix = languageService.pluginPrefix();
    }

    @CommandMethod("containersort add language")
    @CommandPermission("containersort.command.addlanguage")
    @CommandDescription("Create a new language")
    public void addLanguageCommand(Player player) {
        if (this.languageService.createLanguage(player.locale())) {
            player.sendMessage(this.languageService.getMessage(
                    "language-successfully-created", player, this.pluginPrefix, player.locale()));
        }
    }

    @CommandMethod("containersort editsign")
    @CommandPermission("containersort.command.editsign")
    public void executeEditSignCommand(@NotNull Player player) {

        Block targetBlock = player.getTargetBlock(this.containerSortApi.getSettings().getMaxSignDistance());
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
