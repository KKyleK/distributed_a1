package distributed_a1;

import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class UDPMicroservice {
    private static final String USAGE = "Usage: java UDPMicroservice [port]";
    protected DatagramSocket socket = null;
    protected BufferedReader in = null;

    private File file = new File("words.txt");

    private int wordCount = 21543;
    
    public UDPMicroservice() throws IOException {
        this(5599);
    }
    
    public UDPMicroservice(int port) throws IOException {
        socket = new DatagramSocket(port);
    }
    
    public void serve() {
        while(true) {
            try {
                System.out.println("Listening for incoming requests...");
                byte[] inputbuf = new byte[256];
                byte[] outputbuf = new byte[256];

                // receive request
                DatagramPacket udpRequestPacket = new DatagramPacket(inputbuf, inputbuf.length);
                socket.receive(udpRequestPacket);

                String msg = new String(udpRequestPacket.getData(), udpRequestPacket.getOffset(), udpRequestPacket.getLength());
                //System.out.println(msg);

                outputbuf = pick(msg).getBytes();
               
                // Send the response to the client.
                // Address and port are extracted from client request message. 
                InetAddress address = udpRequestPacket.getAddress();
                int port = udpRequestPacket.getPort();
                DatagramPacket udpReplyPacket = 
                        new DatagramPacket(outputbuf, outputbuf.length, address, port);
                socket.send(udpReplyPacket);
            } catch (SocketException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }   
    }

    public String pick(String input)
    {
        String[] sp = input.split(" ");
        String output = null;

        switch(sp[0])
        {
            case "add":
                add(sp[1]);
                output = "Added.";
                break;
            case "remove":
                remove(sp[1]);
                output = "Removed.";
                break;
            case "check":
                output = check(sp[1]);
                break;
            case "random":
                output = random();
                break;
        }

        return output;
    }

    public void add(String input)
    {
        Scanner sc = null;
        FileWriter fw = null;
        StringBuffer sb = new StringBuffer();
        boolean inserted = false;

        try
        {
            sc = new Scanner(file);
        }
        catch(FileNotFoundException e)
        {
            System.err.println(e);
        }

        while(sc.hasNextLine())
        {
            String word = sc.nextLine();

            if(word.compareToIgnoreCase(input) == 0)
            {
                inserted = true;
            }
            else if(word.compareToIgnoreCase(input) > 0 && inserted == false)
            {
                inserted = true;
                sb.append(input + System.lineSeparator());
                wordCount++;
            }

            sb.append(word + System.lineSeparator());
        }

        sc.close();

        try
        {
            fw = new FileWriter(file);
            fw.append(sb.toString());
            fw.flush();
            fw.close();
        }
        catch(IOException e)
        {
            System.err.println(e);
        }
    }

    public void remove(String input)
    {
        Scanner sc = null;
        FileWriter fw = null;
        StringBuffer sb = new StringBuffer();

        try
        {
            sc = new Scanner(file);
        }
        catch(FileNotFoundException e)
        {
            System.err.println(e);
        }

        while(sc.hasNextLine())
        {
            String word = sc.nextLine();

            if(word.equals(input))
            {
                wordCount--;
                continue;
            }

            sb.append(word + System.lineSeparator());
        }

        sc.close();

        try
        {
            fw = new FileWriter(file);
            fw.append(sb.toString());
            fw.flush();
            fw.close();
        }
        catch(IOException e)
        {
            System.err.println(e);
        }
    }
    
    public String check(String input)
    {
        boolean found = false;
        Scanner sc = null;

        try
        {
            sc = new Scanner(file);
        }
        catch(FileNotFoundException e)
        {
            System.err.println(e);
        }

        while(sc.hasNextLine())
        {
            String word = sc.nextLine();
            if(word.equals(input))
            {
                found = true;
                break;
            }
        }

        sc.close();

        if(found == true)
        {
            return "Found.";
        }
        else
        {
            return "Not found.";
        }
    }

    public String random()
    {
        Random rand = new Random();

        int n = rand.nextInt(wordCount);

        String line = null;

        try (Stream<String> lines = Files.lines(Paths.get("words.txt"))) 
        {
            line = lines.skip(n).findFirst().get();
            System.out.println(line);
          }
          catch(IOException e){
            System.out.println(e);
          }

        return line.toLowerCase();
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println(USAGE);
            System.exit(1);
        }
        
        int port = 0;
        UDPMicroservice server = null;
        try {
            port = Integer.parseInt(args[0]);
            server = new UDPMicroservice(port);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number: " + port + ".");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + port);
            System.out.println(e.getMessage());
        }
        server.serve();
        server.socket.close();
    }
}