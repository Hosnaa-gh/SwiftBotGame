package com.game;


import swiftbot.SwiftBotAPI;
import swiftbot.Underlight;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main
{


    static void printScore(short round, short score)
    {
        System.out.println("\n\u001B[35m===============================\u001B[0m");
        System.out.println("\u001B[35m    \u001B[35mRound: \u001B[35m" + round + "       Score: " + score + "\u001B[35m   \u001B[0m");
        System.out.println("\u001B[35m===============================\u001B[0m\n");
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
            System.out.println("\nI2C disabled!");
            System.exit(5);
        }


        short round = 1;
        short score = 0;





        //ARRAYS USED FOR STORING THE PREVIOUS COLOR SEQUENCE
        ArrayList<int[]> savedColors = new ArrayList<>();
        ArrayList<Underlight> savedUnderlights = new ArrayList<>();

        //control variable for the main game loop
        boolean finish = true;

        //Main game loop
        while(finish)
        {

            for(int i = 3; i > 0; i--)
            {
                System.out.println("Staring round in: " + i + "s");
                Thread.sleep (1000);
            }


            printScore(round, score);

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




            if(round % 5 == 0)
            {
                Scanner sc = new Scanner(System.in);
                boolean controlVar = true;
                while(controlVar)
                {
                    System.out.println("Would you like to continue? (Y/N)");
                    String input = sc.nextLine();

                    if(input.equalsIgnoreCase("N") || input.equalsIgnoreCase("NO") )
                    {
                        System.out.println("See you again champ!");
                        finish = false;
                        controlVar = false;
                    }
                    else if (input.equalsIgnoreCase("Y") || input.equalsIgnoreCase("YES"))
                    {
                        controlVar = false;
                    }
                    else
                    {
                        System.out.println("Invalid input! Please try again.");
                    }
                }

            }

                round++;

        }


    }

}
//git fetch origin
//git switch --track origin/dev