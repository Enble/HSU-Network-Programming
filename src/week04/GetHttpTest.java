package week04;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class GetHttpTest {

    public static void main(String[] args) {
        InetAddress host = null;
        Socket cSocket = null;

        BufferedReader in = null;
        BufferedWriter out = null;

        try {
            // 클라이언트 소켓 생성
            host = InetAddress.getByName("sarammani.com");
            cSocket = new Socket(host, 80);

            // 네트워크 입출력 스트림 생성
            in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(cSocket.getOutputStream()));

            // 웹서버에 요청 전송
            out.write("GET /a.html HTTP/1.1\r\n");
            out.write("Host: sarammani.com\r\n");
            out.write("\r\n");
            out.flush();

            // 웹서버로부터 응답 수신
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                System.out.println(line);
            }
        } catch (UnknownHostException e) {
            System.err.println("유효하지 않은 사이트입니다: " + e.getMessage());
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("연결 오류: " + e.getMessage());
            System.exit(-1);
        } finally {
            // 스트림 및 소켓 종료
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (cSocket != null) {
                    cSocket.close();
                }
            } catch (IOException e) {
                System.err.println("연결 종료 실패: " + e.getMessage());
                System.exit(-1);
            }
        }

    }

}
