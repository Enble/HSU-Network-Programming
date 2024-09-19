package week04;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class IntClient {
    private String serverAddress;
    private int serverPort;

    private OutputStream out;

    public IntClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        connectToServer();
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            out = socket.getOutputStream();
        } catch (IOException e) {
            System.err.println("클라이언트 연결 오류: " + e.getMessage());
            System.exit(-1);
        }
    }

    private void sendMessage(int msg) {
        try {
            out.write(msg);
        } catch (IOException e) {
            System.err.println("클라이언트 쓰기 오류: " + e.getMessage());
            System.exit(-1);
        }

        if (msg == 0) {
            try {
                out.close();
            } catch (IOException e) {
                System.err.println("클라이언트 닫기 오류: " + e.getMessage());
                System.exit(-1);
            }

            System.exit(0);
        }

        System.out.println("나: " + msg);
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("데이터를 입력하세요 (종료: '0'): ");
            int message = scanner.nextInt();

            sendMessage(message);
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 54321;

        IntClient client = new IntClient(serverAddress, serverPort);
        client.start();
    }
}
