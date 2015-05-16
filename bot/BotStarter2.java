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


import bot.log.Log;
import bot.monty.MCState;
import main.Region;
import move.AttackTransferMove;
import move.PlaceArmiesMove;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BotStarter2 implements Bot
{
    Log logger = new Log();

    @Override
    public ArrayList<Region> getPreferredStartingRegions(BotState state, Long timeOut)
    {
        ArrayList<Region> startRegions = state.getPickableStartingRegions();
        Collections.sort(startRegions, new RegionComparator());
        Collections.reverse(startRegions);
        List<Region> list = startRegions.subList(0, 6);
        ArrayList<Region> ret = new ArrayList<Region>();
        for(int i = 0; i < list.size(); i++)
            ret.add(list.get(i));
        return ret;
    }

    @Override
    public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(BotState state, Long timeOut) {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut)
    {
        return new ArrayList<>();
    }

    public static void main(String[] args)
    {
        BotParser parser = new BotParser(new BotStarter2());
        parser.run();
    }


}