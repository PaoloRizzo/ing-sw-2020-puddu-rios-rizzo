package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Space {

    private int posX, posY;
    private Worker worker;
    private List<Piece> pieces;
    private List<Space> adjacentSpaces;

    public Space(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        adjacentSpaces = new ArrayList<>();
        pieces = new ArrayList<>();
        addPiece(Piece.LEVEL0);
        worker = null;
    }

    void addAdjacentSpace(Space s){
        adjacentSpaces.add(s);
    }

    public List<Space> getAdjacentSpaces(){
        return adjacentSpaces;
    }

    public boolean hasWorkerOnIt(){
        return worker != null;
    }

    public Worker getWorkerOnIt(){
        return worker;
    }

    void setWorkerOnIt(Worker w){
        this.worker = w;
    }

    void removeWorkerOnIt(){
        this.worker = null;
    }

    void addPiece(Piece p){
        if(pieces.contains(p))
            throw new IllegalArgumentException("This space already contains" + p.toString());
        else if(UpperLevelInSpace(p))
            throw new IllegalArgumentException("Can't build over an upper level");
        else
            pieces.add(p);
    }

    private boolean UpperLevelInSpace(Piece p) {
        if (p != Piece.DOME && pieces.size() > 0)
        {
            for (Piece piece : Piece.values()) {
                if (piece.getLevel() > p.getLevel() && pieces.contains(piece))
                    return true;
            }
        }
        return false;
    }

    //to undo action
    void removeLastPiece(){
        if(pieces.size() == 1)
            throw new IllegalArgumentException("Can't undo removing piece level 0");
        pieces.remove(pieces.size()-1);
    }
    public Piece getLastPiece(){
        return pieces.get(pieces.size()-1);
    }

    public int getLevel(){
        Piece last = pieces.get(pieces.size()-1);
        return last.getLevel();
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public boolean isFreeSpace(){
        return (!this.hasWorkerOnIt()) && (this.getLevel() != 4);
    }

    public boolean isPeripheralSpace(){
        return posX == 0 || posX == 4 || posY == 0 || posY == 4;
    }

    protected Space lightClone(){
        return new Space(posX, posY);
    }
}
