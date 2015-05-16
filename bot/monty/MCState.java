package bot.monty;

import bot.BotState;
import bot.log.Log;
import main.Region;
import move.AttackTransferMove;
import move.PlaceArmiesMove;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class MCState {
    BotState state;     // current state
    Move move;          // the move that caused this state
    String myName;

    Log logger = new Log();

    public MCState(BotState state) {
        this.state = state;
        myName = state.getMyPlayerName();
    }

    /*@return state evaluation, i.e the ratio between owned regions and all regions */
    public double getResult() {
        int regions = state.getFullMap().getRegions().size();
        int ownedRegions = 0;
        for(Region region : state.getVisibleMap().getRegions())
            if (region.ownedByPlayer(myName))
                ownedRegions++;

        return ownedRegions / regions;
    }

    private int getSpendableUnits(Region to) {
        return to.getArmies() * 2;
    }

    /*@return owned regions that are adjacent to not owned regions
     * neutral regions included */
    public LinkedList<Region> getFrontLine() {
        LinkedList<Region> frontLine = new LinkedList<>();
        for (Region region : state.getVisibleMap().getRegions()) {
            if (region.ownedByPlayer(myName)) {
                for (Region neigh : region.getNeighbors()) {
                    if (!neigh.ownedByPlayer(myName)) {
                        frontLine.add(region);
                        break;
                    }
                }
            }
        }

        return frontLine;
    }

    /* for each nMoves moves pick one front line region and give it all reinforcements */
    public ArrayList<ArrayList<PlaceArmiesMove>> getPlaceArmiesMoves(int nMoves) {
        ArrayList<ArrayList<PlaceArmiesMove>> ret = new ArrayList<>();
        LinkedList<Region> frontLine = getFrontLine();


        int armies = state.getStartingArmies();

        for (int i = 0; i < frontLine.size(); i++) {
            if(i == nMoves) break;
            ret.add(new ArrayList<>());
            ret.get(i).add(new PlaceArmiesMove(myName, frontLine.get(i), armies));
        }

        Random rand = new Random();
        if(frontLine.size() > 0) {
            while (ret.size() < nMoves) {
                int i = rand.nextInt(frontLine.size());
                ret.add(new ArrayList<>());
                ret.get(ret.size() - 1).add(new PlaceArmiesMove(myName, frontLine.get(i), armies));
            }
        }

        return ret;
    }

    /* for each front line region, attack! */
    public ArrayList<AttackTransferMove> getAttackMoves() {
        ArrayList<AttackTransferMove> ret = new ArrayList<>();
        LinkedList<Region> frontLine = getFrontLine();

        for(Region region : frontLine) {
            int armies = region.getArmies();
            if (armies < 3) continue;
            for (Region neigh : region.getNeighbors()) {
                if(armies < 3) continue;
                if(!neigh.ownedByPlayer(myName)) {
                    int spendable = getSpendableUnits(neigh);
                    if(spendable > armies) continue;
                    ret.add(new AttackTransferMove(myName, region, neigh, spendable));
                }
            }
            if (armies > 3) {
                AttackTransferMove latest = ret.get(ret.size()-1);
                ret.add(new AttackTransferMove(myName, latest.getFromRegion(), latest.getToRegion(), latest.getArmies() + armies - 1));
                ret.remove(ret.size() - 2);
            }
        }

        return ret;
    }

    public ArrayList<AttackTransferMove> getTransferMoves() {
        ArrayList<AttackTransferMove> ret = new ArrayList<>();
        LinkedList<Region> frontLine = getFrontLine();
        for(Region region : state.getVisibleMap().getRegions()) {
            if(region.ownedByPlayer(myName)) {
                if(!frontLine.contains(region)) {
                    ret.add(new AttackTransferMove(myName, region, region.getNeighbors().getLast(), region.getArmies() - 1));
                }
            }
        }

        return ret;
    }

    /* executes a move to update state */
    public void doMovePlaceArmies(ArrayList<PlaceArmiesMove> pams) {
        for (PlaceArmiesMove pam : pams) {
            Region region = state.getVisibleMap().getRegion(pam.getRegion().getId());
            region.setArmies(region.getArmies() + pam.getArmies());
        }
    }

    /* executes a move to update state */
    public void doMoveAttackTransfer(ArrayList<AttackTransferMove> atms) {
        for (AttackTransferMove atm : atms) {
            Region from = state.getVisibleMap().getRegion(atm.getFromRegion().getId());
            Region to = state.getVisibleMap().getRegion(atm.getToRegion().getId());

            from.setArmies(from.getArmies() - atm.getArmies());

            if(to.ownedByPlayer(myName))
                to.setArmies((atm.getArmies() + to.getArmies()));
            else {
                to.setArmies(atm.getArmies() - to.getArmies()); // this is an assumption
                to.setPlayerName(myName);
            }
        }
    }
}
