package net.blazinblaze.blazetwitchmod.item.custom;

import net.blazinblaze.blazetwitchmod.component.BlazinBlazeTwitchModComponents;
import net.blazinblaze.blazetwitchmod.networking.OverlayMessageS2CPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.StructureTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FakeEyeOfEnderItem extends EnderEyeItem {
    public FakeEyeOfEnderItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        BlockHitResult blockHitResult = getPlayerPOVHitResult(world, user, ClipContext.Fluid.NONE);
        if (blockHitResult.getType() == HitResult.Type.BLOCK && world.getBlockState(blockHitResult.getBlockPos()).is(Blocks.END_PORTAL_FRAME)) {
            return InteractionResult.PASS;
        } else {
            if(ThreadLocalRandom.current().nextDouble() < 0.1) {
                if (user instanceof ServerPlayer serverPlayer) {
                    ItemStack stack = user.getItemInHand(hand);
                    String name = stack.getOrDefault(BlazinBlazeTwitchModComponents.FAKE_EYE_USER, "test");
                    OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§d§lFAKE EYE OF ENDER!");
                    ServerPlayNetworking.send(serverPlayer, payload);
                    serverPlayer.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0F, 1.0F);

                    serverPlayer.sendSystemMessage(Component.literal(String.format("§5§l[BTM]§r §d§l%s fooled you with a fake eye!", name)));
                }
            }else {
                user.startUsingItem(hand);
                if (world instanceof ServerLevel) {
                    ServerLevel serverWorld = (ServerLevel)world;
                    Random random = new Random();
                    double randomNumberX = random.nextDouble(1000 - (-1000) + 1) + (-1000);
                    double randomNumberY = random.nextDouble(100 - (-100) + 1) + (-100);
                    double randomNumberZ = random.nextDouble(1000 - (-1000) + 1) + (-1000);
                    BlockPos blockPos = BlockPos.containing(randomNumberX, randomNumberY, randomNumberZ);

                    EyeOfEnder eyeOfEnderEntity = new EyeOfEnder(world, user.getX(), user.getY((double)0.5F), user.getZ());
                    eyeOfEnderEntity.setItem(itemStack);
                    eyeOfEnderEntity.signalTo(Vec3.atLowerCornerOf(blockPos));
                    world.gameEvent(GameEvent.PROJECTILE_SHOOT, eyeOfEnderEntity.blockPosition(), GameEvent.Context.of(user)); //eyeOfEnderEntity.getPos()
                    world.addFreshEntity(eyeOfEnderEntity);
                    if (user instanceof ServerPlayer) {
                        ServerPlayer serverPlayerEntity = (ServerPlayer)user;
                        CriteriaTriggers.USED_ENDER_EYE.trigger(serverPlayerEntity, blockPos);
                    }

                    float f = Mth.lerp(world.getRandom().nextFloat(), 0.33F, 0.5F);
                    world.playSound((Entity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 1.0F, f);
                    itemStack.consume(1, user);
                    user.awardStat(Stats.ITEM_USED.get(this));
                }
            }

            return InteractionResult.SUCCESS_SERVER;
        }
    }
}
