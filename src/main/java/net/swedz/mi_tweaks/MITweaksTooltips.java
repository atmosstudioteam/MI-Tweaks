package net.swedz.mi_tweaks;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.CableTierHolder;
import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.components.CasingComponent;
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity;
import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.swedz.mi_tweaks.item.MachineBlueprintItem;
import net.swedz.tesseract.neoforge.proxy.Proxies;
import net.swedz.tesseract.neoforge.proxy.builtin.TesseractProxy;
import net.swedz.tesseract.neoforge.tooltip.TooltipAttachment;

import java.util.List;
import java.util.Optional;

public final class MITweaksTooltips
{
	/*
	 * machine_blueprints.machines is a STARTUP config value.
	 *
	 * Tesseract resolves the config value every time machines() is called.
	 * MachineList construction is expensive because it resolves regexes by
	 * scanning the Minecraft block registry.
	 *
	 * Tooltips may be requested many times per frame by Minecraft and other
	 * mods such as Item Borders, so resolving this list repeatedly causes
	 * severe GUI lag.
	 *
	 * Cache it after the first tooltip request.
	 */
	private static MITweaksConfig.MachineList cachedBlueprintMachines;

	private static MITweaksConfig.MachineList getBlueprintMachines()
	{
		if(cachedBlueprintMachines == null)
		{
			cachedBlueprintMachines =
					MITweaks.config()
							.machineBlueprints()
							.machines();
		}

		return cachedBlueprintMachines;
	}

	public static final TooltipAttachment MACHINE_HULL_AND_ENERGY_HATCH_VOLTAGE =
			TooltipAttachment.multilinesOptional(
					(flags, context, stack, item) ->
					{
						List<Component> lines = Lists.newArrayList();

						CableTier tier = CasingComponent.getCasingTier(item);

						if(tier == null &&
						   item instanceof BlockItem blockItem &&
						   blockItem.getBlock() instanceof MachineBlock machineBlock &&
						   machineBlock.getBlockEntityInstance() instanceof HatchBlockEntity &&
						   machineBlock.getBlockEntityInstance() instanceof CableTierHolder energyHatch)
						{
							tier = energyHatch.getCableTier();
						}

						if(tier != null)
						{
							if(MITweaks.config()
									.tweaks()
									.displayMachineVoltage())
							{
								lines.add(
										MITweaks.text()
												.machineVoltageRecipes(tier)
								);
							}

							if(MITweaks.config()
									.efficiency()
									.useCasingMaxOverclockOverrides())
							{
								lines.add(
										MITweaks.text()
												.machineHullAndHatchMaxOverclock(
														MITweaks.config()
																.efficiency()
																.casingMaxOverclockOverrides()
																.get(tier)
												)
								);
							}
						}

						return lines.isEmpty()
								? Optional.empty()
								: Optional.of(lines);
					}
			);

	public static final TooltipAttachment MACHINE_BLUEPRINT_MISSING =
			TooltipAttachment.singleLineOptional(
					(flags, context, stack, item) ->
					{
						/*
						 * Reject non-machine items before touching any of the
						 * blueprint configuration.
						 */
						if(!(item instanceof BlockItem blockItem) ||
						   !(blockItem.getBlock() instanceof MachineBlock machineBlock))
						{
							return Optional.empty();
						}

						MITweaksConfig.MachineBlueprintRequiredMode requiredMode =
								MITweaks.config()
										.machineBlueprints()
										.required()
										.tooltip();

						if(!requiredMode.isEnabled())
						{
							return Optional.empty();
						}

						/*
						 * Use the cached resolved machine list instead of
						 * rebuilding it every tooltip request.
						 */
						if(!getBlueprintMachines().contains(machineBlock))
						{
							return Optional.empty();
						}

						TesseractProxy proxy =
								Proxies.get(TesseractProxy.class);

						if(!proxy.isClient())
						{
							return Optional.empty();
						}

						Player player = proxy.getClientPlayer();

						if(player == null)
						{
							return Optional.empty();
						}

						return MachineBlueprintItem.hasBlueprint(
								player,
								machineBlock,
								requiredMode
						)
								? Optional.empty()
								: Optional.of(requiredMode.tooltip());
					}
			).noShiftRequired();

	public static final TooltipAttachment FLUX_TRANSFORMER =
			TooltipAttachment.singleLine(
					List.of(MITweaks.id("flux_transformer")),
					MITweaks.text().fluxTransformerHelp(
							MITweaks.config()
									.fluxTransformer()
									.conversionRate()
					)
			);

	public static final TooltipAttachment EU_TRANSFORMER =
			TooltipAttachment.singleLine(
					List.of(MITweaks.id("eu_transformer")),
					MITweaks.text().euTransformerHelp(
							MITweaks.config()
									.euTransformer()
									.conversionRate()
					)
			);

	public static void init()
	{
	}
}
