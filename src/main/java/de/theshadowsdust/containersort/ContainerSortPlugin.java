package de.theshadowsdust.containersort;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import de.theshadowsdust.containersort.api.ContainerSortApi;
import de.theshadowsdust.containersort.api.ContainerSortApiImpl;
import de.theshadowsdust.containersort.command.ContainerSortCommand;
import de.theshadowsdust.containersort.listener.ContainerProtectionListener;
import de.theshadowsdust.containersort.listener.SignListener;
import de.theshadowsdust.containersort.listener.PlayerInteractListener;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.function.Function;
import java.util.logging.Level;

public final class ContainerSortPlugin extends JavaPlugin {

    private ContainerSortApiImpl containerSortApi;

    @Override
    public void onEnable() {

        int javaVersion = Runtime.version().feature();
        if (javaVersion < 17) {
            getLogger().log(Level.WARNING, "Your Java Version is outdated! Please use 17 or newer");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        PluginManager pluginManager = getServer().getPluginManager();
        try {

            this.containerSortApi = new ContainerSortApiImpl(this);
            this.getServer().getServicesManager().register(
                    ContainerSortApi.class,
                    this.containerSortApi,
                    this,
                    ServicePriority.Highest);

            pluginManager.registerEvents(new PlayerInteractListener(this.containerSortApi), this);
            pluginManager.registerEvents(new ContainerProtectionListener(this.containerSortApi), this);
            pluginManager.registerEvents(new SignListener(this.containerSortApi), this);
            buildCommandSystem();

        } catch (IOException e) {
            pluginManager.disablePlugin(this);
            getLogger().log(Level.SEVERE, "Something went wrong on load configuartion", e);
        }
    }

    @Override
    public void onDisable() {
        if (this.containerSortApi != null) {
            this.getServer().getServicesManager().unregister(this.containerSortApi);
        }
    }

    private void buildCommandSystem() {

        PaperCommandManager<CommandSender> paperCommandManager;

        try {

            paperCommandManager = new PaperCommandManager<>(this,
                    CommandExecutionCoordinator.simpleCoordinator(), Function.identity(), Function.identity());
        } catch (Exception e) {
            this.getLogger().log(Level.WARNING, "Failed to build command system", e);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (paperCommandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            paperCommandManager.registerBrigadier();
            this.getLogger().info("Brigadier support enabled");
        }

        if (paperCommandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            paperCommandManager.registerAsynchronousCompletions();
            this.getLogger().info("Asynchronous completions enabled");
        }

        Function<ParserParameters, CommandMeta> commandMetaFunction = p -> CommandMeta.
                simple().with(
                        CommandMeta.DESCRIPTION,
                        p.get(StandardParameters.DESCRIPTION, "No description")).build();

        AnnotationParser<CommandSender> annotationParser =
                new AnnotationParser<>(
                        paperCommandManager,
                        CommandSender.class,
                        commandMetaFunction);

        annotationParser.parse(new ContainerSortCommand(this.containerSortApi));
    }
}
