package week02;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetTest {

    public static void main(String[] args) {
        InetAddress local = null;
        InetAddress hs = null;

        try {
            local = InetAddress.getLocalHost();
            // hs = InetAddress.getByName("www.hansung.ac.kr");

            byte[] addr = { (byte)220, (byte)66, (byte)102, (byte)11 };
            hs = InetAddress.getByAddress(addr);
        } catch (UnknownHostException e) {
            System.err.println("유효하지 않은 도메인입니다.");
            System.exit(1);
        }

        System.out.println(local.getHostName());
        System.out.println(local.getHostAddress());

        System.out.println(hs.getHostName());
        System.out.println(hs.getHostAddress());
    }
}
