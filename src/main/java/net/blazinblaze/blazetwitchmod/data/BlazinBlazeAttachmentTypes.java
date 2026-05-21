package net.blazinblaze.blazetwitchmod.data;

import net.blazinblaze.blazetwitchmod.BlazinBlazeTwitchMod;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

public class BlazinBlazeAttachmentTypes {
    public static final AttachmentType<LootCrateAttachmentData> LOOT_CRATE_ATTACHMENT_TYPE = AttachmentRegistry.create(
                    Identifier.of(BlazinBlazeTwitchMod.MOD_ID,"loot_crate_data"),
                    builder->builder // we are using a builder chain here to configure the attachment data type
                            .initializer(()->LootCrateAttachmentData.DEFAULT) // a default value to provide if you dont supply one
                            .persistent(LootCrateAttachmentData.CODEC) // how to save and load the data when the object it is attached to is saved or loaded
            .syncWith(
                    LootCrateAttachmentData.PACKET_CODEC,  // how to turn the data into a packet to send to players
                    AttachmentSyncPredicate.all() // who to send the data to
            ));

    public static final AttachmentType<MultiplierAttachmentData> MULTIPLIER_ATTACHMENT_TYPE = AttachmentRegistry.create(
            Identifier.of(BlazinBlazeTwitchMod.MOD_ID,"mutiliplier_data"),
            builder->builder // we are using a builder chain here to configure the attachment data type
                    .initializer(()->MultiplierAttachmentData.DEFAULT) // a default value to provide if you dont supply one
                    .persistent(MultiplierAttachmentData.CODEC) // how to save and load the data when the object it is attached to is saved or loaded
                    .syncWith(
                            MultiplierAttachmentData.PACKET_CODEC,  // how to turn the data into a packet to send to players
                            AttachmentSyncPredicate.all() // who to send the data to
                    ));

    public static final AttachmentType<TimerAttachmentData> TIMER_ATTACHMENT_TYPE = AttachmentRegistry.create(
            Identifier.of(BlazinBlazeTwitchMod.MOD_ID,"timer_data"),
            builder->builder // we are using a builder chain here to configure the attachment data type
                    .initializer(()->TimerAttachmentData.DEFAULT) // a default value to provide if you dont supply one
                    .persistent(TimerAttachmentData.CODEC) // how to save and load the data when the object it is attached to is saved or loaded
                    .syncWith(
                            TimerAttachmentData.PACKET_CODEC,  // how to turn the data into a packet to send to players
                            AttachmentSyncPredicate.all() // who to send the data to
                    ));
    public static final AttachmentType<MailboxAttachmentData> MAILBOX_ATTACHMENT_TYPE = AttachmentRegistry.create(
            Identifier.of(BlazinBlazeTwitchMod.MOD_ID,"mailbox_data"),
            builder->builder // we are using a builder chain here to configure the attachment data type
                    .initializer(()->MailboxAttachmentData.DEFAULT) // a default value to provide if you dont supply one
                    .persistent(MailboxAttachmentData.CODEC) // how to save and load the data when the object it is attached to is saved or loaded
                    .syncWith(
                            MailboxAttachmentData.PACKET_CODEC,  // how to turn the data into a packet to send to players
                            AttachmentSyncPredicate.all() // who to send the data to
                    ));
    public static final AttachmentType<RadioAttachmentData> RADIO_ATTACHMENT_TYPE = AttachmentRegistry.create(
            Identifier.of(BlazinBlazeTwitchMod.MOD_ID,"radio_data"),
            builder->builder // we are using a builder chain here to configure the attachment data type
                    .initializer(()->RadioAttachmentData.DEFAULT) // a default value to provide if you dont supply one
                    .persistent(RadioAttachmentData.CODEC) // how to save and load the data when the object it is attached to is saved or loaded
                    .syncWith(
                            RadioAttachmentData.PACKET_CODEC,  // how to turn the data into a packet to send to players
                            AttachmentSyncPredicate.all() // who to send the data to
                    ));


    public static void init() {
        // This empty method can be called from the mod initializer to ensure our component type is registered at mod initialization time
        // ModAttachmentTypes.init();
    }
}
