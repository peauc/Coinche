package eu.epitech.jcoinche.jcoincheclient.client.StandardInputHandler;

import io.netty.channel.Channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Parser {
    private BufferedReader _in = new BufferedReader(new InputStreamReader(System.in));
    private Channel _channel;
    private String _string;
    private Map<String, Integer> _map = new HashMap<>();
    {{
        //IMSORRYJAVA
        _map.put("NAME", 0);
        _map.put("HAND", 1);
        _map.put("QUIT", 2);
        _map.put("CONTRACT", 3);
        _map.put("PASS", 4);
        _map.put("COINCHE", 5);
        _map.put("SURCOINCHE", 6);
        _map.put("PLAY", 7);
        _map.put("LAST", 8);
        _map.put("ANNOUNCE", 9);
        _map.put("BELOTE", 10);
        _map.put("REBELOTE", 11);
    }}

    public Parser(Channel channel) {
        _channel = channel;
    }

    public Boolean shouldParse() {
        try {
            return (_in.ready());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (false);
    }

    private void Read() throws IOException {
        _string = _in.readLine();
    }

    private void resetString() {
        _string = "";
    }

    private void dumpString() {
        System.out.print("|");
        System.out.print(_string);
        System.out.println("|");
    }

    public void Parse() {
        try {
            Read();
           } catch (IOException e) {
            e.printStackTrace();
        }
        dumpString();
    }

}
