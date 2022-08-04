package de.uniquegame.containersort.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import de.uniquegame.containersort.api.ContainerSortApi;
import de.uniquegame.containersort.util.Permissions;
import de.uniquegame.containersort.util.SignUtil;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ContainerSortCommand {

    private final ContainerSortApi containerSortApi;

    public ContainerSortCommand(@NotNull ContainerSortApi containerSortApi) {
        this.containerSortApi = containerSortApi;
    }

    @CommandMethod("containersort add language")
    @CommandPermission("containersort.command.addlanguage")
    @CommandDescription("Create a new language")
    public void addLanguageCommand(Player player) {
        if (this.containerSortApi.getLanguageService().createLanguage(player.locale())) {
            player.sendMessage(this.containerSortApi.getLanguageService().getMessage(
                    "language-successfully-created",
                    player,
                    this.containerSortApi.getLanguageService().prefix(), player.locale()));
        }
    }

    @CommandMethod("containersort editsign")
    @CommandPermission("containersort.command.editsign")
    public void executeEditSignCommand(@NotNull Player player) {

        Block targetBlock = player.getTargetBlock(this.containerSortApi.getSettings().getMaxSignDistance());
        if (targetBlock == null || !(targetBlock.getState() instanceof Sign sign)) {
            player.sendMessage(this.containerSortApi.getLanguageService().getMessage("no-sign-in-line-of-sight",
                    player, this.containerSortApi.getLanguageService().prefix()));
            return;
        }

        Container container = SignUtil.findConnectedContainer(sign);
        if(container == null || !this.containerSortApi.isValidContainer(container.getBlock().getState())) {
            SignUtil.breakSign(player, this.containerSortApi.getLanguageService().getMessage(
                    "no-connected-container-found", player,
                    this.containerSortApi.getLanguageService().prefix()), sign);
            return;
        }

        if (!this.containerSortApi.isSortSign(sign)) {
            player.sendMessage(this.containerSortApi.getLanguageService().getMessage("invalid-sign", player,
                    this.containerSortApi.getLanguageService().prefix()));
            return;
        }

        if (!this.containerSortApi.isSignOwner(player.getUniqueId(), sign)) {
            if (!player.hasPermission(Permissions.PERMISSION_SORT_CREATE_OTHERS)) {
                player.sendMessage(this.containerSortApi.getLanguageService().getMessage("invalid-sign-owner", player,
                        this.containerSortApi.getLanguageService().prefix()));
                return;
            }
        }

        player.openSign(sign);
    }
}
