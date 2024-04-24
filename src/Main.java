import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int bufferSize = Integer.parseInt(args[0]);

        BufferPool bufferPool = new BufferPool();
        bufferPool.initialize(bufferSize);
//        System.out.println(bufferPool);

        while (true) {
            System.out.println("The program is ready for the next command: ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("EXIT")) {
                break;
            }

            String[] res = isValidCommand(input);
            while (res == null) {
                System.out.println("Use commands: GET <record number>, SET <record number> <string of 40 bytes>, PIN <block id>, UNPIN <block id> or EXIT (input is case insensitive)");
                System.out.println("The program is ready for the next command: ");
                input = scanner.nextLine();
                res = isValidCommand(input);
            }

            String cmd = res[0];
            int num = Integer.parseInt(res[1]);
            String givenContent = res[2];


            //GET, SET, PIN, UNPIN
            if (!cmd.equalsIgnoreCase("GET") && !cmd.equalsIgnoreCase("SET") && !cmd.equalsIgnoreCase("PIN") && !cmd.equalsIgnoreCase("UNPIN")) {
                System.out.println("Use commands: GET <record number>, SET <record number> <string of 40 bytes>, PIN <block id>, UNPIN <block id> or EXIT (input is case insensitive)");
            } else {
                switch (cmd) {
                    case "GET":
                        System.out.println(bufferPool.GET(num));
                        System.out.println(bufferPool);
                        break;
                    case "SET":
                        System.out.println(bufferPool.SET(num, givenContent));
                        System.out.println(bufferPool);
                        break;
                    case "PIN":
                        System.out.println(bufferPool.PIN(num));
                        System.out.println(bufferPool);
                        break;
                    default:
                        System.out.println(bufferPool.UNPIN(num));
                        System.out.println(bufferPool);
                        break;
                }
            }
        }
    }

    public static String[] isValidCommand(String input) {
        String[] parts = input.split(" ", 3);
        int len = parts.length;
        String[] res = new String[3];
        /*
        if input has no parts or has only one part then request another input
        if input has two parts then check that it is not the set command
            if it is the set command and it has only two parts then request for a new input
         */

        if (len <= 1) {
            return null;
        } else if (len == 2 && !parts[0].equalsIgnoreCase("SET")) { //valid get pin or unpin
            String cmd = parts[0].toUpperCase();
            String num = parts[1];
            res[0] = cmd;
            res[1] = num;
            return res;
        } else if (len == 3 && parts[0].equalsIgnoreCase("SET")) { //valid set
            String cmd = parts[0].toUpperCase();
            String num = parts[1];
            String givenContent = parts[2].replace("\"", "");
            res[0] = cmd;
            res[1] = num;
            res[2] = givenContent;
            return res;
        }
        return null;
    }
}