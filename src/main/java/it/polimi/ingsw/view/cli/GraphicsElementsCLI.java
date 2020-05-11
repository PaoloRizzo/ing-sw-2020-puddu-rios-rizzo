package it.polimi.ingsw.view.cli;

/**
 * Encapsulates all the loadings of textures for the CLI.
 */
class GraphicsElementsCLI
{
    /**
     * Sets up graphics for a determinate level of a building.
     * @param figure the RectangleCLI instance to set the texture on.
     * @param level the requested level to draw.
     */
    public static void drawLevel(RectangleCLI figure, int level)
    {
        switch (level) {
            case 0:
                figure.setPalette(AnsiColors.ANSI_BG_GREEN);
                break;
            case 1:
                figure.setPalette(AnsiColors.ANSI_BG_BLACK);
                break;
            case 2:
                figure.setMask("./src/main/resources/level2.txt");
                figure.setPalette(AnsiColors.ANSI_BG_BLACK, AnsiColors.ANSI_BG_WHITE);
                break;
            case 3:
                figure.setMask("./src/main/resources/level3.txt");
                figure.setPalette(AnsiColors.ANSI_BG_BLACK, AnsiColors.ANSI_BRIGHT_BG_BLACK, AnsiColors.ANSI_BG_WHITE);
                break;
            case 4:
                figure.setMask("./src/main/resources/level3.txt");
                figure.setPalette(AnsiColors.ANSI_BG_BLUE, AnsiColors.ANSI_BRIGHT_BG_BLACK, AnsiColors.ANSI_BG_WHITE);
                break;
            default:
                figure.setPalette(AnsiColors.ANSI_BG_RED);
                break;
        }
    }

    /**
     * Sets up graphics for the picture of a God, to be inserted in a card.
     * @param pic the RectangleCLI instance to set the texture on.
     * @param title the name of the god on the card.
     */
    public static void drawGod(RectangleCLI pic, String title)
    {
        switch(title)
        {
            case "PROMETHEUS":
                pic.setMask("./src/main/resources/prometheus.txt");
                pic.setPalette(AnsiColors.ANSI_BG_BLACK, AnsiColors.ANSI_BRIGHT_BG_RED, AnsiColors.ANSI_BG_RED, AnsiColors.ANSI_BRIGHT_BG_YELLOW, AnsiColors.ANSI_BG_YELLOW, AnsiColors.ANSI_RESET, AnsiColors.ANSI_BRIGHT_BG_BLACK);
                break;
            case "ATLAS":
                pic.setMask("./src/main/resources/atlas.txt");
                pic.setPalette(AnsiColors.ANSI_BRIGHT_BG_CYAN, AnsiColors.ANSI_BG_WHITE, AnsiColors.ANSI_RESET, AnsiColors.ANSI_BRIGHT_BG_GREEN, AnsiColors.ANSI_BG_GREEN, AnsiColors.ANSI_BRIGHT_BG_BLUE, AnsiColors.ANSI_BG_BLUE);
                break;
            case "ATHENA":
                pic.setMask("./src/main/resources/athena.txt");
                pic.setPalette(AnsiColors.ANSI_BRIGHT_BG_BLUE, AnsiColors.ANSI_BRIGHT_BG_RED, AnsiColors.ANSI_BG_RED, AnsiColors.ANSI_BRIGHT_BG_YELLOW, AnsiColors.ANSI_BG_YELLOW, AnsiColors.ANSI_BG_BLACK, AnsiColors.ANSI_BRIGHT_BG_BLACK, AnsiColors.ANSI_RESET);
                break;
            case "DEMETER":
                pic.setMask("./src/main/resources/demeter.txt");
                pic.setPalette(AnsiColors.ANSI_BG_BLACK, AnsiColors.ANSI_RESET, AnsiColors.ANSI_BRIGHT_BG_RED, AnsiColors.ANSI_BG_RED, AnsiColors.ANSI_BRIGHT_BG_BLUE, AnsiColors.ANSI_BG_BLUE, AnsiColors.ANSI_BRIGHT_BG_BLACK, AnsiColors.ANSI_BG_YELLOW);
                break;
            case "MINOTAUR":
                pic.setMask("./src/main/resources/minotaur.txt");
                pic.setPalette(AnsiColors.ANSI_BG_BLACK, AnsiColors.ANSI_BRIGHT_BG_BLACK, AnsiColors.ANSI_BG_RED, AnsiColors.ANSI_RESET);
                break;
            case "ZEUS":
                pic.setMask("./src/main/resources/zeus.txt");
                pic.setPalette(AnsiColors.ANSI_BRIGHT_BG_BLUE, AnsiColors.ANSI_BG_BLUE, AnsiColors.ANSI_BRIGHT_BG_YELLOW, AnsiColors.ANSI_BG_YELLOW, AnsiColors.ANSI_RESET, AnsiColors.ANSI_BG_WHITE);
                break;
            case "HEPHAESTUS":
                pic.setMask("./src/main/resources/efesto.txt");
                pic.setPalette(AnsiColors.ANSI_BG_RED, AnsiColors.ANSI_BRIGHT_BG_RED, AnsiColors.ANSI_RESET, AnsiColors.ANSI_BG_YELLOW, AnsiColors.ANSI_BG_WHITE);
                break;
            case "APOLLO":
                pic.setMask("./src/main/resources/pan.txt");
                pic.setPalette(AnsiColors.ANSI_BRIGHT_BG_CYAN, AnsiColors.ANSI_BG_CYAN, AnsiColors.ANSI_BG_PURPLE, AnsiColors.ANSI_BRIGHT_BG_YELLOW, AnsiColors.ANSI_BG_YELLOW, AnsiColors.ANSI_BG_GREEN, AnsiColors.ANSI_BRIGHT_BG_PURPLE, AnsiColors.ANSI_BG_BLUE);
                break;
            default:
                pic.setPalette(AnsiColors.ANSI_BG_BLUE);
                break;
        }
    }

    /**
     * Sets up graphics for an arrow used to iterate through cards.
     * @param arrow the RectangleCLI instance to set the texture on.
     * @param userSelection an integer representing which part of the scene the user is selecting. Based on the selection, the color changes.
     * @param right a flag to distinguish between right and left arrow. True if the arrow to draw is the arrow pointing right.
     */
    public static void drawArrow(RectangleCLI arrow, int userSelection, boolean right)
    {
        if(right)
        {
            arrow.setMask("./src/main/resources/arrow.txt");
            arrow.setPalette(AnsiColors.ANSI_RESET, userSelection == 2? AnsiColors.ANSI_BG_RED : AnsiColors.ANSI_BG_WHITE);
        }
        else
        {
            arrow.setMask("./src/main/resources/arrow2.txt");
            arrow.setPalette(AnsiColors.ANSI_RESET, userSelection == 0? AnsiColors.ANSI_BG_RED : AnsiColors.ANSI_BG_WHITE);
        }
    }

    /**
     * Sets up graphics for the card background.
     * @param card the RectangleCLI instance to set the texture on.
     */
    public static void drawCard(RectangleCLI card)
    {
        card.setMask("./src/main/resources/card.txt");
        card.setPalette(AnsiColors.ANSI_BG_YELLOW, AnsiColors.ANSI_BRIGHT_BG_YELLOW);
    }

    /**
     * Sets up graphics for a number, used in the setup phase to select the number of players.
     * @param figure the RectangleCLI instance to set the texture on.
     * @param num an integer indicating which number to draw. Only "2" or "3" are allowed.
     * @param selection an integer representing which part of the scene the user is selecting. Based on the selection, the color changes.
     */
    public static void drawNumber(RectangleCLI figure, int num, int selection)
    {
        if(num==2)
        {
            figure.setMask("./src/main/resources/two.txt");
            figure.setPalette(selection == 2? AnsiColors.ANSI_BG_BLUE : AnsiColors.ANSI_BRIGHT_BG_BLUE, AnsiColors.ANSI_BRIGHT_BG_BLACK);
        }
        else if(num == 3)
        {
            figure.setMask("./src/main/resources/three.txt");
            figure.setPalette(selection == 3? AnsiColors.ANSI_BG_BLUE : AnsiColors.ANSI_BRIGHT_BG_BLUE, AnsiColors.ANSI_BRIGHT_BG_BLACK);
        }
    }

    /**
     * Sets up graphics for a worker to be represented on the game board.
     * @param worker the RectangleCLI instance to set the texture on.
     * @param player the ID number of the player, necessary to pick the correct color for the worker.
     */
    public static void drawWorker(RectangleCLI worker, char player)
    {
        worker.setMask("./src/main/resources/worker.txt");

        switch(player)
        {
            case '0':
                worker.setPalette(AnsiColors.ANSI_BRIGHT_BG_CYAN, AnsiColors.ANSI_BG_CYAN);
                break;
            case '1':
                worker.setPalette(AnsiColors.ANSI_BRIGHT_BG_PURPLE, AnsiColors.ANSI_BG_PURPLE);
                break;
            case '2':
                worker.setPalette(AnsiColors.ANSI_BG_RED, AnsiColors.ANSI_BRIGHT_BG_RED);
                break;
            default:
                break;
        }
    }
}