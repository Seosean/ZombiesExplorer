package com.seosean.zombiesexplorer.commands;

import com.seosean.zombiesexplorer.PowerUpDetect;
import com.seosean.zombiesexplorer.ZombiesExplorer;
import com.seosean.zombiesexplorer.utils.DebugUtils;
import com.seosean.zombiesexplorer.utils.GameUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Commands implements ICommand {
    private final List<String> aliases = new ArrayList<>();

    public Commands() {
        this.aliases.add("zombiesexplorer");
        this.aliases.add("ze");
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            DebugUtils.sendMessage(EnumChatFormatting.RED + "This command is used to select powerup pattern yourself! Mod will always detect correct powerup pattern, you might use this command when some incorrect situation happens!");
            DebugUtils.sendMessage(EnumChatFormatting.RED + "Usage: /ze <ins\\max\\ss> <Pattern>");
            return;
        }
        PowerUpDetect powerUpDetect = ZombiesExplorer.getInstance().getPowerUpDetect();
        GameUtils.ZombiesMap map = GameUtils.getMap();
        if (map == GameUtils.ZombiesMap.NULL) {
            DebugUtils.sendMessage(EnumChatFormatting.RED + "You are not in any Zombies game!");
            return;
        }

        if (args.length <= 2) {
            int round = 0;
            if (args.length == 2) {
                round = Integer.parseInt(args[1]);
            }
            String powerup = args[0];
            if (powerup.equalsIgnoreCase("ins")) {
                if (round == 0) {
                    DebugUtils.sendMessage(EnumChatFormatting.YELLOW + "The " + EnumChatFormatting.RED + "Insta Kill" + EnumChatFormatting.YELLOW + " pattern is " + EnumChatFormatting.RED + (powerUpDetect.insRounds.isEmpty() ? "Unknown" : powerUpDetect.insRounds.get(0)) + EnumChatFormatting.YELLOW + "!");
                } else if (round == 2) {
                    switch (map) {
                        case DEAD_END: powerUpDetect.insRounds = Arrays.asList(PowerUpDetect.r2InsRoundsDE); break;
                        case BAD_BLOOD: powerUpDetect.insRounds = Arrays.asList(PowerUpDetect.r2InsRoundsBB); break;
                        case ALIEN_ARCADIUM: powerUpDetect.insRounds = Arrays.asList(PowerUpDetect.r2InsRoundsAA); break;
                    }
                    DebugUtils.sendMessage(EnumChatFormatting.YELLOW + "The " + EnumChatFormatting.RED + "Insta Kill" + EnumChatFormatting.YELLOW + " pattern has been changed to " + EnumChatFormatting.RED + round + EnumChatFormatting.YELLOW + "!");
                } else if (round == 3) {
                    switch (map) {
                        case DEAD_END:
                        case BAD_BLOOD: powerUpDetect.insRounds = Arrays.asList(PowerUpDetect.r3InsRoundsDEBB); break;
                        case ALIEN_ARCADIUM: powerUpDetect.insRounds = Arrays.asList(PowerUpDetect.r3InsRoundsAA); break;
                    }
                    DebugUtils.sendMessage(EnumChatFormatting.YELLOW + "The " + EnumChatFormatting.RED + "Insta Kill" + EnumChatFormatting.YELLOW + " pattern has been changed to " + EnumChatFormatting.RED + round + EnumChatFormatting.YELLOW + "!");
                }
            } else if (powerup.equalsIgnoreCase("max")) {
                if (round == 0) {
                    DebugUtils.sendMessage(EnumChatFormatting.YELLOW + "The " + EnumChatFormatting.BLUE + "Max Ammo" + EnumChatFormatting.YELLOW + " pattern is " + EnumChatFormatting.RED + (powerUpDetect.maxRounds.isEmpty() ? "Unknown" : powerUpDetect.maxRounds.get(0)) + EnumChatFormatting.YELLOW + "!");
                } else if (round == 2) {
                    switch (map) {
                        case DEAD_END: powerUpDetect.insRounds = Arrays.asList(PowerUpDetect.r2MaxRoundsDE); break;
                        case BAD_BLOOD: powerUpDetect.insRounds = Arrays.asList(PowerUpDetect.r2MaxRoundsBB); break;
                        case ALIEN_ARCADIUM: powerUpDetect.insRounds = Arrays.asList(PowerUpDetect.r2MaxRoundsAA); break;
                    }
                    DebugUtils.sendMessage(EnumChatFormatting.YELLOW + "The " + EnumChatFormatting.BLUE + "Max Ammo" + EnumChatFormatting.YELLOW + " pattern has been changed to " + EnumChatFormatting.RED + round + EnumChatFormatting.YELLOW + "!");

                } else if (round == 3) {
                    switch (map) {
                        case DEAD_END:
                        case BAD_BLOOD: powerUpDetect.maxRounds = Arrays.asList(PowerUpDetect.r3MaxRoundsDEBB); break;
                        case ALIEN_ARCADIUM: powerUpDetect.maxRounds = Arrays.asList(PowerUpDetect.r3MaxRoundsAA); break;
                    }
                    DebugUtils.sendMessage(EnumChatFormatting.YELLOW + "The " + EnumChatFormatting.BLUE + "Max Ammo" + EnumChatFormatting.YELLOW + " pattern has been changed to " + EnumChatFormatting.RED + round + EnumChatFormatting.YELLOW + "!");
                }
            } else if (powerup.equalsIgnoreCase("ss")) {
                if (round == 0) {
                    DebugUtils.sendMessage(EnumChatFormatting.YELLOW + "The " + EnumChatFormatting.DARK_PURPLE + "Shopping Spree" + EnumChatFormatting.YELLOW + " pattern is " + EnumChatFormatting.RED + (powerUpDetect.ssRounds.isEmpty() ? "Unknown" : powerUpDetect.ssRounds.get(0)) + EnumChatFormatting.YELLOW + "!");
                } else if (round == 5) {
                    powerUpDetect.ssRounds = Arrays.asList(PowerUpDetect.r5SSRoundsAA);
                    DebugUtils.sendMessage(EnumChatFormatting.YELLOW + "The " + EnumChatFormatting.DARK_PURPLE + "Shopping Spree" + EnumChatFormatting.YELLOW + " pattern has been changed to " + EnumChatFormatting.RED + round + EnumChatFormatting.YELLOW + "!");
                } else if (round == 6) {
                    powerUpDetect.ssRounds = Arrays.asList(PowerUpDetect.r6SSRoundsAA);
                    DebugUtils.sendMessage(EnumChatFormatting.YELLOW + "The " + EnumChatFormatting.DARK_PURPLE + "Shopping Spree" + EnumChatFormatting.YELLOW + " pattern has been changed to " + EnumChatFormatting.RED + round + EnumChatFormatting.YELLOW + "!");
                } else if (round == 7) {
                    powerUpDetect.ssRounds = Arrays.asList(PowerUpDetect.r7SSRoundsAA);
                    DebugUtils.sendMessage(EnumChatFormatting.YELLOW + "The " + EnumChatFormatting.DARK_PURPLE + "Shopping Spree" + EnumChatFormatting.YELLOW + " pattern has been changed to " + EnumChatFormatting.RED + round + EnumChatFormatting.YELLOW + "!");
                }
            }
        }
    }

    public List<String> getCommandAliases() {
        return this.aliases;
    }

    public int compareTo(ICommand o) {
        return 0;
    }

    public String getCommandName() {
        return "zombiesexplorer";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "zombiesexplorer";
    }

    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return Arrays.asList("ins", "max", "ss");
        } else if (args.length == 2) {
            switch (args[0]) {
                case "ins":
                case "max": return Arrays.asList("2", "3");
                case "ss": return Arrays.asList("5", "6", "7");
            }
        }
        return null;
    }

    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
}