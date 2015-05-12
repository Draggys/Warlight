package bot.minimax;

import bot.RegionComparator;
import main.Region;
import main.SuperRegion;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Made to test functions
 */
public class TestingGround {
    public static void main(String[] args) {
        // Check that RegionComparator works, should be in descending order
        TestingGround T = new TestingGround();
        ArrayList<Region> regions = T.testStartRegions();
        int prev = regions.get(0).getSuperRegion().getArmiesReward();
        for (int i = 1; i < regions.size(); i++) {
            int curr = regions.get(i).getSuperRegion().getArmiesReward();
            if (curr > prev)
                throw new IllegalStateException();
            prev = curr;
        }
    }

    private ArrayList<Region> testStartRegions() {
        SuperRegion northAmerica = new SuperRegion(1, 5);
        SuperRegion southAmerica = new SuperRegion(2, 2);

        Region region1 = new Region(1, northAmerica);
        Region region2 = new Region(2, southAmerica);

        ArrayList<Region> regions = new ArrayList<Region> ();
        regions.add(region2);
        regions.add(region1);
        regions.add(region2);

        Collections.sort(regions, new RegionComparator());
        return regions;
    }
}
