package bot.minimax;

import bot.BotState;
import main.Region;

public class State {
    public Region offenseRegion = null;
    public BotState state = null;

    public State(Region region, BotState state) {
        offenseRegion = region;
        this.state = state;
    }
}