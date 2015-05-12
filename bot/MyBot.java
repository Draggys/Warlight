package bot;
import java.util.Scanner;

public class MyBot {

    private Scanner scan = new Scanner(System.in);

    public void run()
    {
        while(scan.hasNextLine()) {
            String line = scan.nextLine();

            if(line.length() == 0) {
                continue;
            }

            String[] parts = line.split(" ");

            if(parts[0].equals("pick_starting_regions")) {
                System.out.println( "give me randomly" );
            }
            else if(parts.length == 3 && parts[0].equals("go")) {
                String output = "";

                if(parts[1].equals("place_armies")) {

                    for(int i=1; i<=42; i++) {
                        output.concat("myBot place_armies " + i + " 1,");
                    }
                }
                else if(parts[1].equals("attack/transfer")) {
                    for(int i=1; i<=41; i++) {
                        output.concat("myBot attack/transfer " + i + " " + i+1 + " 1,");
                    }
                }

                System.out.println(output);
            }
        }
    }

    public static void main(String[] args) {
        (new MyBot()).run();
    }
}