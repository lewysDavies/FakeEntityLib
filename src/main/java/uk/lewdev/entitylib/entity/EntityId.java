package uk.lewdev.entitylib.entity;

import org.bukkit.entity.EntityType;

/**
 * Generated using {@link uk.lewdev.datagen.EntityEnumGenerator}
 *
 * @version 1.16.4
 */
public enum EntityId {
    MINECART_TNT(51), 
    CHICKEN(9), 
    OCELOT(54), 
    PIGLIN_BRUTE(61), 
    SKELETON_HORSE(74), 
    SALMON(68), 
    ENDER_SIGNAL(25), 
    STRAY(82), 
    IRON_GOLEM(36), 
    VEX(92), 
    SMALL_FIREBALL(76), 
    TRIDENT(88), 
    EVOKER_FANGS(23), 
    HUSK(34), 
    ITEM_FRAME(38), 
    ENDER_PEARL(85), 
    PUFFERFISH(65), 
    TURTLE(91), 
    SHEEP(69), 
    TROPICAL_FISH(90), 
    SLIME(75), 
    BLAZE(5), 
    BAT(3), 
    FIREWORK(27), 
    SPECTRAL_ARROW(79), 
    ENDER_DRAGON(19), 
    SNOWMAN(77), 
    VILLAGER(93), 
    LLAMA_SPIT(43), 
    CAT(7), 
    DROPPED_ITEM(37), 
    SNOWBALL(78), 
    PIG(59), 
    EGG(84), 
    TRADER_LLAMA(89), 
    MINECART_CHEST(46), 
    CREEPER(12), 
    WITHER(97), 
    ENDER_CRYSTAL(18), 
    EXPERIENCE_ORB(24), 
    LIGHTNING(41), 
    HORSE(33), 
    ZOMBIE_HORSE(103), 
    ILLUSIONER(35), 
    ZOMBIE_VILLAGER(104), 
    ENDERMITE(21), 
    SKELETON(73), 
    EVOKER(22), 
    THROWN_EXP_BOTTLE(86), 
    WITHER_SKELETON(98), 
    MINECART_HOPPER(49), 
    PAINTING(55), 
    PANDA(56), 
    GIANT(30), 
    COD(10), 
    WANDERING_TRADER(95), 
    DROWNED(16), 
    FISHING_HOOK(107), 
    SQUID(81), 
    MULE(52), 
    SPIDER(80), 
    GUARDIAN(31), 
    PHANTOM(58), 
    ENDERMAN(20), 
    VINDICATOR(94), 
    ZOMBIFIED_PIGLIN(105), 
    MAGMA_CUBE(44), 
    MUSHROOM_COW(53), 
    ZOGLIN(101), 
    FALLING_BLOCK(26), 
    POLAR_BEAR(63), 
    PILLAGER(62), 
    PARROT(57), 
    ZOMBIE(102), 
    COW(11), 
    FOX(28), 
    BEE(4), 
    FIREBALL(39), 
    SHULKER_BULLET(71), 
    GHAST(29), 
    DOLPHIN(13), 
    BOAT(6), 
    STRIDER(83), 
    AREA_EFFECT_CLOUD(0), 
    LEASH_HITCH(40), 
    RAVAGER(67), 
    MINECART_MOB_SPAWNER(50), 
    SHULKER(70), 
    MINECART_COMMAND(47), 
    MINECART_FURNACE(48), 
    SPLASH_POTION(87), 
    DRAGON_FIREBALL(15), 
    WOLF(100), 
    ELDER_GUARDIAN(17), 
    DONKEY(14), 
    WITCH(96), 
    WITHER_SKULL(99), 
    PLAYER(106), 
    SILVERFISH(72), 
    ARMOR_STAND(1), 
    CAVE_SPIDER(8), 
    LLAMA(42), 
    RABBIT(66), 
    ARROW(2), 
    HOGLIN(32), 
    MINECART(45), 
    PIGLIN(60), 
    PRIMED_TNT(64); 

    private int id;
    
    private EntityId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static int fromType(EntityType type) {
    	return EntityId.valueOf(type.name()).getId();
    }
}
