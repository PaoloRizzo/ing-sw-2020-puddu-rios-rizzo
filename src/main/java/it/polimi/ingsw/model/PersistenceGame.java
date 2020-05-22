package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * abstracts from game, keeping some information and adding some missing info
 * objects of this class will contain the general info needed to save and load a game
 * the turn archive will instead provide precise details about the actions taken in the turns
 */
public class PersistenceGame {
    int numPlayers;
    int phase;
    List<Card> chosenCards;
    int numCardsChosen;
    int[] ids;
    String[] names;
    int[] numGods;

    /**
     * contructs a persistence game object from a game object, which is already used and has the
     * necessary connections to the relevant information
     * @param game the object from which to retrieve the relevant info
     */
    PersistenceGame(Game game){
        this.numPlayers = game.getNumPlayers();
        this.phase = 2;         //implement phase logic if persistence must be supported from setup phase instead of from turn phase
        this.chosenCards = game.getChosenCards();
        this.numCardsChosen = this.chosenCards.size();
        this.ids = new int[this.numPlayers];
        this.names = new String[this.numPlayers];
        this.numGods = new int[this.numPlayers];
        for(int i=0; i<this.numPlayers; i++){
            Player currPlayer = game.model.getPlayer(i);
            this.ids[i] = currPlayer.getId();
            this.names[i] = currPlayer.getNickname();
            this.numGods[i] = currPlayer.getCard().getNum();
        }
    }

    /**
     * saves the object to a json file in the proper location
     * @return a boolean value representing whether the save operation was successful
     */
    boolean save(){
        Gson gson = new Gson();
        String pathToGameJson = "src/main/resources/persistence/game.json";
        try{
            FileWriter writer = new FileWriter(pathToGameJson);
            gson.toJson(this, writer);
            writer.flush();
            writer.close();
        }
        catch(IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    static PersistenceGame load(){
        Gson gson = new Gson();
        String pathToGameJson = "src/main/resources/persistence/game.json";
        try{
            JsonReader reader = new JsonReader(new FileReader(pathToGameJson));
            PersistenceGame instance = gson.fromJson(reader, PersistenceGame.class);
            return instance;
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
}


