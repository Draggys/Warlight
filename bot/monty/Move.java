package bot.monty;

import move.AttackTransferMove;
import move.PlaceArmiesMove;

import java.util.ArrayList;

public class Move {
    ArrayList<PlaceArmiesMove> pams;
    ArrayList<AttackTransferMove> atms;

    public Move() {
        pams = new ArrayList<>();
        atms = new ArrayList<>();
    }

    public Move(ArrayList<PlaceArmiesMove> pams, ArrayList<AttackTransferMove> atms) {
        this.pams = pams;
        this.atms = atms;
    }
}
