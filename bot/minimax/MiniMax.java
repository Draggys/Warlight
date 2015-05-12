package bot.minimax;

import bot.BotState;
import bot.Heuristics;
import main.Region;

import java.util.ArrayList;
import java.util.LinkedList;

public class MiniMax {
    Heuristics stateEval = new Heuristics();
    public State bestState = null;

    public int minimax(BotState node, int depth, String player, String enemyPlayer, boolean enemy) {
        if (depth == 0) { // or node is a terminal node
            return stateEval.frontLineState(node, player, enemy);
        }

        ArrayList<State> children = getChildren(node, player, enemy);
        int bestChildId = -1;
        // Maximizing player
        if (!enemy) {
            int bestVal = Integer.MIN_VALUE;
            int i = 0;
            for (State child : children) {
                int val = minimax(child.state, depth - 1, enemyPlayer, player, true);
                if (val > bestVal) {
                    bestVal = val;
                    bestChildId = i;
                }
                i++;
            }
            return bestVal;
        }

        // Minimizing player
        if (enemy) {
            int bestVal = Integer.MAX_VALUE;
            int i = 0;
            for (State child : children) {
                int val = minimax(child.state, depth - 1, enemyPlayer, player, false);
                if (val < bestVal) {
                    bestVal = val;
                    bestChildId = i;
                }
                i++;
            }
            return bestVal;
        }

        bestState = children.get(bestChildId);
        return -1;
    }

    /* Create possible children states
    ** for each front region, conquer from the 'left'
    **/
    private ArrayList<State> getChildren(BotState parent, String player, boolean enemy) {
        ArrayList<State> children = new ArrayList<State>();
        LinkedList<Region> frontLine = frontLineRegions(parent, player);

        for (Region fRegion : frontLine) {
            BotState child = new BotState();
            child.visibleMap = parent.visibleMap;
            LinkedList<Region> neighbours = child.getVisibleMap().getRegions();
            fRegion.setArmies(fRegion.getArmies() + parent.getStartingArmies());
            for (int i = 0; i < neighbours.size(); i++){
                if (fRegion.getArmies() < 3)
                    break;
                if (neighbours.get(i).ownedByPlayer(player))
                    continue;

                int armiesToSpend = armiesNeeded(fRegion, neighbours.get(i));
                fRegion.setArmies(fRegion.getArmies() - armiesToSpend);

                // Assumes that the attack was successful
                child.visibleMap.getRegions().get(i).setArmies(0);
            }

            children.add(new State(fRegion, child));
        }

        return children;
    }

    /*@return armies needed to conquer a region, returns 0 if it cannot be conquered */
    public int armiesNeeded(Region myRegion, Region enemyRegion) {
        int myArmies = myRegion.getArmies();
        int enemyArmies = enemyRegion.getArmies();

        // Todo: Replace with probability function
        if (enemyArmies * 2 < myArmies)
            return enemyArmies * 2;

        return 0;
    }

    /* @return owned regions which are adjacent to regions not owned */
    private LinkedList<Region> frontLineRegions(BotState state, String player) {
        LinkedList<Region> frontLine = new LinkedList<Region>();
        LinkedList<Region> regions = state.getVisibleMap().getRegions();
        for (Region region : regions) {
            if (region.ownedByPlayer(player)) {
                LinkedList<Region> neighbours = region.getNeighbors();
                for(Region neigh : neighbours) {
                    if (!neigh.ownedByPlayer(player)) {
                        frontLine.add(region);
                        break;
                    }
                }
            }
        }
        return frontLine;
    }

    /* @return enemy regions adjacent to a owned region */
    private LinkedList<Region> enemyRegions(Region myRegion, String player) {
        LinkedList<Region> enemyRegions = new LinkedList<Region>();

        for(Region region : myRegion.getNeighbors()) {
            if (!region.ownedByPlayer(player)) {
                enemyRegions.add(region);
            }
        }
        return enemyRegions;
    }
}
