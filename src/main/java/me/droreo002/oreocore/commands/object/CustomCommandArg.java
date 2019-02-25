package me.droreo002.oreocore.commands.object;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.commands.CommandArg;
import me.droreo002.oreocore.commands.CustomCommand;
import me.droreo002.oreocore.database.debug.SqlDebug;
import me.droreo002.oreocore.database.object.DatabaseFlatFile;
import me.droreo002.oreocore.database.object.DatabaseSQL;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CustomCommandArg extends CommandArg {

    public CustomCommandArg(CustomCommand parent) {
        super("test", parent);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        sender.sendMessage("Test!");

        // FlatFile example
//        DatabaseFlatFile data = OreoCore.getInstance().getFlatFileData();
//        /*
//         * Setting up for the first time
//         */
//        data.setup(player.getUniqueId().toString() + ".yml", true);
//        /*
//        Getting and editing
//         */
//        FileConfiguration config = data.getDataConfig(player.getUniqueId().toString());
//        if (config == null) {
//            player.sendMessage("Config is null!");
//            error(player);
//            return;
//        }
//        config.set("Data.working", true);
//        data.saveData(player.getUniqueId().toString());
    }
}
