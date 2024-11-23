package net.skds.physo.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.skds.physo.fluids.FluidUtils;
import net.skds.physo.world.AsyncWorldViewer;

public class DebugStickItem extends Item {

	public DebugStickItem(Properties p) {
		super(p);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!(context.getLevel() instanceof ServerLevel sl)) {
			return InteractionResult.PASS;
		}
		Player p = context.getPlayer();
		if (p != null) {
			BlockPos pos = context.getClickedPos().offset(context.getClickedFace().getNormal());
			AsyncWorldViewer viewer = new AsyncWorldViewer(sl);
			FluidState fs = viewer.getFluidState(pos);
			if (p.isCrouching()) {
				FluidUtils.setFluidState(viewer, pos, Fluids.WATER.getFlowing(4, false));
			}
			p.sendSystemMessage(Component.literal("use " + fs.getType().getClass().getNestHost().getSimpleName()
					+ " amount:" + fs.getAmount() +/* " pressure:" + viewer.getPressure(pos) + */" " + fs.isSource()));
		}
		return InteractionResult.PASS;
	}
}
