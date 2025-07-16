package net.blazinblaze.blazetwitchmod.item.custom;

import net.blazinblaze.blazetwitchmod.component.BlazinBlazeTwitchModComponents;
import net.blazinblaze.blazetwitchmod.networking.OverlayMessageS2CPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderEyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FakeEyeOfEnderItem extends EnderEyeItem {
    public FakeEyeOfEnderItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.NONE);
        if (blockHitResult.getType() == HitResult.Type.BLOCK && world.getBlockState(blockHitResult.getBlockPos()).isOf(Blocks.END_PORTAL_FRAME)) {
            return ActionResult.PASS;
        } else {
            if(ThreadLocalRandom.current().nextDouble() < 0.1) {
                if (user instanceof ServerPlayerEntity serverPlayer) {
                    ItemStack stack = user.getStackInHand(hand);
                    String name = stack.getOrDefault(BlazinBlazeTwitchModComponents.FAKE_EYE_USER, "test");
                    OverlayMessageS2CPayload payload = new OverlayMessageS2CPayload("§d§lFAKE EYE OF ENDER!");
                    ServerPlayNetworking.send(serverPlayer, payload);
                    serverPlayer.playSoundToPlayer(SoundEvents.ENTITY_ENDER_EYE_DEATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                    serverPlayer.sendMessage(Text.literal(String.format("§5§l[BTM]§r §d§l%s fooled you with a fake eye!", name)));
                }
            }else {
                user.setCurrentHand(hand);
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld)world;
                    Random random = new Random();
                    double randomNumberX = random.nextDouble(1000 - (-1000) + 1) + (-1000);
                    double randomNumberY = random.nextDouble(100 - (-100) + 1) + (-100);
                    double randomNumberZ = random.nextDouble(1000 - (-1000) + 1) + (-1000);
                    BlockPos blockPos = BlockPos.ofFloored(randomNumberX, randomNumberY, randomNumberZ);

                    EyeOfEnderEntity eyeOfEnderEntity = new EyeOfEnderEntity(world, user.getX(), user.getBodyY((double)0.5F), user.getZ());
                    eyeOfEnderEntity.setItem(itemStack);
                    eyeOfEnderEntity.initTargetPos(Vec3d.of(blockPos));
                    world.emitGameEvent(GameEvent.PROJECTILE_SHOOT, eyeOfEnderEntity.getPos(), GameEvent.Emitter.of(user));
                    world.spawnEntity(eyeOfEnderEntity);
                    if (user instanceof ServerPlayerEntity) {
                        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)user;
                        Criteria.USED_ENDER_EYE.trigger(serverPlayerEntity, blockPos);
                    }

                    float f = MathHelper.lerp(world.random.nextFloat(), 0.33F, 0.5F);
                    world.playSound((Entity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 1.0F, f);
                    itemStack.decrementUnlessCreative(1, user);
                    user.incrementStat(Stats.USED.getOrCreateStat(this));
                }
            }

            return ActionResult.SUCCESS_SERVER;
        }
    }
}
