package de.uniquegame.containersort.command;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import de.uniquegame.containersort.api.ContainerSortApi;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ContainerSortCommand {

    private final ContainerSortApi containerSortApi;

    public ContainerSortCommand(@NotNull ContainerSortApi containerSortApi) {
        this.containerSortApi = containerSortApi;
    }

    @CommandMethod("containersort language")
    @CommandPermission("containersort.command.languagecode")
    @CommandDescription("Get your client language code")
    public void executeLanguageCodeCommand(Player player) {
        player.sendMessage(this.containerSortApi.getLanguageService().getMessage("client-language-code", player,
                this.containerSortApi.getLanguageService().prefix(), player.locale()));
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

        if (!this.containerSortApi.isSortSign(sign)) {
            player.sendMessage(this.containerSortApi.getLanguageService().getMessage("invalid-sign", player,
                    this.containerSortApi.getLanguageService().prefix()));
            return;
        }

        if (!this.containerSortApi.isSignOwner(player.getUniqueId(), sign)) {
            player.sendMessage(this.containerSortApi.getLanguageService().getMessage("invalid-sign-owner", player,
                    this.containerSortApi.getLanguageService().prefix()));
            return;
        }

        player.openSign(sign);
    }
}
