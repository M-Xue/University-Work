# Design Document

Notes on design of system. May change during development.

### Collectables

Not quite sure yet what CollectableEntity class is supposed to do, apart from being a parent class for all collectable and buildable entities.

All collectable entities inherit from the Collectable class. This includes:
- Treasure
- Key
- Potion (which is further subclassed by InvincibilityPotion and InvisibilityPotion)
- Wood
- Arrow
- Bomb 
- Sword

All buildable entities also inherit from the Collectable class. This includes:
- Bow
- Shield

Some Collectable (potions, Bomb, Sword) and all Buildable entities implement the Durability interface which specifies how long they last and tracks how many more times they can be used.

- [ ] Refactor Durability interface to be a mixin class (bc getDurability() and setDurability() are shared by all entities that implement it anyway.)

### Player

- Player's inventory represented by Map<Class<?>, List<Collectable>> inventory. Each key is a subclass of Collectable and each value is a list of Collectable objects of that subclas. E.g.
{
    Treasure.class: [Treasure, Treasure, Treasure],
    Key.class: [Key], // Player can only hold one key at a time
    Potion.class: [InvincibilityPotion, InvisibilityPotion, InvincibilityPotion],
    Wood.class: [Wood, Wood, Wood],
    Arrow.class: [Arrow, Arrow, Arrow],
    Bomb.class: [Bomb, Bomb],
    Sword.class: [Sword, Sword, Sword],
    Bow.class: [Bow, Bow, Bow],
    Shield.class: [Shield, Shield, Shield]
}

- [ ] Might consider changing inventory to a Set<Collectable>.

### Questions

Design
- [ ] Position in Entity or lower?
- [ ] Should player inherit from Entity base class?
- [ ] Should we have a Movable interface? With Player, Boulder and Mercenary implementing it?

Implementation
- [ ] How do we represent the player's inventory?
- [ ] Buildable entity ought to inherit from CollectableEntity but cannot since it has not position. Should we use a `Position` interface for entities with a position?