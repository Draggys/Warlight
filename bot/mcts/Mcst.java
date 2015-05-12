package bot.mcts;

public class Mcst {
    int itermax;

    public Mcst(int itermax){
        this.itermax = itermax;
    }

    public MCState search(MCState root) {
        double bestVal = Integer.MIN_VALUE;
        TreeNode bestState = null;
        for (int i = 0; i < itermax; i++) {
            TreeNode state = new TreeNode(new MCState(root));

            state.selectAction();
            if (state.totValue > bestVal) {
                bestVal = state.totValue;
                bestState = state;
            }
        }

        return bestState.select().state;
    }
}
