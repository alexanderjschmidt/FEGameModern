package heros.journey.initializers.base;

import heros.journey.Application;
import heros.journey.GameState;
import heros.journey.entities.Entity;
import heros.journey.entities.actions.Action;
import heros.journey.initializers.InitializerInterface;
import heros.journey.screens.MainMenuScreen;
import heros.journey.ui.HUD;

public class Actions implements InitializerInterface {

    public static Action wait, end_turn, exit_game, attack;

	static {
		/*end_turn = new Action("End Turn", true) {
			@Override
			public void onSelect(GameState gameState, Entity selected) {
				gameState.nextTurn();
			}

			@Override
			public boolean requirementsMet(GameState gameState, Entity selected) {
				return true;
			}
		};*/
        exit_game = new Action("Exit Game", true) {
			@Override
			public void onSelect(GameState gameState, Entity selected) {
                Application.get().setScreen(new MainMenuScreen(Application.get()));
			}

			@Override
			public boolean requirementsMet(GameState gameState, Entity selected) {
				return true;
			}
		};
        wait = new Action("Wait") {
			@Override
			public void onSelect(GameState gameState, Entity selected) {
			}

			@Override
			public boolean requirementsMet(GameState gameState, Entity selected) {
				return true;
			}
		};
		/*attack = new TargetAction("Attack", 0, null, RangeColor.RED, true) {
			@Override
			public void onHover(GameState gameState, Entity selected) {
				Entity e = selected;
				gameState.getRangeManager().clearRange();
				gameState.getRangeManager().setDistanceRangeAt((int) e.getXCoord(), (int) e.getYCoord(), e.getEntityClass().getRanges(), rangeType);
			}

			public void onSelect(GameState gameState, Entity selected) {
				HUD.get().setState(HUDState.TARGET);
				gameState.getRangeManager().updateTargets(selected, true, selected.getEntityClass().getRanges(), rangeType);
				gameState.getRangeManager().pointAtTarget(0);
			}

			@Override
			public boolean requirementsMet(GameState gameState, Entity selected) {
				Entity e = selected;
				return selected.getEntityClass().getRanges().length > 0 && gameState.getRangeManager().updateTargets(e, true, e.getEntityClass().getRanges(), rangeType).size() > 0;
			}

			@Override
			public void targetEffect(GameState gameState, Entity selected, int targetX, int targetY) {
				Entity attacker = selected;
				Entity defender = gameState.getEntities().get(targetX, targetY);
				// System.out.println(gameState.getEntities().getBuilding(targetX, targetY));
				int damageTaken = defender.calcDamageTaken(attacker);

				Buff parry = defender.getBuff(BuffManager.get().parry);
				if (parry != null && parry.isActive() && EntityManager.getDistanceBetween(attacker, defender) == 1) {
					parry.activate(gameState, selected, damageTaken, damageTaken);
					damageTaken -= 1;
				}

				if (EntityManager.getDistanceBetween(attacker, defender) == 1) {
					attacker.lunge(0, defender);
					gameState.getEntities().addEffect(0, new Effect(ResourceManager.get().slash[(int) (Math.random() * 6)], defender.getXCoord() * GameCamera.get().getSize(), defender.getYCoord() * GameCamera.get().getSize()));
				} else {
					gameState.getEntities().addEffect(0, new Effect(ResourceManager.get().arrow, attacker.getXCoord() *
                        GameCamera.get().getSize(), attacker.getYCoord() *
                        GameCamera.get().getSize(), defender.getXCoord() *
                        GameCamera.get().getSize(), defender.getYCoord() *
                        GameCamera.get().getSize(), true));
				}
				if (damageTaken > 0) {
					defender.vibrate(.2f, attacker);
					defender.getStats().adjustHealth(-damageTaken);
				}
				HUD.get().getCursor().clearSelected(true);
			}

			@Override
			public String getUIMessage(GameState gameState, Entity selected, int targetX, int targetY) {
				Entity attacker = selected;
				Entity defender = gameState.getEntities().get(targetX, targetY);
				int damage = defender.calcDamageTaken(attacker);
				String message = "Attack for " + damage + " damage";
				return message;
			}

			@Override
			public int utilityFunc(Entity user, Entity e) {
				return (int) (user.getTeam() != e.getTeam() && e.calcDamageTaken(user) != 0 ? Stats.MAX_HEALTH / e.getStats().getHealth() : 0);
			}
		};*/
	}

}
