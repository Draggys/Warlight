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
    public MCState state;

    long startTime;

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
          //  ArrayList<MCState> possibleMoves = state.getPossibleStates();
          //  MCState state = possibleMoves.get(0);
            for (int j = 0; j < 1000; j++) {}

            children[i] = new TreeNode(state);
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
