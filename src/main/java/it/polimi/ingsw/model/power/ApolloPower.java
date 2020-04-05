package it.polimi.ingsw.model.power;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;

public class ApolloPower extends PowerStrategy {

    protected ActionTree generateActionTree(Board board, Player player){
        ActionTree root = new ActionTree();
        this.addMoveLayer(root, player, board);
        super.addBuildLayer(root, player, board);
        return root;
    }


    protected void addMoveLayer(ActionTree curr, Player player, Board board){
        //inizialmente simulo le mosse
        if(!curr.isRoot()){
            //simulate
            board.executeAction(curr.getAction());
        }
        //parto dai nodi più in basso
        for(ActionTree child: curr.getChildren())
            addMoveLayer(child, player, board);

        if(curr.isAppendedLayer()){
            curr.setAppendedLayer(false);

            List<Worker> workers = new ArrayList<Worker>();

            if(curr.isRoot()) {
                //scelta libera del worker
                workers.add(player.getWorker(Sex.MALE));
                workers.add(player.getWorker(Sex.FEMALE));
            }else{
                String workerID = curr.getAction().getWorkerID();
                if(workerID.charAt(workerID.length()-1) == 'M')
                    workers.add(player.getWorker(Sex.MALE));
                else if(workerID.charAt((workerID.length()-1)) == 'F')
                    workers.add(player.getWorker(Sex.FEMALE));
            }
            boolean moved = false;
            for(Worker worker: workers){
                Space currSpace = worker.getSpace();
                for(Space adj: currSpace.getAdjacentSpaces()){
                    if(adj.getLevel()<=3 && (!adj.hasWorkerOnIt() || (adj.hasWorkerOnIt() && adj.getWorkerOnIt() != player.getWorker(Sex.MALE) && adj.getWorkerOnIt() != player.getWorker(Sex.FEMALE))) && adj.getLevel()<=currSpace.getLevel()+1){
                        boolean win = false, endOfTurn = false;
                        //winning condition
                        if(currSpace.getLevel() == 2 && adj.getLevel() == 3){
                            win = true;
                            endOfTurn = true;
                        }
                        Direction dir = Direction.compareTwoLevels(currSpace.getLevel(), adj.getLevel());
                        Action action;
                        if(adj.hasWorkerOnIt())
                            action = new MoveAndForceAction(worker.toString(), adj.getPosX(), adj.getPosY(), dir, currSpace.getPosX(), currSpace.getPosY(), adj.getWorkerOnIt().toString(), adj.getPosX(), adj.getPosY(), currSpace.getPosX(), currSpace.getPosY());
                        else
                            action = new MoveAction(worker.toString(), adj.getPosX(), adj.getPosY(), dir, currSpace.getPosX(), currSpace.getPosY());
                        moved = true;
                        curr.addChild(new ActionTree(action, win, false, endOfTurn, true));
                    }
                }
            }
            if(curr.isRoot() && !moved){
                //can't move with the workers
                curr.setLose(true);
                curr.setEndOfTurn(true);
            }
        }

        //undo simulazione azione
        if(!curr.isRoot()){
            board.undoExecuteAction(curr.getAction());
        }
    }
}
