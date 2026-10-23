package net.blazinblaze.blazetwitchmod.block.custom;

import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.MapCodec;
import net.blazinblaze.blazetwitchmod.block.entity.MailboxBlockEntity;
import net.blazinblaze.blazetwitchmod.data.BlazinBlazeAttachmentTypes;
import net.blazinblaze.blazetwitchmod.data.LootCrateAttachmentData;
import net.blazinblaze.blazetwitchmod.data.MailboxAttachmentData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.MenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Containers;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.stream.Stream;

public class MailboxBlock extends BaseEntityBlock {
    public static final MapCodec<MailboxBlock> CODEC = simpleCodec(MailboxBlock::new);
    public static final EnumProperty<Direction> FACING;
    public static final BooleanProperty OPEN;
    public static final BooleanProperty ACTIVE;

    public MailboxBlock(Properties settings) {
        super(settings);
        this.registerDefaultState((this.stateDefinition.any()).setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(ACTIVE, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape active = Stream.of(
                Block.box(1, 0, 1, 15, 9, 15),
                Block.box(1.5, 9, 1, 14.5, 11, 15),
                Block.box(2.5, 11, 1, 13.5, 13, 15),
                Block.box(3.5, 13, 1, 12.5, 14, 15),
                Block.box(5.5, 14, 1, 10.5, 15, 15),
                Block.box(0, 7, 4, 1, 17, 6),
                Block.box(0, 15, 6, 1, 17, 7)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        VoxelShape inactive = Stream.of(
                Block.box(1, 0, 1, 15, 9, 15),
                Block.box(1.5, 9, 1, 14.5, 11, 15),
                Block.box(2.5, 11, 1, 13.5, 13, 15),
                Block.box(3.5, 13, 1, 12.5, 14, 15),
                Block.box(5.5, 14, 1, 10.5, 15, 15),
                Block.box(0, 5, 4, 1, 7, 14),
                Block.box(0, 4, 12, 1, 5, 14)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        Direction dir = state.getValue(FACING);

        Vec3 anchor = new Vec3(0.5,0.5,0.5);

        if(state.getValue(ACTIVE)) {
            if(dir == Direction.EAST) {
                return Shapes.rotate(active, OctahedralGroup.BLOCK_ROT_Y_90, anchor);
            }else if(dir == Direction.WEST){
                return Shapes.rotate(active, OctahedralGroup.BLOCK_ROT_Y_270, anchor);
            }else if(dir == Direction.SOUTH){
                return Shapes.rotate(active, OctahedralGroup.BLOCK_ROT_Y_180, anchor);
            }
        }else {
            if (dir == Direction.EAST) {
                return Shapes.rotate(inactive, OctahedralGroup.BLOCK_ROT_Y_90, anchor);
            } else if (dir == Direction.WEST) {
                return Shapes.rotate(inactive, OctahedralGroup.BLOCK_ROT_Y_270, anchor);
            } else if (dir == Direction.SOUTH) {
                return Shapes.rotate(inactive, OctahedralGroup.BLOCK_ROT_Y_180, anchor);
            }
        }

        return inactive;
    }

    @Override
    protected BlockState rotate(final BlockState state, final Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected MapCodec<MailboxBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world instanceof ServerLevel) {
            BlockEntity entity = world.getBlockEntity(pos);
            if(entity instanceof MailboxBlockEntity mailboxBlockEntity) {
                if(player.isShiftKeyDown()) {
                    ServerPlayer serverPlayer = (ServerPlayer)player;
                    MailboxAttachmentData data = serverPlayer.getAttachedOrCreate(BlazinBlazeAttachmentTypes.MAILBOX_ATTACHMENT_TYPE, () -> MailboxAttachmentData.DEFAULT);
                    serverPlayer.setAttached(BlazinBlazeAttachmentTypes.MAILBOX_ATTACHMENT_TYPE, data.setBlockPos(pos));
                    serverPlayer.sendSystemMessage(Component.literal(String.format("§5§l[BTM]§r §oAssigned mailbox to %s!", player.getName().getString())));
                    mailboxBlockEntity.setOwnerName(player.getName().getString());
                }else {
                    player.openMenu(mailboxBlockEntity);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MailboxBlockEntity) {
            ((MailboxBlockEntity)blockEntity).tick();
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        Containers.updateNeighboursAfterDestroy(state, world, pos);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MailboxBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, ACTIVE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    static {
        FACING = BlockStateProperties.FACING;
        OPEN = BlockStateProperties.OPEN;
        ACTIVE = BooleanProperty.create("active");
    }
}
