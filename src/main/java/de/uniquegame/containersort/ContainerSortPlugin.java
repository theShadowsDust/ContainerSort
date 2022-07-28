package de.uniquegame.containersort;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import de.uniquegame.containersort.api.ContainerSortApi;
import de.uniquegame.containersort.api.ContainerSortApiImpl;
import de.uniquegame.containersort.command.ContainerSortCommand;
import de.uniquegame.containersort.configuration.ContainerSortSettings;
import de.uniquegame.containersort.listener.PlayerInteractListener;
import de.uniquegame.containersort.listener.SignListener;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;

public class ContainerSortPlugin extends JavaPlugin {

    private ContainerSortApiImpl containerSortApi;

    @Override
    public void onLoad() {

        Optional<Integer> javaVersionOptional = Runtime.version().build();
        if (javaVersionOptional.orElse(8) < 17) {
            this.getServer().getPluginManager().disablePlugin(this);
        }

        ConfigurationSerialization.registerClass(ContainerSortSettings.class);
    }

    @Override
    public void onEnable() {

        PluginManager pluginManager = getServer().getPluginManager();

        this.containerSortApi = new ContainerSortApiImpl(this);
        this.getServer().getServicesManager().register(
                ContainerSortApi.class,
                this.containerSortApi,
                this,
                ServicePriority.Highest);


        pluginManager.registerEvents(new PlayerInteractListener(this.containerSortApi), this);
        pluginManager.registerEvents(new SignListener(this.containerSortApi), this);
        buildCommandSystem();
    }

    @Override
    public void onDisable() {
        this.getServer().getServicesManager().unregisterAll(this);
        ConfigurationSerialization.unregisterClass(ContainerSortSettings.class);
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
