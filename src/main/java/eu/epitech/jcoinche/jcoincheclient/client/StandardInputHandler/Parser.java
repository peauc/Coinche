package eu.epitech.jcoinche.jcoincheclient.client.StandardInputHandler;

import eu.epitech.jcoinche.jcoincheclient.client.utils.MessageFactory;
import eu.epitech.jcoinche.jcoincheclient.client.utils.Utils;
import eu.epitech.jcoinche.protocol.Coinche;
import io.netty.channel.Channel;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Parser {
    private BufferedReader _in = new BufferedReader(new InputStreamReader(System.in));
    private Channel _channel;
    private String _string;
    private Map<String, Integer> _map = new HashMap<>();
    private Map<Coinche.Card.Type, String> _color = new HashMap<>();
    private Map<Coinche.Card.Value, String> _number = new HashMap<>();

    {
        {
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
            _map.put("HELP", 12);
        }
    }


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
        Integer i = -1;
        for (Map.Entry<String, Integer> m : _map.entrySet()) {
            if (_string.contains(m.getKey())) {
                i = m.getValue();
            }
        }
        _string = _string.trim();
        switch (i) {
            case -1: {
                System.out.println("Please input a valid command, type HELP to see the list");
                break;
            }
            case 0: {
                Name(_string);
                break;
            }
            case 1: {
                Hand();
                break;
            }
            case 2: {
                System.exit(1);
                break;
            }
            case 7: {
                Play(_string);
                break;
            }
            case 9: {
                Announce();
                break;
            }
            case 12: {
                PrintHelp();
                break;
            }
        }
    }

    private void Announce() {

    }

    private void Play(String string) {
        if (_color.isEmpty() || _number.isEmpty()) {
            System.out.println("Creating comparaison maps");
            _color.put(Coinche.Card.Type.DIAMONDS, "DIAMONDS");
            _color.put(Coinche.Card.Type.HEARTS, "HEARTS");
            _color.put(Coinche.Card.Type.CLUBS, "CLUBS");
            _color.put(Coinche.Card.Type.SPADES, "SPADES");
            _number.put(Coinche.Card.Value.ACE, "ACE");
            _number.put(Coinche.Card.Value.KING, "KING");
            _number.put(Coinche.Card.Value.QUEEN, "QUEEN");
            _number.put(Coinche.Card.Value.JACK, "JACK");
            _number.put(Coinche.Card.Value.TEN, "TEN");
            _number.put(Coinche.Card.Value.NINE, "NICE");
            _number.put(Coinche.Card.Value.EIGHT, "EIGHT");
            _number.put(Coinche.Card.Value.SEVEN, "SEVEN");
        }

        if (!Utils.hasArguments(string)) {
            System.err.println("PLAY [SEVEN-EIGHT-NINE-TEN-JACK-QUEEN-KING-ACE] [CLUBS-DIAMONDS-HEARTS-SPADES]");
            return ;
        }
        string = Utils.getArguments(string);
        String[] input = string.split(" ");
        Coinche.Card.Value v = null;
        Coinche.Card.Type t = null;
        for (Map.Entry<Coinche.Card.Value, String> e : _number.entrySet()) {
            if (Objects.equals(input[0], e.getValue())) {

                v = e.getKey();
            }
        }
        for (Map.Entry<Coinche.Card.Type, String> e : _color.entrySet()) {
            if (Objects.equals(input[1], e.getValue())) {
                t = e.getKey();
            }
        }
        if (t != null && v != null) {
            Coinche.Message m = Coinche.Message.newBuilder().setType(Coinche.Message.Type.EVENT).setEvent(Coinche.Event.newBuilder().setType(Coinche.Event.Type.PLAY).setCard(Coinche.Card.newBuilder().setTypeValue(t.getNumber()).setValueValue(v.getNumber()).build()).build()).build();
            _channel.writeAndFlush(m);
        }
        else {
            System.err.println("Syntax error, type PLAY to have the correct syntax");
        }
    }

    private void Name(String line) {
        String arguments;

        if (!Utils.hasArguments(line)) {
            System.err.println("Name need an argument");
            return;
        }
        arguments = Utils.getArguments(line);
        Coinche.Message mess = Coinche.Message.newBuilder().setType(Coinche.Message.Type.EVENT)
                .setEvent(Coinche.Event.newBuilder().setType(Coinche.Event.Type.NAME).addArgument(arguments).build()).build();
        _channel.writeAndFlush(mess);
    }

    private void Hand() {
        Coinche.Message m = Coinche.Message.newBuilder().setType(Coinche.Message.Type.EVENT).setEvent(Coinche.Event.newBuilder().setType(Coinche.Event.Type.HAND).build()).build();
        _channel.writeAndFlush(m);
    }

    private void PrintHelp() {
        System.out.println("NAME nickname\nHAND\nQUIT\nCONTRACT\nPASS\nCOINCHE\nSURCOINCHE\nPLAY\nLAST\nANNOUNCE\nBELOTE\nREBELOTE\nHELP");
    }
}
