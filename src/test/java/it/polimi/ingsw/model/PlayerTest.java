package it.polimi.ingsw.model;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PlayerTest
{
    @Test
    public void checkWorkers()
    {
        Player p = new Player("name", Color.BLUE, 1);
        assertNotNull(p.getWorker(Sex.MALE));
        assertNotNull(p.getWorker(Sex.FEMALE));
    }

    @Test
    public void checkNickname()
    {
        Player p = new Player("name", Color.BLUE, 1);
        assertEquals(p.getNickname(), "name");
    }
}