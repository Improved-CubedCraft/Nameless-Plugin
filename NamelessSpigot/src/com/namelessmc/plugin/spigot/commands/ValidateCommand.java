package com.namelessmc.plugin.spigot.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.namelessmc.NamelessAPI.ApiError;
import com.namelessmc.NamelessAPI.NamelessException;
import com.namelessmc.NamelessAPI.NamelessPlayer;
import com.namelessmc.plugin.spigot.Config;
import com.namelessmc.plugin.spigot.Message;
import com.namelessmc.plugin.spigot.NamelessPlugin;
import com.namelessmc.plugin.spigot.Permission;

/**
 * Command used to submit a code to validate a user's NamelessMC account
 */
public class ValidateCommand extends Command {

	public ValidateCommand() {
		super(Config.COMMANDS.getConfig().getString("validate"),
				Message.COMMAND_VALIDATE_DESCRIPTION.getMessage(),
				Message.COMMAND_VALIDATE_USAGE.getMessage("command", Config.COMMANDS.getConfig().getString("validate")),
				Permission.COMMAND_VALIDATE);
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) {
		if (args.length != 1) {
			return false;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage(Message.COMMAND_NOTAPLAYER.getMessage());
			return true;
		}

		final Player player = (Player) sender;

		NamelessPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(NamelessPlugin.getInstance(), () -> {
			final NamelessPlayer namelessPlayer;

			try {
				namelessPlayer = NamelessPlugin.getInstance().api.getPlayer(player.getUniqueId());
			} catch (final NamelessException e) {
				sender.sendMessage(Message.COMMAND_VALIDATE_OUTPUT_FAIL_GENERIC.getMessage());
				return;
			}

			if (!namelessPlayer.exists()) {
				sender.sendMessage(Message.PLAYER_SELF_NOTREGISTERED.getMessage());
				return;
			}

//			if (namelessPlayer.isValidated()) {
//				sender.sendMessage(Message.COMMAND_VALIDATE_OUTPUT_FAIL_ALREADYVALIDATED.getMessage());
//				return;
//			}

			final String code = args[0];

			try {
				namelessPlayer.validate(code);
				sender.sendMessage(Message.COMMAND_VALIDATE_OUTPUT_SUCCESS.getMessage());
			} catch (final ApiError e) {
				if (e.getErrorCode() == ApiError.INVALID_VALIDATE_CODE) {
					sender.sendMessage(Message.COMMAND_VALIDATE_OUTPUT_FAIL_INVALIDCODE.getMessage());
				} else {
					throw new RuntimeException(e);
				}
			} catch (final NamelessException e) {
				throw new RuntimeException(e);
			}

		});

		return true;
	}

}
