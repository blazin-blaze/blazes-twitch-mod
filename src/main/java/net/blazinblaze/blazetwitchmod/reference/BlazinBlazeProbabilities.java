package net.blazinblaze.blazetwitchmod.reference;

public enum BlazinBlazeProbabilities {
    ZOMBIE_DEFAULT(0.7, "zombieProb"),
    BLAZE_DEFAULT(0.7, "blazeProb"),
    DRAGON_DEFAULT(0.0001, "dragonProb"),
    WITHER_DEFAULT(0.0001, "witherProb"),
    CREEPER_DEFAULT(0.6, "creeperProb"),
    TNT_DEFAULT(0.7, "tntProb"),
    SKELETON_DEFAULT(0.7, "skeletonProb"),
    WARDEN_DEFAULT(0.0001, "wardenProb"),
    BRUTE_DEFAULT(0.1, "bruteProb"),
    GUARDIAN_DEFAULT(0.7, "guardianProb"),
    GHAST_DEFAULT(0.5, "ghastProb"),
    LIGHTNING_BOLT_DEFAULT(0.1, "lightningBoltProb"),
    SPIDER_DEFAULT(0.7, "spiderProb"),
    VEX_DEFAULT(0.5, "vexProb"),
    EVOKER_DEFAULT(0.5, "evokerProb"),
    PILLAGER_DEFAULT(0.5, "pillagerProb"),
    BREEZE_DEFAULT(0.5, "breezeProb"),
    HOGLIN_DEFAULT(0.7, "hoglinProb"),
    PIGLIN_DEFAULT(0.7, "piglinProb"),
    ILLUSIONER_DEFAULT(0.4, "illusionerProb"),
    SLIME_DEFAULT(0.7, "slimeProb"),
    PHANTOM_DEFAULT(0.5, "phantomProb"),
    RAVAGER_DEFAULT(0.4, "ravagerProb"),
    SHULKER_DEFAULT(0.6, "shulkerProb"),
    SILVERFISH_DEFAULT(0.7, "silverfishProb"),
    VINDICATOR_DEFAULT(0.5, "vindicatorProb"),
    VILLAGER_DEFAULT(0.7, "villagerProb"),
    FISH_DEFAULT(0.7, "fishProb"),
    IRON_GOLEM_DEFAULT(0.6, "ironGolemProb"),
    MUTATION_DEFAULT(0.2, "mutationProb"),
    RARE_MUTATION_DEFAULT(0.05, "rareMutationProb"),
    POTION_MUTATION_DEFAULT(0.3, "potionMutationProb"),
    FLING_DEFAULT(0.2, "flingProb"),
    NUKE_DEFAULT(0.2, "nukeProb"),
    MEGA_NUKE_DEFAULT(0.1, "megaNukeProb"),
    TELEPORT_DEFAULT(0.03, "teleportProb"),
    LOOT_CRATE_DEFAULT(0.2, "lootCrateProb"),
    WOLF_DEFAULT(0.7, "wolfProb"),
    LIFE_LINKED_DEFAULT(0.03, "lifeLinkedProb"),
    WOLF_ARMORED_DEFAULT(0.3, "armoredWolfProb"),
    CAT_DEFAULT(0.7, "catProb"),
    PARROT_DEFAULT(0.7, "parrotProb"),
    POTION_EFFECT_DEFAULT(0.6, "potionEffectProb"),
    CHICKEN_BOMB_DEFAULT(0.2, "chickenBombProb"),
    MESSAGE_DEFAULT(0.5, "messageProb"),
    LETTER_DEFAULT(0.8, "letterDefaultProb"),
    LETTER_STARRY(0.5, "letterStarryProb"),
    LETTER_BLAZIN(0.3, "letterBlazinProb"),
    LETTER_EPIC(0.3, "letterEpicProb"),
    LETTER_MINECRAFT(0.5, "letterMinecraftProb"),
    LETTER_ERROR(0.1, "letterErrorProb"),
    LETTER_SINISTER(0.5, "letterSinisterProb"),
    VOICE_MESSAGE_DEFAULT(0.6, "voiceMessageProb"),
    AUTOMATIC_VOICE_MESSAGE_DEFAULT(0.1, "automaticVoiceMessageProb"),
    DELETE_ITEM_DEFAULT(0.3, "deleteItemProb"),
    DELETE_INVENTORY_DEFAULT(0.01, "deleteInventoryProb"),
    VOID_DEFAULT(0.05, "voidProb"),
    BEDDY_DEFAULT(0.1, "beddyProb"),
    SOUND_DEFAULT(0.5, "soundProb"),
    FULLSCREEN_DEFAULT(0.2, "fullscreenProb"),
    CLOSE_DEFAULT(0.01, "closeProb"),
    DECREASE_TIMER_DEFAULT(0.1, "decreaseTimerProb"),
    EMOTE_DISPLAY_DEFAULT(0.4, "emoteDisplayProb"),
    IMMUNITY_DEFAULT(0.005, "immunityProb"),
    CHATTER_SPAWN(1, "chatterSpawn"),
    CHATTER_HOLD(1, "chatterHold");

    public final double probabilty;
    public final String key;

    BlazinBlazeProbabilities(final double probability, final String key) {
        this.probabilty = probability;
        this.key = key;
    }

    public double getProbabilty() {
        return probabilty;
    }
    public String getKey() {
        return key;
    }
}
