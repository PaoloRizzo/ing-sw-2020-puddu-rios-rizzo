package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.ControllerInterface;
import it.polimi.ingsw.exception.IncorrectStateException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.observation.GameObservable;
import it.polimi.ingsw.observation.PlayersObservable;
import it.polimi.ingsw.observation.UserInterfaceObserver;
import it.polimi.ingsw.view.middleware.Client;
import it.polimi.ingsw.view.cli.Cli;


import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

//TODO test basically everything in this class
public class ClientView extends View implements UserInterfaceObserver
{
    //finite state machine states
    ConnectionState currentConnectionState;
    SetupState currentSetupState;
    GameState currentGameState;
    //updates will modify these objects
    Game game;
    private int id;
    private UserInterface ui;

    //private because ClientView is singleton. instance() should be called to get an object of this type
    public ClientView(ControllerInterface controller){
        super(controller);
        game = null;
        id = -1;
    }

    public void setUi(UserInterface ui){
        this.ui = ui;
    }

    public UserInterface getUi(){
        return ui;
    }

    public int getId(){
        return id;
    }

    public void setNumPlayers(int numPlayers){
        game = new Game(numPlayers);
    }

    //TODO should not be accepted if ID is already set, meaning it is still -1
    public void setID(int id){
        this.id = id;
    }

    //updates relative to UserInterface
    public synchronized void updateReadNumPlayers(int numPlayers){
        if(currentConnectionState.equals(ConnectionState.READ_NUM_PLAYERS))
            currentConnectionState.execute(this, numPlayers);
    }
    public synchronized void updateReadName(String name){
        if(currentConnectionState.equals(ConnectionState.READ_NAME))
            currentConnectionState.execute(this, name);
    }
    public synchronized void updateReadNumCard(int numCard) {
        if(currentSetupState.equals(SetupState.READ_CARD))
            currentSetupState.execute(this, numCard);
    }
    public synchronized void updateReadGod(int numCard) {
        if(currentSetupState.equals(SetupState.READ_GOD))
            currentSetupState.execute(this, numCard);
    }

    public synchronized void updateReadAction(Action action) {
        if(currentSetupState.equals(SetupState.READ_SETUP_WORKER))
            currentSetupState.execute(this, action);
        if(currentGameState!= null && currentGameState.equals(GameState.READ_ACTION))
            currentGameState.execute(this, action);
    }

    public synchronized void updateReadVoluntaryEndOfTurn() {
        if(currentGameState!= null && currentGameState.equals(GameState.READ_ACTION)){
            currentGameState = GameState.PUBLISH_VOLUNTARY_END_OF_TURN;
            currentGameState.execute(this, null);
        }
    }
    //updates relative to GameObserver

    public synchronized void updateNumPlayers(int numPlayers){
        System.out.println("received number of players: " + numPlayers);
        if(game != null)
            System.out.println("it was already known, state: "+currentConnectionState.toString());
        if(currentConnectionState.equals(ConnectionState.RECEIVE_NUM_PLAYERS))
            currentConnectionState.execute(this, numPlayers);
    }
    public synchronized void updateCurrentPlayer(int id, List<Action> possibleActions, boolean canEndOfTurn) {
        System.out.println("currplayer "+id);
        if(id == getId() && currentSetupState.equals(SetupState.ASK_SETUP_WORKER))
            currentSetupState.execute(this, possibleActions);
        if(id == getId() && currentGameState!=null && currentGameState.equals(GameState.RECEIVE_ACTIONS)){
            if(canEndOfTurn){
                currentGameState = GameState.ASK_OPTIONAL_ACTION;
                System.out.println("Posso terminare il turno, invece di fare la mossa");
            }
            currentGameState.execute(this, possibleActions);
        }

    }

    public void updateEndOfTurnPlayer(int id){
        if(id == getId() && currentGameState!=null && currentGameState.equals(GameState.RECEIVE_ACTIONS)){
            //torno in attesa del mio turno
            currentGameState = GameState.REQUEST_ACTIONS;
            currentGameState.execute(this, null);
        }
    }

    public synchronized void updateAction(int id, Action action){
        getUi().executeAction(action);
        System.out.println("Execute action: "+action.toString());
    }

    //updates relative to PlayersObserver

    public synchronized void updateID(int id){

        /*
        if(this.id != -1)
            System.out.println("ClientView.updateID with id "+id+", but was already "+this.id);
        else
            System.out.println("ClientView.updateID with new id "+id);

         */
        if(currentConnectionState.equals(ConnectionState.RECEIVE_ID))
            currentConnectionState.execute(this, id);
    }

    public synchronized void updateAllPlayersConnected(){
        if(currentConnectionState.equals(ConnectionState.RECEIVE_ALL_PLAYERS_CONNECTED))
            currentConnectionState.execute(this, null);
    }

    public synchronized void updateStart(){
        //System.out.println("starting this client");
        if(currentConnectionState.equals(ConnectionState.READY))
            currentConnectionState.execute(this, null);
    }

    //this method supposes that only valid names are received
    public synchronized void updateName(int id, String name){
        getUi().registerPlayer(id, name);
        if(id == this.id){
            System.out.println("my name is " + name);
        }else
            System.out.println("player " + id + " chose " + name + " as their username");
        System.out.println("Player register "+getUi().getNumPlayersRegister());
        if(getUi().getNumPlayersRegister() == game.getNumPlayers() && currentConnectionState == ConnectionState.WAIT_ALL_PLAYERS_NAME){
            currentConnectionState = ConnectionState.END;
            currentConnectionState.execute(this, null);
        }
    }

    public synchronized void updateDeck(Deck deck) {
        if(currentSetupState == SetupState.RECEIVE_DECK){
            currentSetupState.execute(this, deck);
        }
    }


    public synchronized void updateCards(int id, List<Card> cards) {
        if(id == getId() && currentSetupState == SetupState.RECEIVE_CARDS){
            currentSetupState.execute(this, cards);
        }
    }

    public synchronized void updateGod(int id, Card card){
        getUi().registerGod(id, card);
    }

    public synchronized void updatePlayerWin(int id){
        if(id == getId()){
            currentGameState = GameState.WIN_STATE;
        }else{
            currentGameState = GameState.LOSE_STATE;
        }
        currentGameState.execute(this, null);
    }
    public synchronized void updatePlayerLose(int id){
        if(id == getId()){
            currentGameState = GameState.LOSE_STATE;
            currentGameState.execute(this, null);
        }
        getUi().removeWorkersOfPlayer(id);
    }
    public synchronized void updateOk(int id) {
        if(id == this.id){
            if(currentConnectionState.equals(ConnectionState.RECEIVE_CHECK)){
                currentConnectionState.execute(this, true);
            }else if(currentSetupState!=null && currentSetupState.equals(SetupState.RECEIVE_CHECK)){
                currentSetupState.execute(this, true);
            }else{
                throw new IncorrectStateException("Received an okay for some communication, but was not waiting for it. I am in state " + currentConnectionState.name());
            }
        }
    }

    public synchronized void updateKo(int id){
        if(id == this.id){
            if(currentConnectionState.equals(ConnectionState.RECEIVE_CHECK)){
                currentConnectionState.execute(this, false);
            }
            else{
                throw new IncorrectStateException("Received a ko for some communication, but was not waiting for it. I am in state " + currentConnectionState.name());
            }
        }
    }

    //sets up the first state for the connection FSM and does NOT execute it, since it will be awakened by
    //an update
    private void startConnectionFSM(){
        currentConnectionState = ConnectionState.READY;
    }
    public void startSetupFSM() {
        currentSetupState = SetupState.START_SETUP;
        currentSetupState.execute(this, null);
    }
    public void startGameFSM() {
        currentGameState = GameState.START_GAME;
        currentGameState.execute(this, null);
    }
    //start the first FSM
    public void start(){
        ui.showLogo();
        startConnectionFSM();
    }


}
