import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {


        System.out.println("Enter buffer size: ");

        Scanner scanner = new Scanner(System.in);
        int bufferSize = Integer.parseInt(scanner.nextLine());

        BufferPool bufferPool = new BufferPool();
        bufferPool.initialize(bufferSize);
        System.out.println(bufferPool);

        while(true) {
            System.out.println("The program is ready for the next command: ");
            //Scanner scanner2 = new Scanner(System.in);
            String input = scanner.nextLine();

            if(input.equalsIgnoreCase("EXIT")){
                break;
            }

            String[] parts = input.split(" ");

            String cmd = parts[0].toUpperCase();
            int num = Integer.parseInt(parts[1]);
            String givenContent = null;

            if (parts.length > 2) {
                givenContent = input.substring(input.indexOf(parts[1])+2);
            }

            //GET, SET, PIN, UNPIN

            if (!cmd.equals("GET") && !cmd.equals("SET") && !cmd.equals("PIN") && !cmd.equals("UNPIN")) {
                System.out.println("Use commands: GET <record number>, SET <record number> <string of 40 bytes>, PIN <block id>, UNPIN <block id> or EXIT (input is case insensitive)");
            } else {
                switch (cmd) {
                    case "GET":
                        System.out.println(bufferPool.GET(num));
                        System.out.println(bufferPool);
                        break;
                    case "SET":
                        bufferPool.SET(num, givenContent);
                        System.out.println(bufferPool);
                        break;
                    case "PIN":
                        bufferPool.PIN(num);
                        System.out.println(bufferPool);
                        break;
                    default:
                        bufferPool.UNPIN(num);
                        System.out.println(bufferPool);
                        break;
                }
            }
        }



    }
}