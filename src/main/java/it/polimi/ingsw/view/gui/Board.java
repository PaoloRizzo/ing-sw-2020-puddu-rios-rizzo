package it.polimi.ingsw.view.gui;


import it.polimi.ingsw.model.*;
import it.polimi.ingsw.view.ClientView;
import it.polimi.ingsw.view.UserInterface;
import it.polimi.ingsw.view.cli.CliUtils;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;

import java.util.HashMap;
import java.util.List;

import it.polimi.ingsw.model.Color;
public class Board extends Group implements UserInterface {
    private Tower [][] towers = new Tower[5][5];
    private HashMap<Integer, Player> players;
    ActionFSM actionFSM;
    SelectTypeActionMenu selectTypeActionMenu;
    SelectPieceMenu selectPieceMenu;
    EndOfTurnMenu endOfTurnMenu;
    AskNumPlayersMenu askNumPlayersMenu;
    AskNameMenu askNameMenu;
    AskCardMenu askCardMenu;
    AskGodMenu askGodMenu;
    Group group2d, group3d;
    PieceBag pieceBag;

    ClientView clientView;
    public Board(ClientView cw) {
        clientView = cw;
        players = new HashMap<>();

        group2d = new Group();
        group3d = new Group3D();

        this.getChildren().addAll(group2d, group3d);

        prepareBoard();

        actionFSM = new ActionFSM();
        for(int i=0;i<5;i++){
            for(int j=0;j<5;j++){
                towers[i][j] = new Tower(i, j, new Point3D(i*100-200, 0, j*100-200));
                group3d.getChildren().add(towers[i][j]);
                towers[i][j].setOnMouseClicked(actionFSM);
                towers[i][j].setOnMouseEntered(actionFSM);
                towers[i][j].setOnMouseExited(actionFSM);
            }
        }

        askNameMenu = new AskNameMenu(cw);
        group2d.getChildren().add(askNameMenu);

        askNumPlayersMenu = new AskNumPlayersMenu(cw);
        group2d.getChildren().add(askNumPlayersMenu);

        askCardMenu = new AskCardMenu(cw);
        group2d.getChildren().add(askCardMenu);

        askGodMenu = new AskGodMenu(cw);
        group2d.getChildren().add(askGodMenu);

        selectTypeActionMenu = new SelectTypeActionMenu(actionFSM);
        group2d.getChildren().add(selectTypeActionMenu);

        selectPieceMenu = new SelectPieceMenu(actionFSM);
        group2d.getChildren().add(selectPieceMenu);

        endOfTurnMenu = new EndOfTurnMenu(actionFSM);
        group2d.getChildren().add(endOfTurnMenu);

        pieceBag = new PieceBag();
    }

    public synchronized void executeAction(Action action){
        if(action instanceof SetupAction)
            this.executeAction((SetupAction)action);
        else if(action instanceof MoveAndForceAction)
            this.executeAction((MoveAndForceAction)action);
        else if(action instanceof MoveAction)
            this.executeAction((MoveAction)action);
        else if(action instanceof BuildAction)
            this.executeAction((BuildAction)action);
        else
            throw new IllegalArgumentException("Can't execute a normal action!");
    }

    public void executeAction(SetupAction action){
        Color color = players.get(action.getWorkerID().charAt(1)-'0').getColor();
        Worker worker = new Worker(action.getWorkerID(), color.toString());
        //aggiungo worker
        towers[action.getTargetX()][action.getTargetY()].setWorker(worker);
    }
    public void executeAction(MoveAction action){
        //salvo worker
        Worker worker = towers[action.getStartX()][action.getStartY()].getWorker();
        //tolgo worker
        towers[action.getStartX()][action.getStartY()].removeWorker();
        //aggiungo worker
        towers[action.getTargetX()][action.getTargetY()].setWorker(worker);
    }
    public void executeAction(MoveAndForceAction action){
        //salvo workers
        Worker worker = towers[action.getStartX()][action.getStartY()].getWorker();
        Worker forcedWorker = towers[action.getForcedStartX()][action.getForcedStartY()].getWorker();
        //tolgo workers
        towers[action.getStartX()][action.getStartY()].removeWorker();
        towers[action.getForcedStartX()][((MoveAndForceAction) action).getForcedStartY()].removeWorker();
        //aggiungo workers
        towers[action.getTargetX()][action.getTargetY()].setWorker(worker);
        towers[action.getForcedTargetX()][action.getForcedTargetY()].setWorker(forcedWorker);
    }
    public void executeAction(BuildAction action){
        pieceBag.pickPiece(action.getPiece());
        towers[action.getTargetX()][action.getTargetY()].addBuilding(new Building(action.getPiece()));
    }

    public void previewAction(Action action){
        if(action instanceof SetupAction)
            this.previewAction((SetupAction)action);
        else if(action instanceof MoveAndForceAction)
            this.previewAction((MoveAndForceAction)action);
        else if(action instanceof MoveAction)
            this.previewAction((MoveAction)action);
        else if(action instanceof BuildAction)
            this.previewAction((BuildAction)action);
        else
            throw new IllegalArgumentException("Can't execute a normal action!");
    }

    public void previewAction(SetupAction action){
        executeAction(action);
        towers[action.getTargetX()][action.getTargetY()].getWorker().setToPreview();
    }
    public void previewAction(MoveAction action){
        executeAction(action);
        towers[action.getTargetX()][action.getTargetY()].getWorker().setToPreview();
    }
    public void previewAction(MoveAndForceAction action){
        executeAction(action);
        towers[action.getTargetX()][action.getTargetY()].getWorker().setToPreview();
        towers[action.getForcedTargetX()][action.getForcedTargetY()].getWorker().setToDefaultView();
    }
    public void previewAction(BuildAction action){
        executeAction(action);
        towers[action.getTargetX()][action.getTargetY()].getLastBuilding().setToPreview();
    }


    public void undoExecuteAction(Action action){
        if(action instanceof SetupAction)
            this.undoExecuteAction((SetupAction)action);
        else if(action instanceof MoveAndForceAction)
            this.undoExecuteAction((MoveAndForceAction)action);
        else if(action instanceof MoveAction)
            this.undoExecuteAction((MoveAction)action);
        else if(action instanceof BuildAction)
            this.undoExecuteAction((BuildAction)action);
        else
            throw new IllegalArgumentException("Can't undo a normal action!");
    }
    public void undoExecuteAction(SetupAction action){
        towers[action.getTargetX()][action.getTargetY()].removeWorker();
    }
    public void undoExecuteAction(MoveAction action){
        //salvo worker
        Worker w = towers[action.getTargetX()][action.getTargetY()].getWorker();
        //tolgo worker
        towers[action.getTargetX()][action.getTargetY()].removeWorker();
        //aggiungo worker
        towers[action.getStartX()][action.getStartY()].setWorker(w);
    }
    public void undoExecuteAction(MoveAndForceAction action){
        //salvo workers
        Worker worker = towers[action.getTargetX()][action.getTargetY()].getWorker();
        Worker forcedWorker = towers[action.getForcedTargetX()][action.getForcedTargetY()].getWorker();
        //tolgo workers
        towers[action.getTargetX()][action.getTargetY()].removeWorker();
        towers[action.getForcedTargetX()][action.getForcedTargetY()].removeWorker();
        //aggiungo workers
        towers[action.getStartX()][action.getStartY()].setWorker(worker);
        towers[action.getForcedStartX()][action.getForcedStartY()].setWorker(forcedWorker);
    }
    public void undoExecuteAction(BuildAction action){
        pieceBag.undoPickPiece(action.getPiece());
        towers[action.getTargetX()][action.getTargetY()].removeLastBuilding();
    }

    public void undoPreviewAction(Action action){
        if(action instanceof SetupAction)
            this.undoPreviewAction((SetupAction)action);
        else if(action instanceof MoveAndForceAction)
            this.undoPreviewAction((MoveAndForceAction)action);
        else if(action instanceof MoveAction)
            this.undoPreviewAction((MoveAction)action);
        else if(action instanceof BuildAction)
            this.undoPreviewAction((BuildAction)action);
        else
            throw new IllegalArgumentException("Can't undo a normal action!");
    }
    public void undoPreviewAction(SetupAction action){
        undoExecuteAction(action);
    }
    public void undoPreviewAction(MoveAction action){
        undoExecuteAction(action);
        towers[action.getStartX()][action.getStartY()].setToDefaultView();
    }
    public void undoPreviewAction(MoveAndForceAction action){
        undoExecuteAction(action);
        towers[action.getStartX()][action.getStartY()].setToDefaultView();
        towers[action.getForcedStartX()][action.getForcedStartY()].setToEnableView();
    }
    public void undoPreviewAction(BuildAction action){
        undoExecuteAction(action);
    }


    public void showSelectTypeActionMenu() {
        selectTypeActionMenu.show();
    }
    public void hideSelectTypeActionMenu() {
        selectTypeActionMenu.hide();
    }
    public void showSelectPieceMenu() {
        selectPieceMenu.show();
    }
    public void hideSelectPieceMenu() {
        selectPieceMenu.hide();
    }
    public void showEndOfTurnMenu() { endOfTurnMenu.show(); }
    public void hideEndOfTurnMenu() {
        endOfTurnMenu.hide();
    }
    public void showAskNumPlayersMenu() {
        askNumPlayersMenu.show();
    }
    public void hideAskNumPlayersMenu() {
        askNumPlayersMenu.hide();
    }
    public void showAskNameMenu() {
        askNameMenu.show();
    }
    public void hideAskNameMenu() {
        askNameMenu.hide();
    }
    public void showAskCardMenu() {
        askCardMenu.show();
    }
    public void hideAskCardMenu() {
        askCardMenu.hide();
    }
    public void showAskGodMenu() {
        askGodMenu.show();
    }
    public void hideAskGodMenu() {
        askGodMenu.hide();
    }

    public Tower getTower(int targetX, int targetY) {
        return towers[targetX][targetY];
    }

    public void setAllToDefaultView(){
        for(int i=0;i<5;i++){
            for(int j=0;j<5;j++){
                towers[i][j].setToDefaultView();
            }
        }
    }

    private void prepareBoard() {
        MeshView island = new MeshView(GraphicsLoader.instance().getMesh("ISLAND"));
        MeshView islands = new MeshView(GraphicsLoader.instance().getMesh("ISLANDS"));

        PhongMaterial islandTexture = GraphicsLoader.instance().getTexture("ISLAND");

        island.setMaterial(islandTexture);
        islands.setMaterial(islandTexture);

        MeshView seaUp = new MeshView(GraphicsLoader.instance().getMesh("SEA"));
        MeshView seaDown = new MeshView(GraphicsLoader.instance().getMesh("SEADOWN"));

        PhongMaterial seaUpTexture = GraphicsLoader.instance().getTexture("SEA");
        seaUp.setMaterial(seaUpTexture);
        PhongMaterial seaDownTexture = new PhongMaterial();
        seaDownTexture.setDiffuseColor(new javafx.scene.paint.Color((float)59/255, (float)191/255, (float)241/255, 1));
        seaDown.setMaterial(seaDownTexture);

        MeshView innerWall = new MeshView((GraphicsLoader.instance().getMesh("INNERWALL")));
        MeshView outerWall = new MeshView((GraphicsLoader.instance().getMesh("OUTERWALL")));
        group3d.getChildren().addAll(island, islands, innerWall, seaUp, seaDown, outerWall);
    }

    @Override
    public void showLogo() {

    }

    @Override
    public void askNumPlayers() {
        showAskNumPlayersMenu();
        System.out.println("Ask num player");
    }

    @Override
    public void askUsername() {
        showAskNameMenu();
        System.out.println("Ask name");
    }



    @Override
    public void askCard(Deck deck) {
        askCardMenu.setDeck(deck);
        showAskCardMenu();

    }
    @Override
    public void askGod(List<Card> cards) {
        askGodMenu.setCards(cards);
        showAskGodMenu();

    }
    @Override
    public void askSetupWorker(List<Action> possibleActions) {
        actionFSM.setPossibleActions(this, possibleActions, false);
    }
    @Override
    public void askAction(List<Action> possibleActions, boolean canEndOfTurn){
        actionFSM.setPossibleActions(this, possibleActions, canEndOfTurn);
    }
    @Override
    public void removeWorkersOfPlayer(int id){
        for(int i=0;i<5;i++){
            for(int j=0;j<5;j++){
                if(towers[i][j].hasWorker() && (towers[i][j].getWorker().getWorkerId().charAt(1)-'0' == id)){
                    towers[i][j].removeWorker();
                }
            }
        }
    }

    @Override
    public void registerPlayer(int id, String name) {
        players.put(id, new Player(id, name));
    }
    @Override
    public void registerGod(int id, Card card){
        players.get(id).setCard(card);
    }

    @Override
    public int getNumPlayersRegister() {
        return players.size();
    }
}