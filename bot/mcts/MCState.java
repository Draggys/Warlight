package bot.mcts;

import bot.BotState;
import main.Map;
import main.Region;
import move.AttackTransferMove;
import move.PlaceArmiesMove;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * A game state used in Monte Carlo Tree Search
 */
public class MCState {
    int playerJustMoved; // our bot is 1 and opponent is 2
    BotState state = null;

    public MCState(BotState state) {
        this.state = state;
        playerJustMoved = 2; // the opponent just moved
    }

    public MCState(MCState mcState) {
        this.state = mcState.state;
        this.playerJustMoved = mcState.playerJustMoved;
    }

    /* Update state by placing armies */
    public void placeArmies(ArrayList<PlaceArmiesMove> placeArmies) {
        Map map = state.getVisibleMap();
        LinkedList<Region> visibleRegions = map.getRegions();
        Region vRegion = null;
        for(PlaceArmiesMove armies : placeArmies) {
            for (int i = 0; i < visibleRegions.size(); i++) {
                vRegion = visibleRegions.get(i);
                if(visibleRegions.get(i).getId() == armies.getRegion().getId()) {
                    vRegion.setArmies(vRegion.getArmies() + armies.getArmies());
                    break;
                }
            }
        }
        state.visibleMap.regions = visibleRegions;
    }

    /* Update state by attacking/transferring armies
    * Todo: something something super regions */
    public void attackTransfer(ArrayList<AttackTransferMove> attackTransferMoves) {
        Map map = state.getVisibleMap();
        LinkedList<Region> visibleRegions = map.getRegions();
        Region vRegion = null;
        for(AttackTransferMove atm : attackTransferMoves) {
            for (int i = 0; i < visibleRegions.size(); i++) {
                vRegion = visibleRegions.get(i);
                if(visibleRegions.get(i).getId() == atm.getFromRegion().getId()) {
                    for (int j = 0; j < visibleRegions.size(); j++) {
                        if (visibleRegions.get(j).getId() == atm.getToRegion().getId()) {
                            // assumes that the attack is a success and a loss of half the attacking army
                            visibleRegions.get(i).setArmies(visibleRegions.get(i).getArmies() - atm.getArmies());
                            visibleRegions.get(j).setArmies(Math.round(atm.getArmies() / 2));
                            visibleRegions.get(j).setPlayerName(state.getMyPlayerName());
                            break;
                        }
                    }
                }
            }
        }
        state.visibleMap.regions = visibleRegions;
    }

    /* Evaluates leaf node
     * @return 0 of draw, 1 if win, -1 if loss */
    public int getResult() {
        /*
        assert (state.getRoundNumber() != 100);

        LinkedList<Region> regions = state.getVisibleMap().getRegions();
        boolean playerOwned = false;
        boolean opponentOwned = false;
        for (Region region : regions) {
            if (region.ownedByPlayer(state.getMyPlayerName()))
                playerOwned = true;
            else if (region.ownedByPlayer(state.getOpponentPlayerName()))
                opponentOwned = true;
        }

        if (playerOwned && opponentOwned)
            return 0;
        else if(playerOwned && !opponentOwned)
            return 1;
        else if (!playerOwned && opponentOwned)
            return -1;
        else
            throw(new IllegalStateException());
        */

        int myScore = 0;
        int enemyScore = 0;
        String myName = state.getMyPlayerName();
        LinkedList<Region> regions = state.getVisibleMap().getRegions();
        for(Region region : regions) {
            if(region.ownedByPlayer(myName))
                myScore += region.getArmies();
            else
                enemyScore += region.getArmies();
        }

        int finalScore;
        if (myScore > enemyScore)
            finalScore = 1;
        else if (myScore < enemyScore)
            finalScore = -1;
        else
            finalScore = 0;

        return finalScore;
    }

    /* Same logic as starter bot
    * Todo: develop more tactics*/
    public ArrayList<PlaceArmiesMove> getPlaceArmies() {
        ArrayList<PlaceArmiesMove> placeArmiesMoves = new ArrayList<PlaceArmiesMove>();
        String myName = state.getMyPlayerName();
        int armies = 2;
        int armiesLeft = state.getStartingArmies();
        LinkedList<Region> visibleRegions = state.getVisibleMap().getRegions();

        while(armiesLeft > 0)
        {
            double rand = Math.random();
            int r = (int) (rand*visibleRegions.size());
            Region region = visibleRegions.get(r);

            if(region.ownedByPlayer(myName))
            {
                placeArmiesMoves.add(new PlaceArmiesMove(myName, region, armies));
                armiesLeft -= armies;
            }
        }
        return placeArmiesMoves;
    }

    public ArrayList<PlaceArmiesMove> getPlaceArmiesFrontLine() {
        ArrayList<PlaceArmiesMove> placeArmiesMoves = new ArrayList<PlaceArmiesMove>();
        String myName = state.getMyPlayerName();
        int armies = state.getStartingArmies();
        LinkedList<Region> regions = state.getVisibleMap().getRegions();

        int x = 2;
        int placeArmies = armies - x;
        boolean rest = false;

        for (Region region : regions) {
            if(region.ownedByPlayer((myName))) {
                LinkedList<Region> neighbours = region.getNeighbors();
                for (Region neigh : neighbours) {
                    if (!neigh.ownedByPlayer(myName)) {
                        placeArmiesMoves.add(new PlaceArmiesMove(myName, region, placeArmies));
                        rest = true;
                    }
                    if(rest)
                        break;
                }
            }
            if(rest)
                break;
        }

        Collections.reverse(regions);
        if(rest) {
            for (Region region : regions) {
                if(region.ownedByPlayer(myName)) {
                    LinkedList<Region> neighbours = region.getNeighbors();
                    for(Region neigh : neighbours) {
                        if(!neigh.ownedByPlayer(myName)) {
                            placeArmiesMoves.add(new PlaceArmiesMove(myName, region, x));
                            return placeArmiesMoves;
                        }
                    }
                }
            }
        }

        return getPlaceArmies();
    }

    /* Same logic as starter bot
    *  Todo: develop more tactics */
    public ArrayList<AttackTransferMove> getAttackTransfer() {
        ArrayList<AttackTransferMove> attackTransferMoves = new ArrayList<AttackTransferMove>();
        String myName = state.getMyPlayerName();
        int armies = 5;

        for(Region fromRegion : state.getVisibleMap().getRegions())
        {
            if(fromRegion.ownedByPlayer(myName)) //do an attack
            {
                ArrayList<Region> possibleToRegions = new ArrayList<Region>();
                possibleToRegions.addAll(fromRegion.getNeighbors());

                while(!possibleToRegions.isEmpty())
                {
                    double rand = Math.random();
                    int r = (int) (rand*possibleToRegions.size());
                    Region toRegion = possibleToRegions.get(r);

                    if(!toRegion.getPlayerName().equals(myName) && fromRegion.getArmies() > 6) //do an attack
                    {
                        attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, toRegion, armies));
                        break;
                    }
                    else if(toRegion.getPlayerName().equals(myName) && fromRegion.getArmies() > 1) //do a transfer
                    {
                        attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, toRegion, armies));
                        break;
                    }
                    else
                        possibleToRegions.remove(toRegion);
                }
            }
        }
        return attackTransferMoves;
    }

    public ArrayList<AttackTransferMove> getAttackTransferFrontLine() {
        ArrayList<AttackTransferMove> attackTransferMoves = new ArrayList<AttackTransferMove>();
        String myName = state.getMyPlayerName();

        LinkedList<Region> regions = state.getVisibleMap().getRegions();
        LinkedList<Region> frontLine = new LinkedList<Region>();
        // Collect front line
        for (Region region : regions) {
            if (region.ownedByPlayer((myName))) {
                LinkedList<Region> neighbours = region.getNeighbors();
                for (Region neigh : neighbours) {
                    if (!neigh.ownedByPlayer(myName)) {
                        frontLine.add(region);
                        break;
                    }
                }
            }
        }

        // Attack neighbours
        for (Region region : frontLine) {
            LinkedList<Region> neighbours = region.getNeighbors();
            int armiesLeft = region.getArmies();
            int lastEnemy = -1;
            for (int i = 0; i < neighbours.size(); i++) {
                if(neighbours.get(i).ownedByPlayer(myName))
                    continue;
                int amount = getArmiesToSpend(region, neighbours.get(i));
                if (amount == 0)
                    continue;
                else if (armiesLeft < 3)
                    break;
                else if (i == neighbours.size() - 1) {
                    if(armiesLeft > 2)
                        attackTransferMoves.add(new AttackTransferMove(myName, region, neighbours.get(i), armiesLeft - 1));
                }
                else {
                    attackTransferMoves.add(new AttackTransferMove(myName, region, neighbours.get(i), amount));
                    armiesLeft -= amount;
                    lastEnemy = attackTransferMoves.size() - 1;
                }
            }
            if(lastEnemy != -1 && armiesLeft > 1) {
                Region toAtk = attackTransferMoves.get(lastEnemy).getToRegion();
                int amount = attackTransferMoves.get(lastEnemy).getArmies();
                attackTransferMoves.set(lastEnemy, new AttackTransferMove(myName, region, toAtk, amount + armiesLeft - 1));
            }
        }

        // Move armies not on frontline
        for (Region region : regions) {
            if(region.getArmies() < 2)
                continue;
            if(region.ownedByPlayer(myName)) {
                if(!frontLine.contains(region)) {
                    if(region.getNeighbors().size() == 2) {
                        attackTransferMoves.add(new AttackTransferMove(myName, region, region.getNeighbors().get(0), region.getArmies() - 1)); //Todo: perhaps remove this
                    }
                    else
                        attackTransferMoves.add(new AttackTransferMove(myName, region, region.getNeighbors().get(region.getNeighbors().size()-1), region.getArmies() - 1));
                }
            }
        }

        return attackTransferMoves;
    }

    /* @return #armies to attack with
    * returns 0 if region has no armies to spare or attack success is uncertain */
    private int getArmiesToSpend(Region from, Region to) {
        int has = from.getArmies();
        int needed = to.getArmies() * 2 + 1;

        if (has > needed)
            return needed;

        return 0;
    }

    /* returns a list with possible states to transist to */
    public ArrayList<MCState> getPossibleStates() {
        ArrayList<MCState> possibleStates = new ArrayList<>();
        MCState newState = new MCState(this.state);

        ArrayList<PlaceArmiesMove> move = newState.getPlaceArmiesFrontLine();
        for(PlaceArmiesMove m : move) {
            int armies = m.getArmies();
            Region region = m.getRegion();
            String player = m.getPlayerName();

            newState.state.getVisibleMap().getRegion(region.getId()).setArmies(0);
        }

        return possibleStates;
    }
}
