package dungeonmania.entities.player;

import java.util.*;
import java.util.stream.Collectors;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.dungeon.actions.*;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.ItemEntity;
import dungeonmania.entities.LivingEntity;
import dungeonmania.entities.behaviours.*;
import dungeonmania.entities.buildable.Bow;
import dungeonmania.entities.buildable.BuildableEntity;
import dungeonmania.entities.buildable.MidnightArmour;
import dungeonmania.entities.buildable.Shield;
import dungeonmania.entities.collectable.SunStone;
import dungeonmania.entities.collectable.Sword;
import dungeonmania.entities.collectable.potions.InvincibilityPotion;
import dungeonmania.entities.collectable.potions.InvisibilityPotion;
import dungeonmania.entities.collectable.potions.Potion;
import dungeonmania.battle.Battle;
import dungeonmania.entities.moving.Assassin;
import dungeonmania.entities.moving.Mercenary;
import dungeonmania.entities.moving.MovingEntity;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Pair;
import dungeonmania.util.Position;

import org.json.JSONObject;

public class Player extends LivingEntity implements Incrementable, Triggerable {
    private ArrayList<PlayerAction> actions = new ArrayList<>();
    private Inventory inventory = new Inventory();
    private ArrayList<Battle> battleHistory = new ArrayList<>();

    /**
     * To queue up potions to be used sequentially.
     * Treat the start of the queue as the active potion.
     * Once it wears off (removed from queu), the next potion in the
     * queue is considered active.
     */
    private ArrayList<Potion> potionQueue = new ArrayList<>();

    private int enemiesKilled = 0;
    private Position lastPosition;

    private boolean isDummy = false;
    private ArrayList<PlayerAction> actionsQueue = new ArrayList<>();
    private boolean isCancelMove = false;

    public Player(int id, Position position, JSONObject configJSON) {
        super(
                id,
                "player",
                configJSON.getDouble("player_health"),
                configJSON.getDouble("player_attack"));
        setPosition(position);
    }

    public Player(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")), configJSON);
    }

    public int getEnemiesKilled() {
        return enemiesKilled;
    }

    public void incrementEnemiesKilled() {
        this.enemiesKilled++;
    }

    public Position getLastPosition() {
        return lastPosition;
    }

    private List<Mercenary> getAllys() {
        return getDungeon().getEntities().stream()
                .filter(Mercenary.class::isInstance)
                .map(entity -> (Mercenary) entity)
                .filter(Mercenary::isAlly)
                .collect(Collectors.toList());
    }

    private List<Assassin> getAssassinAllys() {
        return getDungeon().getEntities().stream()
                .filter(Assassin.class::isInstance)
                .map(entity -> (Assassin) entity)
                .filter(Assassin::isAlly)
                .collect(Collectors.toList());
    }

    public void setActions(List<PlayerAction> actions) {
        this.actions = new ArrayList<>(actions);
    }

    public List<PlayerAction> getActions() {
        return actions;
    }

    public boolean isDummy() {
        return isDummy;
    }

    public void setIsDummy(boolean isDummy) {
        this.isDummy = isDummy;
    }

    public void setActionsQueue(List<PlayerAction> actionsQueue) {
        this.actionsQueue = new ArrayList<>(actionsQueue);
    }

    public boolean isHostileToClones() {
        if (hasItem(SunStone.class) || hasItem(MidnightArmour.class) || hasPotionEffect(InvisibilityPotion.class)) {
            return false;
        }
        return true;

    }
    // Overrides
    // ==============================================================================

    @Override
    public String getType() {
        if (isDummy) {
            return "older_player";
        } else {
            return super.getType();
        }
    }

    @Override
    public Player clone() {
        Inventory clonedInventory = this.inventory.clone();
        ArrayList<PlayerAction> clonedActions = new ArrayList<>(actions);
        ArrayList<Potion> clonedPotionQueue = new ArrayList<>(potionQueue);
        potionQueue.forEach(potion -> clonedPotionQueue.add((Potion) potion.clone()));
        ArrayList<Battle> clonedBattleHistory = new ArrayList<>(battleHistory);

        Player clone = (Player) super.clone();
        clone.inventory = clonedInventory;
        clone.actions = clonedActions;
        clone.potionQueue = clonedPotionQueue;
        clone.battleHistory = clonedBattleHistory;
        return clone;
    }

    /**
     * Handle events related to Player that occur every tick.
     */
    @Override
    public void increment() {
        if (isCancelMove) {
            isCancelMove = false;
            return;
        }

        if (isDummy) {
            if (actionsQueue.isEmpty()) {
                getDungeon().removeEntity(this);
            } else {
                PlayerAction action = actionsQueue.remove(0);
                action.execute(this);
            }
        }

        if (!potionQueue.isEmpty()) {
            // Tick player's active potion and remove it if expired.
            Potion activePotion = potionQueue.get(0);
            activePotion.incrementDurability(-1);
            if (activePotion.isExpired()) {
                potionQueue.remove(0);
            }
        }
    }

    @Override
    public void onTrigger(Entity entity) {
        if (entity instanceof MovingEntity && ((MovingEntity) entity).isHostile()) {
            getDungeon().addBattle(new Battle(this, (MovingEntity) entity));
        } else if (entity instanceof Player) {
            Player otherPlayer = (Player) entity;
            if (isHostileToClones() && otherPlayer.isHostileToClones()) {
                getDungeon().addBattle(new Battle(this, otherPlayer));
            }
        }
    }

    @Override
    public Pair<Double, List<ItemEntity>> attack() {
        double attack = getBasicAttack();
        List<ItemEntity> itemsUsed = new ArrayList<>();

        if (hasPotionEffect(InvincibilityPotion.class)) {
            return Pair.of(Double.MAX_VALUE, List.of());
        }

        Sword sword = inventory.getFirstItem(Sword.class);
        if (sword != null) {
            attack = sword.getBonusedAttack(attack);
            sword.incrementDurability(-1);
            itemsUsed.add(sword);
            if (sword.getDurability() <= 0)
                inventory.removeItem(sword);
        }

        List<Mercenary> allys = getAllys();
        for (Mercenary ally : allys) {
            attack += ally.getAllyAttack();
        }

        MidnightArmour midnightArmour = inventory.getFirstItem(MidnightArmour.class);
        if (midnightArmour != null) {
            attack = midnightArmour.getBonusedAttack(attack);
            itemsUsed.add(midnightArmour);
        }

        Bow bow = inventory.getFirstItem(Bow.class);
        if (bow != null) {
            attack = bow.getBonusedAttack(attack);
            bow.incrementDurability(-1);
            itemsUsed.add(bow);
            if (bow.getDurability() <= 0)
                inventory.removeItem(bow);
        }

        return Pair.of(attack, itemsUsed);
    }

    @Override
    public Pair<Double, List<ItemEntity>> onDamaged(double attack) {
        List<ItemEntity> itemsUsed = new ArrayList<>();

        if (hasPotionEffect(InvincibilityPotion.class)) {
            return Pair.of(0d, itemsUsed);
        }

        Shield shield = inventory.getFirstItem(Shield.class);
        if (shield != null) {
            attack -= shield.getDefence();
            shield.incrementDurability(-1);
            itemsUsed.add(shield);
            if (shield.getDurability() <= 0)
                inventory.removeItem(shield);
        }

        List<Mercenary> allys = getAllys();
        for (Mercenary ally : allys) {
            attack -= ally.getAllyDefence();
        }

        List<Assassin> assassinAllys = getAssassinAllys();
        for (Assassin ally : assassinAllys) {
            attack -= ally.getAllyDefence();
        }

        MidnightArmour midnightArmour = inventory.getFirstItem(MidnightArmour.class);
        if (midnightArmour != null) {
            attack -= midnightArmour.getDefence();
            itemsUsed.add(midnightArmour);
        }

        // After defence bonuses, attack should not be less than 0.
        attack = Math.max(0, attack);

        double deltaHealth = -(attack / 10);

        setHealth(getHealth() + deltaHealth);
        return Pair.of(deltaHealth, itemsUsed);
    }

    // Others
    // =================================================================================

    public void move(Direction movementDirection) {
        Dungeon dungeon = getDungeon();
        Position attemptedPosition = getPosition().translateBy(movementDirection);

        List<PreTriggerable> preTriggerables = dungeon.getEntitiesOnTile(attemptedPosition).stream()
                .filter(PreTriggerable.class::isInstance)
                .map(PreTriggerable.class::cast)
                .collect(Collectors.toList());
        preTriggerables.forEach(entity -> entity.preTrigger(this));

        if (dungeon.isOpenTile(attemptedPosition)) {
            lastPosition = getPosition();
            dungeon.moveEntity(movementDirection, this);
        }

        actions.add(new MovementAction(movementDirection));
    }

    public void useItem(String itemUsedId) throws IllegalArgumentException, InvalidActionException {
        int itemId = Integer.parseInt(itemUsedId);
        ItemEntity item = inventory.getItem(itemId);
        if (item == null) {
            throw new InvalidActionException(
                    String.format("Invalid item %s used: not in the player's inventory", itemUsedId));
        }

        if (!(item instanceof Usable)) {
            throw new IllegalArgumentException(
                    String.format("Invalid item %s used: not a usable item", itemUsedId));
        }

        Usable usableItem = (Usable) item;
        usableItem.onUse(this);
        inventory.removeItem(itemId);
        actions.add(new UseItemAction(itemUsedId));
    }

    /**
     * Build a buildable entity: bow, shield, sceptre, midnight_armour
     * 
     * @param buildableType The type of the buildable entity.
     */
    public void build(String buildableType) throws IllegalArgumentException, InvalidActionException {
        Dungeon dungeon = getDungeon();

        Class<? extends BuildableEntity> buildableClass = EntityFactory.getBuildableEntityClass(buildableType);
        BuildableEntity buildable;
        int buildableId = dungeon.generateEntityId();

        try {
            buildable = buildableClass
                    .getConstructor(Integer.TYPE, JSONObject.class)
                    .newInstance(buildableId, dungeon.getConfigJSON());
            buildable.setDungeon(dungeon);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Invalid buildable type %s: Not a buildable entity.", buildableType));
        }

        if (!buildable.canBuild()) {
            throw new InvalidActionException("Cannot build this buildable in current conditions.");
        }

        List<List<Pair<Class<? extends ItemEntity>, Integer>>> materials = buildable.getMaterials();
        List<Pair<Class<? extends ItemEntity>, Integer>> materialsToConsumed = inventory
                .checkAvailableMaterials(materials);

        if (materialsToConsumed == null) {
            throw new InvalidActionException("Player does not have the required materials to build.");
        }

        for (Pair<Class<? extends ItemEntity>, Integer> material : materialsToConsumed) {
            if (material.x == SunStone.class) {
                // sunstones retained after using to build a buildable.
                continue;
            }
            inventory.removeItem(material.x, material.y);
        }

        buildable.setDungeon(getDungeon());
        inventory.addItem(buildable);
        actions.add(new BuildAction(buildableType));
    }

    public void interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        int id = Integer.parseInt(entityId);

        Entity entity = getDungeon().getEntity(id);
        if (entity != null) {
            if (entity instanceof Interactable) {
                ((Interactable) entity).onInteract(this);
            } else {
                throw new IllegalArgumentException(
                        String.format("Invalid entity to interact with: entityId %s is not interactable.", entityId));
            }
        } else {
            throw new IllegalArgumentException(
                    String.format("Invalid entity to interact with: entityId  %s does not exist.", entityId));
        }

        actions.add(new InteractAction(entityId));
    }

    public List<String> getBuildableNames() {
        List<String> result = new ArrayList<>();
        Dungeon dungeon = getDungeon();
        JSONObject config = dungeon.getConfigJSON();

        for (Class<? extends BuildableEntity> buildableClass : EntityFactory.getBuildableClasses()) {
            BuildableEntity buildable;
            int buildableId = dungeon.generateEntityId();
            try {
                buildable = buildableClass
                        .getConstructor(Integer.TYPE, JSONObject.class)
                        .newInstance(buildableId, config);
                buildable.setDungeon(getDungeon());
            } catch (Exception e) {
                // System.err.println(String.format("Error creating buildable %s. Continuing to
                // next buildable type", buildableClass.getSimpleName()));
                // e.printStackTrace();
                continue;
            }
            if (buildable.canBuild() && inventory.checkAvailableMaterials(buildable.getMaterials()) != null) {
                result.add(buildable.getType());
            }
        }

        return result;
    }

    public void addPotionEffect(Potion potion) {
        potionQueue.add(potion);
    }

    public boolean hasPotionEffect(Class<? extends Potion> potionType) {
        if (potionQueue.size() > 0) {
            Potion potion = potionQueue.get(0);
            return potionType.isInstance(potion);
        }
        return false;
    }

    public void addBattle(Battle battle) {
        battleHistory.add(battle);
    }

    public List<BattleResponse> getBattleResponses() {
        return battleHistory.stream().map(Battle::getResponse).collect(Collectors.toList());
    }

    public void cancelMove() {
        isCancelMove = true;
    }

    // Inventory
    // ==============================================================================

    public List<ItemResponse> getInventoryResponse() {
        return inventory.getResponse();
    }

    public boolean hasItem(Class<?> itemClass) {
        return inventory.hasItem(itemClass);
    }

    public <T> T getFirstItem(Class<T> itemClass) {
        return inventory.getFirstItem(itemClass);
    }

    public <T> List<T> getItemsByClass(Class<T> itemClass) {
        return inventory.getItemsByClass(itemClass);
    }

    public void addItem(ItemEntity itemEntity) {
        inventory.addItem(itemEntity);
    }

    public void removeItem(ItemEntity item) {
        inventory.removeItem(item);
    }

    public void removeItem(Class<?> itemClass, int amount) {
        inventory.removeItem(itemClass, amount);
    }

    public int countItems(Class<?> itemClass) {
        return inventory.countItems(itemClass);
    }
}
