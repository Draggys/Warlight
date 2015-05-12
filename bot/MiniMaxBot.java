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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;

import bot.minimax.MiniMax;
import main.Region;
import move.AttackTransferMove;
import move.PlaceArmiesMove;

public class MiniMaxBot implements Bot
{
    private MiniMax minimax = new MiniMax();
    @Override
    /**
     * A method used at the start of the game to decide which player start with what Regions. 6 Regions are required to be returned.
     * This example randomly picks 6 regions from the pickable starting Regions given by the engine.
     * @return : a list of m (m=6) Regions starting with the most preferred Region and ending with the least preferred Region to start with
     */
    public ArrayList<Region> getPreferredStartingRegions(BotState state, Long timeOut)
    {
        ArrayList<Region> startRegions = state.getPickableStartingRegions();
        Collections.sort(startRegions, new RegionComparator());
        return (ArrayList<Region>) startRegions.subList(0, 6);
    }

    @Override
    /**
     * This method is called for at first part of each round. This example puts two armies on random regions
     * until he has no more armies left to place.
     * @return The list of PlaceArmiesMoves for one round
     */
    public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(BotState state, Long timeOut)
    {
        ArrayList<PlaceArmiesMove> placeArmiesMoves = new ArrayList<PlaceArmiesMove>();
        minimax.minimax(state, 1000, state.getMyPlayerName(), state.getOpponentPlayerName(), false);

        placeArmiesMoves.add(new PlaceArmiesMove(state.getMyPlayerName(), minimax.bestState.offenseRegion, state.getStartingArmies()));
        return placeArmiesMoves;

/*
        ArrayList<PlaceArmiesMove> placeArmiesMoves = new ArrayList<PlaceArmiesMove>();
        String myName = state.getMyPlayerName();
        int armies = 2;
        int armiesLeft = state.getStartingArmies();
        LinkedList<Region> visibleRegions = state.getVisibleMap().getRegions();

        while(armiesLeft > 0)
        {
            double rand = Math.random();
            int r = (int) (rand*visibleRegions.size());
            Region region = visibleRegions.get(r);

            if(region.ownedByPlayer(myName))
            {
                placeArmiesMoves.add(new PlaceArmiesMove(myName, region, armies));
                armiesLeft -= armies;
            }
        }

        return placeArmiesMoves;
*/
    }

    @Override
    /**
     * This method is called for at the second part of each round. This example attacks if a region has
     * more than 6 armies on it, and transfers if it has less than 6 and a neighboring owned region.
     * @return The list of PlaceArmiesMoves for one round
     */
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut)
    {
        ArrayList<AttackTransferMove> attackTransferMoves = new ArrayList<AttackTransferMove>();
        Region offenseRegion = minimax.bestState.offenseRegion;
        LinkedList<Region> neighbours = offenseRegion.getNeighbors();
        for (int i = 0; i < neighbours.size(); i++){
            if (offenseRegion.getArmies() < 3)
                break;
            if (neighbours.get(i).ownedByPlayer(state.getMyPlayerName()))
                continue;

            int armiesToSpend = minimax.armiesNeeded(offenseRegion, neighbours.get(i));
            offenseRegion.setArmies(offenseRegion.getArmies() - armiesToSpend);

            attackTransferMoves.add(new AttackTransferMove(state.getMyPlayerName(), offenseRegion, neighbours.get(i), armiesToSpend));
        }
        return attackTransferMoves;
        /*
        ArrayList<AttackTransferMove> attackTransferMoves = new ArrayList<AttackTransferMove>();
        String myName = state.getMyPlayerName();
        int armies = 5;

        for(Region fromRegion : state.getVisibleMap().getRegions())
        {
            if(fromRegion.ownedByPlayer(myName)) //do an attack
            {
                ArrayList<Region> possibleToRegions = new ArrayList<Region>();
                possibleToRegions.addAll(fromRegion.getNeighbors());

                while(!possibleToRegions.isEmpty())
                {
                    double rand = Math.random();
                    int r = (int) (rand*possibleToRegions.size());
                    Region toRegion = possibleToRegions.get(r);

                    if(!toRegion.getPlayerName().equals(myName) && fromRegion.getArmies() > 6) //do an attack
                    {
                        attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, toRegion, armies));
                        break;
                    }
                    else if(toRegion.getPlayerName().equals(myName) && fromRegion.getArmies() > 1) //do a transfer
                    {
                        attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, toRegion, armies));
                        break;
                    }
                    else
                        possibleToRegions.remove(toRegion);
                }
            }
        }
        return attackTransferMoves;
        */
    }

    public static void main(String[] args)
    {
        BotParser parser = new BotParser(new BotStarter());
        parser.run();
    }


}
