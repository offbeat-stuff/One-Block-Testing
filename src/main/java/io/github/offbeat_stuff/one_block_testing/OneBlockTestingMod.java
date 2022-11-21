package io.github.offbeat_stuff.one_block_testing;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.lifecycle.api.event.ServerWorldTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class OneBlockTestingMod implements ModInitializer {
  // This logger is used to write text to the console and the log file.
  // It is considered best practice to use your mod name as the logger's name.
  // That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER = LoggerFactory.getLogger("Example Mod");
  int playerDeaths = 0;

  @Override
  public void onInitialize(ModContainer mod) {
    LOGGER.info("Hello Quilt world from {}!", mod.metadata().name());

    ServerWorldTickEvents.START.register((server, world) -> {
      for (var player : world.getPlayers()) {
        player.sendMessage(Text.of("Deaths: " + playerDeaths), true);
        if (player.isCreative())
          continue;
        if (player.getHealth() > 9f)
          player.setHealth(9f);
        player.getHungerManager().setFoodLevel(0);
        player.getHungerManager().setExhaustion(0);
        player.getHungerManager().setSaturationLevel(0);
      }
      if (server.getCurrentPlayerCount() == 1) {
        server.getCommandManager().executePrefixedCommand(server.getCommandSource(),
            "execute in minecraft:the_nether positioned 0 0 0 run player Steve spawn in survival");
      }
      server.getCommandManager().executePrefixedCommand(server.getCommandSource(), "kill @e[type=!player,limit=1]");
    });
    ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
      if (source.isOutOfWorld() && entity instanceof ServerPlayerEntity player) {
        playerDeaths++;
      }
    });
  }
}
