package bot;

import java.util.Comparator;

import main.Region;

public class RegionComparator implements Comparator<Region>{
    @Override
    public int compare(Region lhs, Region rhs) {
        int ret = Integer.compare(lhs.getSuperRegion().getArmiesReward(), rhs.getSuperRegion().getArmiesReward());
        if (ret == 1)
            return -1;
        else if (ret == 0)
            return 0;
        else
            return 1;
    }
}
