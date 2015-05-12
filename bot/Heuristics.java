package bot;

import main.Map;
import main.Region;
import main.SuperRegion;

import java.util.LinkedList;

public class Heuristics {
    private Region region;

    public Region getOffenseRegion() {
        return region;
    }

    /* Approximates state by counting armies on the front lines */
    public int frontLineState(BotState state, String player, boolean enemy) {
        // Perhaps some useful data
        Map fullMap = state.getFullMap();
        Map map = state.getVisibleMap();
        LinkedList<Region> regions = map.getRegions();
        LinkedList<SuperRegion> superRegions = map.getSuperRegions();

        // Count armies
        int armies = 0;
        for(Region region : regions) {
            if(!region.ownedByPlayer(player))
                continue;
            LinkedList<Region> neighbours = region.getNeighbors();
            for(Region neigh : neighbours) {
                if(!neigh.ownedByPlayer(player)) {
                    armies += region.getArmies();
                    break;
                }
            }
        }

        // Bonus armies
        int bonus = 10; // Todo: Find a good bonus value
        for(SuperRegion sregion : superRegions) {
            if (sregion.ownedByPlayer().equals(player)) {
                armies += bonus;
                armies += bonus;
            }
        }

        return enemy ? -armies : armies;
    }

    /* Count all visible armies */
    // Will have bias towards our due to fog of war
    public int countArmies(BotState state, String player, boolean enemy) {
        Map map = state.getVisibleMap();
        LinkedList<Region> regions = map.getRegions();
        LinkedList<SuperRegion> superRegions = map.getSuperRegions();

        // Count armies
        int armies = 0;
        for (Region region : regions) {
            if (region.ownedByPlayer(player)) {
                armies += region.getArmies();
            }
        }

        // Bonus armies
        int bonus = 10; // Todo: Find a good bonus value
        for(SuperRegion sregion : superRegions) {
            if (sregion.ownedByPlayer().equals(player)) {
                armies += bonus;
            }
        }

        return enemy ? -armies : armies;
    }
}
