package com.game;

import swiftbot.Button;
import swiftbot.SwiftBotAPI;
import swiftbot.Underlight;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main
{
    static void celebrationDive(short score) throws InterruptedException {
        blinkLED();
        int speed = 0;
        if(score >= 5 && score < 10){
            speed = score*10;
        }
        if(score >= 10){
            speed = 100;
        }
        //complete a V shape move with arm length 30 cm
        swiftBot.move(speed,speed,1800);
        swiftBot.move(80,0,1500);
        swiftBot.move(speed,speed,1800);
        blinkLED();
    }
    static void blinkLED() throws InterruptedException {
        Random rand = new Random();
        int[][] colours = {
                { 255, 0, 0 }, // Red
                { 0, 255, 0 }, // Green
                { 0, 0, 255 }, // Blue
                { 255, 255, 255 }, // White
                {255, 255, 0} // yellow
        };
        //loop 15 times to show 15 random colours
        for(int i = 0; i < 15; i++){
            int randomIndex = rand.nextInt(5);
            swiftBot.fillUnderlights(colours[randomIndex]);
            Thread.sleep(150);
        }
        swiftBot.disableUnderlights();
    }


    static void printScore(short round, short score)
    {
        GameUI.typewriter("\n\u001B[35m===============================\u001B[0m", 25);
        GameUI.typewriter("\u001B[35m    \u001B[35mRound: \u001B[35m" + round + "       Score: " + score + "\u001B[35m   \u001B[0m", 25);
        GameUI.typewriter("\u001B[35m===============================\u001B[0m\n", 25);
    }

    static void handleButtons(Button button, boolean[] controlVar)
    {
        swiftBot.enableButton(button,() -> controlVar[0] = false);

    }

    static void gameOver(short currentRound, short currentScore){

        GameUI.GameOverAnim();
        printScore(currentRound, currentScore);

        try
        {
            if (swiftBot != null)
            {
                swiftBot.disableAllButtons();
                swiftBot.disableUnderlights();
            }
        }
        catch (Exception ignored){}


        if(currentScore >= 5)
        {
            try {
                celebrationDive(currentScore);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.exit(0);
    }

    static void handleWrongPress(Button button, short currentRound, short currentScore)
    {
        // enable handlers for all other buttons and ignore if a handler already exists
        for (Button b : Button.values()) {
            if (b == button) continue;
            try
            {
                swiftBot.enableButton(b, () -> gameOver(currentRound, currentScore));
            }
            catch (IllegalArgumentException ignored) {}
        }
    }


    static SwiftBotAPI swiftBot;

    public static void main(String[] args) throws InterruptedException
    {

        try
        {
            swiftBot = SwiftBotAPI.INSTANCE;
        }

        catch (Exception e)
        {
            GameUI.typewriter("\nI2C disabled!", 25);
            System.exit(5);
        }

        GameUI.showIntroUI();

        short round = 1;
        short score = 0;





        //ARRAYS USED FOR STORING THE PREVIOUS COLOR SEQUENCE
        ArrayList<int[]> savedColors = new ArrayList<>();
        ArrayList<Underlight> savedUnderlights = new ArrayList<>();



        //Main game loop
        while(true)
        {

            printScore(round, score);
            GameUI.startCountdown(3);


            final int[][] colors =
            {
                    {255, 0, 0}, // red
                    {0, 255, 0}, // green
                    {0, 0, 255}, // blue
                    {255, 255, 0} // yellow
            };


            //GENERATE A NEW RANDOM COLOR AND SAVE IT
            Random rand = new Random();

            int randomIndex = rand.nextInt(4);
            savedColors.add(colors[randomIndex]);

            if(colors[randomIndex][0] == 255 && colors[randomIndex][1] == 0 )
            {
                savedUnderlights.add(Underlight.FRONT_LEFT);
            }
            else if(colors[randomIndex][0] == 0 && colors[randomIndex][1] == 255 )
            {
                savedUnderlights.add(Underlight.BACK_LEFT);
            }
            else if(colors[randomIndex][0] == 0 && colors[randomIndex][1] == 0 )
            {
                savedUnderlights.add(Underlight.FRONT_RIGHT);
            }
            else
            {
                savedUnderlights.add(Underlight.BACK_RIGHT);
            }


            //Displaying the color sequence
            for(int i = 0; i < round; i++)
            {
                swiftBot.setUnderlight(savedUnderlights.get(i), savedColors.get(i));
                Thread.sleep(1500);
                swiftBot.disableUnderlights();
            }


            //Player enters the color sequence
            for(int i = 0; i < round; i++)
            {
                final short currentRound = round;
                final short currentScore = score;

                boolean[] controlVar = {true};
                switch (savedUnderlights.get(i))
                {
                    case FRONT_LEFT:
                        handleButtons(Button.A, controlVar);
                        handleWrongPress(Button.A, currentRound, currentScore);
                        while(controlVar[0])
                        {
                            Thread.sleep(50);
                        }
                        break;

                    case BACK_LEFT:
                        handleButtons(Button.B, controlVar);
                        handleWrongPress(Button.B, currentRound, currentScore);
                        while(controlVar[0])
                        {
                            Thread.sleep(50);
                        }
                        break;

                    case FRONT_RIGHT:
                        handleButtons(Button.X, controlVar);
                        handleWrongPress(Button.X, currentRound, currentScore);
                        while(controlVar[0])
                        {
                            Thread.sleep(50);
                        }
                        break;

                    case BACK_RIGHT:
                        handleButtons(Button.Y, controlVar);
                        handleWrongPress(Button.Y, currentRound, currentScore);
                        while(controlVar[0])
                        {
                            Thread.sleep(50);
                        }
                        break;
                }


                swiftBot.disableAllButtons();
            }


            score++;


            if(round % 5 == 0)
            {
                Scanner sc = new Scanner(System.in);
                boolean controlVar = true;
                while(controlVar)
                {
                    GameUI.typewriter("Would you like to continue? (Y/N)", 25);
                    String input = sc.nextLine();

                    if(input.equalsIgnoreCase("N") || input.equalsIgnoreCase("NO") )
                    {
                        GameUI.typewriter("See you again champ!", 25);
                        printScore(round, score);
                        celebrationDive(score);
                        swiftBot.disableAllButtons();
                        System.exit(0);
                    }
                    else if (input.equalsIgnoreCase("Y") || input.equalsIgnoreCase("YES"))
                    {
                        controlVar = false;
                    }
                    else
                    {
                        GameUI.typewriter("Invalid input! Please try again.", 25);
                    }
                }

            }

                round++;

        }


    }

}

class  GameUI{

    final static String PURPLE ="\u001B[35m";
    final static String CYAN  ="\u001B[36m";
    final static String GREEN ="\u001B[32m";
    final static String RED    ="\u001B[31m";
    final static String YELLOW  ="\u001B[33m";
    final static String RESET ="\u001B[0m";


    static void typewriter(String text, int delay) throws InterruptedException {
       for(char c : text.toCharArray()) {
           System.out.print(c);
           Thread.sleep(delay);
       }
       System.out.println();
    }

    static void LoadingBar() throws InterruptedException {
        for (int i = 0; i <= 20; i++) {
            System.out.print("\r" + GREEN + "Loading: ["
                    + new String(new char[i]).replace("\0", "=")
                    + new String(new char[20 - i]).replace("\0", " ")
                    + "] " + (i * 5) + "%" + RESET);

            Thread.sleep(100);
        }
        System.out.println("\n");
    }


    static void startCountdown(int sec) throws InterruptedException {
       for(int i = sec; i > 0; i--){
           System.out.println("\r" + CYAN + "Starting round in:" + YELLOW + i +"s" + RESET);
           Thread.sleep(1000);
       }
       System.out.println("\n");
   }

   static void animateGameOver(String art) throws InterruptedException {
       String[] glow = { RED, PURPLE, CYAN};

       for(int i=0; i<6; i++){
              System.out.print("\r" + glow[i % glow.length] + art + RESET);
              Thread.sleep(250);
       }
    }
 
    public static void showIntroUI() throws Exception{

        String header=
         "╔═══════════════════════════════════════╗\n" +
         "║             SWIFTBOT SIMON GAME       ║\n" +
         "╚═══════════════════════════════════════╝\n";

         for (char ch: header.toCharArray()) {
             System.out.print(PURPLE + ch + RESET);
             Thread.sleep(5);
         }

         typewriter(GREEN + "\n WELCOME, Champ! Get ready..." + RESET, 25);
            LoadingBar();
            startCountdown(3);
    }

    public static void GameOverAnim() throws InterruptedException {
        String art =
        "\n███████╗ █████╗ ███╗   ███╗███████╗   ██████╗ ██╗   ██╗███████╗██████╗\n" +
        "██╔════╝██╔══██╗████╗ ████║██╔════╝     ██╔══██╗██║   ██║██╔════╝██╔══██╗\n" +
        "█████╗  ███████║██╔████╔██║█████╗       ██████╔╝██║   ██║█████╗  ██████╔╝\n" +
        "██╔══╝  ██╔══██║██║╚██╔╝██║██╔══╝       ██╔══██╗██║   ██║██╔══╝  ██╔══██╗\n" +
        "███████╗██║  ██║██║ ╚═╝ ██║███████╗     ██████╔╝╚██████╔╝███████╗██║  ██║\n" +
        "╚══════╝╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝     ╚═════╝  ╚═════╝ ╚══════╝╚═╝  ╚═╝\n";

        animateGameOver(art);
        System.out.println(RED + "\n Game Over!" + RESET);
    }
}
