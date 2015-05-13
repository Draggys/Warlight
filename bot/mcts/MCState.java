package bot.mcts;

import bot.BotState;
import main.Map;
import main.Region;
import move.AttackTransferMove;
import move.PlaceArmiesMove;

import java.util.ArrayList;
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

        for (Region region : regions) {
            if(region.ownedByPlayer((myName))) {
                LinkedList<Region> neighbours = region.getNeighbors();
                for (Region neigh : neighbours) {
                    if (!neigh.ownedByPlayer(myName)) {
                        placeArmiesMoves.add(new PlaceArmiesMove(myName, region, armies));
                        return placeArmiesMoves;
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
}
