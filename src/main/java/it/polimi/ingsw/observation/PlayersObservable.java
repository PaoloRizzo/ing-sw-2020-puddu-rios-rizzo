package it.polimi.ingsw.observation;

import it.polimi.ingsw.model.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayersObservable extends Observable<PlayersObserver>{

    public PlayersObservable(){
        super();
    }

    public synchronized void notifyStart(){
        System.out.println("starting client");
        for(PlayersObserver obs:observers){
            obs.updateStart();
        }
    }

    public synchronized void notifyID(int id){
        System.out.println("notifyID with id: "+id);
        for(PlayersObserver obs:observers){
            obs.updateID(id);
        }
    }

    public synchronized void notifyName(int id, String name){
        for(PlayersObserver obs:observers){
            obs.updateName(id, name);
        }
    }

    public synchronized void notifyOk(int id){
        for(PlayersObserver obs:observers){
            obs.updateOk(id);
        }
    }

    public synchronized void notifyKo(int id){
        for(PlayersObserver obs:observers){
            obs.updateKo(id);
        }
    }

    public synchronized void notifyAllPlayersConnected(){
        for(PlayersObserver obs:observers){
            obs.updateAllPlayersConnected();
        }
    };
}
