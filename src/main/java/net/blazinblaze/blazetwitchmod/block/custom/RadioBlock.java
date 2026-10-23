package net.blazinblaze.blazetwitchmod.block.custom;

import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.MapCodec;
import net.blazinblaze.blazetwitchmod.block.entity.BlazinBlazeTwitchModBlockEntities;
import net.blazinblaze.blazetwitchmod.block.entity.MailboxBlockEntity;
import net.blazinblaze.blazetwitchmod.block.entity.RadioBlockEntity;
import net.blazinblaze.blazetwitchmod.data.BlazinBlazeAttachmentTypes;
import net.blazinblaze.blazetwitchmod.data.MailboxAttachmentData;
import net.blazinblaze.blazetwitchmod.data.RadioAttachmentData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Containers;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class RadioBlock extends BaseEntityBlock {
    public static final MapCodec<RadioBlock> CODEC = simpleCodec(RadioBlock::new);
    public static final EnumProperty<Direction> FACING;

    public RadioBlock(Properties settings) {
        super(settings);
        this.registerDefaultState((this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape original = Stream.of(
                Block.box(2, 0, 6, 14, 8, 10),
                Block.box(11, 8, 7, 13, 16, 9),
                Block.box(5, 8, 7, 6, 11, 9),
                Block.box(6, 10, 7, 9, 11, 9),
                Block.box(9, 8, 7, 10, 11, 9)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        Direction dir = state.getValue(FACING);

        Vec3 anchor = new Vec3(0.5,0.5,0.5);

        if(dir == Direction.EAST) {
            return Shapes.rotate(original, OctahedralGroup.BLOCK_ROT_Y_90, anchor);
        }else if(dir == Direction.WEST){
            return Shapes.rotate(original, OctahedralGroup.BLOCK_ROT_Y_270, anchor);
        }else if(dir == Direction.SOUTH){
            return Shapes.rotate(original, OctahedralGroup.BLOCK_ROT_Y_180, anchor);
        }

        return original;
    }

    @Override
    protected MapCodec<RadioBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world instanceof ServerLevel) {
            BlockEntity entity = world.getBlockEntity(pos);
            if(entity instanceof RadioBlockEntity radioBlockEntity) {
                if(player.isShiftKeyDown()) {
                    ServerPlayer serverPlayer = (ServerPlayer) player;
                    RadioAttachmentData data = serverPlayer.getAttachedOrCreate(BlazinBlazeAttachmentTypes.RADIO_ATTACHMENT_TYPE, () -> RadioAttachmentData.DEFAULT);
                    serverPlayer.setAttached(BlazinBlazeAttachmentTypes.RADIO_ATTACHMENT_TYPE, data.setBlockPos(pos));
                    serverPlayer.sendSystemMessage(Component.literal(String.format("§5§l[BTM]§r §oAssigned radio to %s!", player.getName().getString())));
                    radioBlockEntity.setOwnerName(player.getName().getString());
                }else {
                    player.openMenu(radioBlockEntity);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        Containers.updateNeighboursAfterDestroy(state, world, pos);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RadioBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
    }
}
