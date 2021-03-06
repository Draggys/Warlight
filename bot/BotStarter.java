// Copyright 2014 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//	
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package bot;

import bot.mcts.MCState;
import bot.mcts.Mcst;
import main.Region;
import move.AttackTransferMove;
import move.PlaceArmiesMove;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BotStarter implements Bot
{
    private MCState nextState = null;

    @Override
    public ArrayList<Region> getPreferredStartingRegions(BotState state, Long timeOut)
    {
        ArrayList<Region> startRegions = state.getPickableStartingRegions();
        Collections.sort(startRegions, new RegionComparator());
        List<Region> list = startRegions.subList(0, 6);
        ArrayList<Region> ret = new ArrayList<Region>();
        for(int i = 0; i < list.size(); i++)
            ret.add(list.get(i));
        return ret;
    }

    @Override
    public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(BotState state, Long timeOut) {
        Mcst mcst = new Mcst(1);
        nextState = mcst.search(new MCState(state));

        return nextState.getPlaceArmiesFrontLine();
        //return nextState.getPlaceArmies();
    }

    @Override
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut)
    {
        return nextState.getAttackTransferFrontLine();
        //return nextState.getAttackTransfer();
    }

    public static void main(String[] args)
    {
        BotParser parser = new BotParser(new BotStarter());
        parser.run();
    }


}
