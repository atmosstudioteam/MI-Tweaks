package net.swedz.mi_tweaks.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.swedz.mi_tweaks.MITweaks;
import net.swedz.mi_tweaks.MITweaksComponents;
import net.swedz.mi_tweaks.MITweaksItems;

import java.util.List;
import java.util.Random;

public final class EmiCopyBlueprintRecipe extends EmiPatternCraftingRecipe
{
	private final List<Block> blueprintMachines;

	public EmiCopyBlueprintRecipe(ResourceLocation id)
	{
		super(
				List.of(
						EmiStack.of(MITweaksItems.MACHINE_BLUEPRINT),
						EmiStack.of(Items.PAPER)
				),
				EmiStack.of(MITweaksItems.MACHINE_BLUEPRINT),
				id
		);

		/*
		 * Resolve the configured machine list ONCE when the EMI recipe
		 * is created.
		 *
		 * Calling machineBlueprints().machines() repeatedly is very
		 * expensive because Tesseract reconstructs MachineList and
		 * scans the block registry.
		 */
		this.blueprintMachines = MITweaks.config()
				.machineBlueprints()
				.machines()
				.stream()
				.toList();
	}

	@Override
	public SlotWidget getInputWidget(int slot, int x, int y)
	{
		if(slot == 0)
		{
			return new GeneratedSlotWidget(
					this::generateBlueprintItem,
					unique,
					x,
					y
			).catalyst(true);
		}
		else if(slot == 1)
		{
			return new SlotWidget(
					EmiStack.of(Items.PAPER),
					x,
					y
			);
		}
		else
		{
			return new SlotWidget(
					EmiStack.EMPTY,
					x,
					y
			);
		}
	}

	@Override
	public SlotWidget getOutputWidget(int x, int y)
	{
		return new GeneratedSlotWidget(
				this::generateBlueprintItem,
				unique,
				x,
				y
		);
	}

	private EmiStack generateBlueprintItem(Random random)
	{
		ItemStack blueprintItem =
				MITweaksItems.MACHINE_BLUEPRINT
						.asItem()
						.getDefaultInstance();

		if(!blueprintMachines.isEmpty())
		{
			Block machineBlock = blueprintMachines.get(
					random.nextInt(blueprintMachines.size())
			);

			/*
			 * The machine came from the already validated configured
			 * machine list, so do not call MachineBlueprintItem.setMachineBlock().
			 *
			 * That method checks machineBlueprints().machines() again,
			 * which would rebuild the expensive MachineList every frame.
			 */
			blueprintItem.set(
					MITweaksComponents.MACHINE_BLOCK,
					machineBlock
			);
		}

		return EmiStack.of(blueprintItem);
	}
}
