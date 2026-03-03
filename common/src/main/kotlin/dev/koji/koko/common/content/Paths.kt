package dev.koji.koko.common.content

object Paths {
    object DefaultSources {
        const val BLOCK_INTERACT = "block/interact"
        const val BLOCK_PLACE = "block/place"
        const val BLOCK_BREAK = "block/break"

        const val ENTITY_INTERACT = "entity/interact"
        const val ENTITY_TAME = "entity/tame"
        const val ENTITY_KILL = "entity/kill"

        const val PLAYER_RUN = "player/run"
        const val PLAYER_JUMP = "player/jump"
        const val PLAYER_CRAFTED = "player/crafted"
        const val PLAYER_FORGED = "player/forge"
        const val PLAYER_ATTACKED = "player/attacked"
        const val PLAYER_ITEM_USE = "player/item_use"
        const val PLAYER_CONSUMED = "player/consumed"
        const val PLAYER_STACK_DROP = "player/stack_drop"
        const val PLAYER_TRADE = "player/trade"
        const val PLAYER_DAMAGED = "player/damaged"
        const val PLAYER_DIED = "player/died"
    }

    object DefaultModifiers {
        const val PLAYER_ATTRIBUTE = "player/attribute"
        const val PLAYER_USE = "player/item_use"
        const val PLAYER_ATTACK = "player/item_attack"
        const val PLAYER_CONSUME = "player/item_consume"
        const val PLAYER_CRAFT = "player/craft"
        const val PLAYER_FORGE = "player/forge"
        const val PLAYER_ARMOR = "player/armor"
    }

    object DefaultFilters {
        const val ABOVE = "filter/above"
        const val RANGE = "filter/range"
        const val BELLOW = "filter/bellow"
        const val BLOCKED = "filter/blocked"
    }
}