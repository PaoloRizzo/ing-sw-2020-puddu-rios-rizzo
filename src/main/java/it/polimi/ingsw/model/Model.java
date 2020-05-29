package it.polimi.ingsw.model;

import it.polimi.ingsw.observation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

//TODO create singleton superclass
//TODO test whole class
//TODO refactor into 3 models, one for every feed
public class Model {
    public static Model instance;

    public Game game;
    HashMap<Integer, Player> players;
    //feeds
    public FeedObservable feed;
    public Board board;

    public Model(){
        board = new Board();
        players = new HashMap<>();
        feed = new FeedObservable();
        game = new Game(this);
    }

    //general methods

    FeedObservable getFeed(){
        return feed;
    }

    //TODO non esporre verso controller
    public HashMap<Integer, Player> getPlayers(){
        return players;
        /*
        HashMap<Integer, Player> copy = new HashMap<Integer, Player>();
        for(HashMap.Entry<Integer, Player> entry:players.entrySet()){
            copy.put(entry.getKey(), entry.getValue().lightClone());
        }
        return copy;

         */
    }

    //TODO non esporre verso controller
    public Player getPlayer(int id){
        /*
        return players.get(id).lightClone();

        */
        return players.get(id);
    }

    public void setNumPlayers(int numPlayers){
        game.setNumPlayers(numPlayers);
        feed.notifyNumPlayers(numPlayers);
    }

    //relies on the controller to only add valid players
    //adds the player in its right place in the ArrayList
    //publishes the notify for all known names so that the last client can know every other client
    //that has already posted their name, possibly even before the last one joined
    public void addPlayer(Player player){
        int id = player.getId();
        players.put(id, player);
        board.createPlayerWorkers(player);
        //important to notify every name to allow late clients to know all other players before
        feed.notifyName(player.getId(), player.getNickname());
    }

    //returns true if someone already claimed the nickname
    public boolean nicknamePresent(String name){
        boolean alreadyPresent = false;
        for(Player p: players.values()) {
            if (p.getNickname().equals(name))
                alreadyPresent = true;
        }
        return alreadyPresent;
    }

    //returns true if there is already a player with that id
    //if the get fails because it is impossible to get a player with that id
    //it catches the relative exception and returns false because
    //the fact that it is impossible to get it means that it is not present
    public boolean playerPresent(int id){
        return players.get(id) != null;
    }

    /**
     * saves the association between the id and the card
     * ADVANCES THE TURN
     * notifies the assiociation through the feed
     * @param id
     * @param numCard
     */
    public void setCardPlayer(int id, int numCard){
        Card card = game.getChosenCard(numCard);
        players.get(id).setCard(card);
        game.removeChosenCard(card);
        game.nextTurn();
        feed.notifyGod(id, card);
    }

    public int getNumPlayers(){
        return game.getNumPlayers();
    }

    public boolean numPlayersIsSet(){
        return game.numPlayersIsSet();
    }

    public void addObserver(FeedObserver obs){
        feed.addObserver(obs);
        feed.notifyStart();
    }

    public void removeObserver(FeedObserver obs){
        feed.removeObserver(obs);
    }

    /**
     * MAY ADVANCE THE TURN
     * updates the board with the setup action, adds it to the game, and if the setup turn is complete advances the turn
     * also notifies the action through the feed
     * @param id
     * @param setupAction
     */
    public void executeSetupAction(int id, SetupAction setupAction){
        board.executeAction(setupAction);
        game.addSetupAction(setupAction);
        if(players.get(id).getWorker(Sex.FEMALE).getSpace() != null && players.get(id).getWorker(Sex.MALE).getSpace() != null){
            game.nextTurn();
        }

        feed.notifyAction(id, setupAction);
    }

    /**
     * @param id of the player that owns the worker
     * @param s sex of the worker
     * @return true if worker of player id of sex s has been placed
     */
    public boolean workerIsPlaced(int id, Sex s){
        return getPlayers().get(id).getWorker(Sex.FEMALE).getSpace() != null;
    }

    /**
     *
     * @param id of the player that owns the worker
     * @param s sex of the worker
     * @param axis on which to get the position
     * @return position on the requested worker on the given axis
     */
    public int getWorkerPos(int id, Sex s, char axis){
        if(!workerIsPlaced(id, s))
            throw new IllegalArgumentException("the worker has not been placed yet");
        if(!(axis == 'x' || axis =='y'))
            throw new IllegalArgumentException("worker has no position on axis " + axis);
        if(axis == 'x')
            return players.get(id).getWorker(s).getSpace().getPosX();
        return players.get(id).getWorker(s).getSpace().getPosY();
    }

    public void executeAction(int id, Action action) {
        board.executeAction(action);
        game.addAction(action);
        feed.notifyAction(id, action);
    }

    /**
     * instantiates a PersistenceGame, and saves both that and the TurnArchive to a json file
     * the files that are saved have a name that is non ambiguous for any given set of players
     */
    public void save(){
        PersistenceGame pg = new PersistenceGame(game);
        TurnArchive ta = game.turnArchive;
        String saveName = saveName();
        System.out.println("saving game persistence files with name suffix:");
        System.out.println("\t" + saveName);
        pg.save(saveName);
        ta.save(saveName);
    }

    /**
     * retrieves the names of the players, and concatenates them to act a suffix for the file names
     * @return a concatenation of the names of the players, sorted alphabetically
     */
    String saveName(){
        List<String> names = new ArrayList<>();
        int numPlayers = game.getNumPlayers();
        for(int i=0;i<numPlayers;i++){
            names.add(players.get(i).getNickname());
        }
        Collections.sort(names);
        String saveName = "";
        for(String name:names){
            saveName += "_" + name;
        }
        return saveName;
    }

    /**
     * checks if there exist save files for the current players
     * @return true if there exist save files for the current players
     */
    public boolean isSaved(){
        String names = saveName();
        String gamePath = PersistenceGame.savePath(names);
        String turnsPath = TurnArchive.savePath(names);
        File gameFile = new File(gamePath);
        File turnsFile = new File(turnsPath);
        return (gameFile.exists() && turnsFile.exists());
    }

    /**
     * deep comparison that sets off comparisons of all the objects that should be restored
     * after loading a previous game
     * @param that the other Model
     * @return true if the two models are functionally indistinguishable
     */
    public boolean fullEquals(Model that){
        boolean equality = true;
        equality &= this.game.fullEquals(that.game);
        equality &= this.players.keySet().equals(that.players.keySet());
        if(!equality)
            return equality;
        for(int key:players.keySet())
            equality &= this.players.get(key).fullEquals(that.players.get(key));
        equality &= this.feed != null && that.feed != null;
        equality &= this.board.fullEquals(that.board);
        return equality;
    }
}
