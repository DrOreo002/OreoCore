package me.droreo002.oreocore.commands.object.base;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.commands.CustomCommand;
import me.droreo002.oreocore.commands.CustomCommandManager;
import me.droreo002.oreocore.commands.object.CustomCommandArg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExampleCommand extends CustomCommand {

    private final List<String> tabComplete = new ArrayList<>();

    public ExampleCommand() {
        super(OreoCore.getInstance(), "customcommand");
        final CustomCommandArg testArg = new CustomCommandArg(this);
        tabComplete.add(testArg.getTrigger());
        addArgument(testArg);

        CustomCommandManager.registerCommand(OreoCore.getInstance(), this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("oreocore.admin")) {
                player.sendMessage("This is  the base command!");
            } else {
                player.sendMessage("No permission!");
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return createReturnList(tabComplete, args[0]);
        }
        return null;
    }
}
