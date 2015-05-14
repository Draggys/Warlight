package bot.mcts;

import main.Map;
import main.Region;
import move.AttackTransferMove;
import move.MoveResult;
import move.PlaceArmiesMove;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;

public class TreeNode {
    static Random r = new Random();
    static int nActions = 5;
    static double epsilon = 1e-6;

    TreeNode[] children;
    double nVisits, totValue;
    MCState state; //parent

    public TreeNode(MCState state) {
        this.state = state;
    }

    public void selectAction() {
        LinkedList<TreeNode> visited = new LinkedList<TreeNode>();
        TreeNode cur  = this;
        visited.add(this);
        while (!cur.isLeaf()) {
            cur = cur.select();
            visited.add(cur);
        }
        cur.expand();
        TreeNode newNode = cur.select();
        visited.add(newNode);
        double value = rollOut(newNode);
        for (TreeNode node : visited) {
            node.updateStats(value);
        }
    }

    public void expand() {
        children = new TreeNode[nActions];
        for (int i = 0; i < nActions; i++) {
            children[i] = new TreeNode(state);

            // Todo: monte carlo too slow
            boolean run = state.state.getRoundNumber() > 0;
            if (run) {
                MCState state = new MCState(this.state);

                /*
                LinkedList<Region> regions = state.state.getVisibleMap().getRegions();
                for(Region r : regions) {
                    if(r.ownedByPlayer(state.state.getMyPlayerName())) {
                        r.setArmies(r.getArmies() + state.state.getStartingArmies());
                        break;
                    }
                }

                for(Region r : regions) {
                    if(r.ownedByPlayer(state.state.getMyPlayerName())) {
                        if(r.getNeighbors().get(r.getNeighbors().size() - 1).getArmies() < r.getArmies() * 2 + 1) {
                            r.getNeighbors().get(r.getNeighbors().size()- 1).setArmies(0);
                            break;
                        }
                    }
                }
                */

                // new stuff from here on
                // Todo: update state according to our strategy.
                ArrayList<PlaceArmiesMove> pam = state.getPlaceArmiesFrontLine();
                ArrayList<AttackTransferMove> atm = state.getAttackTransferFrontLine();
                LinkedList<Region> regions = state.state.getVisibleMap().getRegions();
                Map map = state.state.getVisibleMap();

                // Deploy to region
                for (PlaceArmiesMove a : pam) {
                    Region aRegion = a.getRegion();
                    int armies = a.getArmies();
                    for (int j = 0; j < regions.size(); j++) {
                        if (aRegion == regions.get(j)) {
                            map.getRegion(j).setArmies(map.getRegion(j).getArmies() + armies);
                            break;
                        }
                    }
                }

/*
                // Attack
                boolean attacked = false;
                for (AttackTransferMove a : atm) {
                    Region fromRegion = a.getFromRegion();
                    Region toRegion = a.getToRegion();
                    int armies = a.getArmies();
                    for (int j = 0; j < regions.size(); j++) {
                        if (fromRegion == regions.get(j)) {
                            LinkedList<Region> neighbours = fromRegion.getNeighbors();
                            for (int k = 0; k < neighbours.size(); k++) {
                                if (neighbours.get(k) == toRegion) {
                                    int fromCurr = state.state.getVisibleMap().getRegion(j).getArmies();
                                    int toCurr = state.state.getVisibleMap().getRegion(k).getArmies();
                                    if (toRegion.ownedByPlayer(state.state.getMyPlayerName())) {
                                        state.state.getVisibleMap().getRegion(k).setArmies(toCurr + armies);
                                    } else {
                                        // Assume that the attack was succesful
                                        state.state.getVisibleMap().getRegion(k).setArmies(0);
                                    }
                                    state.state.getVisibleMap().getRegion(j).setArmies(fromCurr - armies);
                                }
                            }
                            break;
                        }
                    }
                }
*/

            }
            children[i].state = state;
        }
    }

    public TreeNode select() {
        TreeNode selected = null;
        double bestValue = Double.MIN_VALUE;
        for (TreeNode c : children) {
            double uctValue =
                    c.totValue / (c.nVisits + epsilon) +
                            Math.sqrt(Math.log(nVisits+1) / (c.nVisits + epsilon)) +
                            r.nextDouble() * epsilon;
            if(uctValue > bestValue) {
                selected = c;
                bestValue = uctValue;
            }
        }
        return selected;
    }

    public boolean isLeaf() {
        return children == null;
    }

    public double rollOut(TreeNode tn) {
        assert tn == null;

        MCState mcState = new MCState(state);
        return mcState.getResult();
    }

    public void updateStats(double value) {
        nVisits++;
        totValue += value;
    }

    public int arity() {
        return children == null ? 0 : children.length;
    }


}
