package com.mrinformatic.refinedsimd.block;

import com.mrinformatic.refinedsimd.RefinedSIMDGui;
import com.mrinformatic.refinedsimd.tile.TileCombinedExporter;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
import com.raoulvdberge.refinedstorage.render.constants.ConstantsExporter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockCombinedExporter extends BlockCableBase {
    public BlockCombinedExporter() {
        super(createBuilder("combined_exporter").tileEntity(TileCombinedExporter::new).create());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "direction=north,down=false,east=true,north=false,south=false,up=false,west=true"));

        registerCover(modelRegistration);
    }

    @Override
    @Nullable
    public BlockDirection getDirection() {
        return BlockDirection.ANY;
    }

    @Override
    public List<CollisionGroup> getCollisions(TileEntity tile, IBlockState state) {
        List<CollisionGroup> groups = super.getCollisions(tile, state);

        switch (state.getValue(getDirection().getProperty())) {
            case NORTH:
                groups.add(ConstantsExporter.LINE_NORTH);
                break;
            case EAST:
                groups.add(ConstantsExporter.LINE_EAST);
                break;
            case SOUTH:
                groups.add(ConstantsExporter.LINE_SOUTH);
                break;
            case WEST:
                groups.add(ConstantsExporter.LINE_WEST);
                break;
            case UP:
                groups.add(ConstantsExporter.LINE_UP);
                break;
            case DOWN:
                groups.add(ConstantsExporter.LINE_DOWN);
                break;
        }

        return groups;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!canAccessGui(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        return openNetworkGui(RefinedSIMDGui.COMBINED_EXPORTER, player, world, pos, side);
    }
}
