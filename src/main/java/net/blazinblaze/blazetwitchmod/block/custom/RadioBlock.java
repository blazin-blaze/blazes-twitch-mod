package net.blazinblaze.blazetwitchmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.blazinblaze.blazetwitchmod.block.entity.BlazinBlazeTwitchModBlockEntities;
import net.blazinblaze.blazetwitchmod.block.entity.MailboxBlockEntity;
import net.blazinblaze.blazetwitchmod.block.entity.RadioBlockEntity;
import net.blazinblaze.blazetwitchmod.data.BlazinBlazeAttachmentTypes;
import net.blazinblaze.blazetwitchmod.data.MailboxAttachmentData;
import net.blazinblaze.blazetwitchmod.data.RadioAttachmentData;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class RadioBlock extends BlockWithEntity {
    public static final MapCodec<RadioBlock> CODEC = createCodec(RadioBlock::new);
    public static final EnumProperty<Direction> FACING;

    public RadioBlock(Settings settings) {
        super(settings);
        this.setDefaultState((this.stateManager.getDefaultState()).with(FACING, Direction.NORTH));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape original = Stream.of(
                Block.createCuboidShape(2, 0, 6, 14, 8, 10),
                Block.createCuboidShape(11, 8, 7, 13, 16, 9),
                Block.createCuboidShape(5, 8, 7, 6, 11, 9),
                Block.createCuboidShape(6, 10, 7, 9, 11, 9),
                Block.createCuboidShape(9, 8, 7, 10, 11, 9)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

        Direction dir = state.get(FACING);

        Vec3d anchor = new Vec3d(0.5,0.5,0.5);

        if(dir == Direction.EAST) {
            return VoxelShapes.transform(original, DirectionTransformation.fromRotations(AxisRotation.R0, AxisRotation.R90), anchor);
        }else if(dir == Direction.WEST){
            return VoxelShapes.transform(original, DirectionTransformation.fromRotations(AxisRotation.R0, AxisRotation.R270), anchor);
        }else if(dir == Direction.SOUTH){
            return VoxelShapes.transform(original, DirectionTransformation.fromRotations(AxisRotation.R0, AxisRotation.R180), anchor);
        }

        return original;
    }

    @Override
    protected MapCodec<RadioBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world instanceof ServerWorld) {
            BlockEntity entity = world.getBlockEntity(pos);
            if(entity instanceof RadioBlockEntity radioBlockEntity) {
                if(player.isSneaking()) {
                    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                    RadioAttachmentData data = serverPlayer.getAttachedOrCreate(BlazinBlazeAttachmentTypes.RADIO_ATTACHMENT_TYPE, () -> RadioAttachmentData.DEFAULT);
                    serverPlayer.setAttached(BlazinBlazeAttachmentTypes.RADIO_ATTACHMENT_TYPE, data.setBlockPos(pos));
                    serverPlayer.sendMessage(Text.literal(String.format("§5§l[BTM]§r §oAssigned radio to %s!", player.getName().getString())));
                    radioBlockEntity.setOwnerName(player.getName().getString());
                }else {
                    player.openHandledScreen(radioBlockEntity);
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        ItemScatterer.onStateReplaced(state, world, pos);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RadioBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    static {
        FACING = HorizontalFacingBlock.FACING;
    }
}
