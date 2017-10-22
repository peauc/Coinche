package eu.epitech.jcoinche.jcoincheclient.client.StandardInputHandler;

import eu.epitech.jcoinche.jcoincheclient.client.utils.Utils;
import eu.epitech.jcoinche.protocol.Coinche;
import io.netty.channel.Channel;

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
    private Map<Coinche.Announce.Type, String> _announces = new HashMap<>();
    private Map<Coinche.Contract.Type, String> _colorContract = new HashMap<>();


    public Parser(Channel channel) {
        _channel = channel;
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
        _colorContract.put(Coinche.Contract.Type.DIAMONDS, "DIAMONDS");
        _colorContract.put(Coinche.Contract.Type.HEARTS, "HEARTS");
        _colorContract.put(Coinche.Contract.Type.CLUBS, "CLUBS");
        _colorContract.put(Coinche.Contract.Type.SPADES, "SPADES");
        _colorContract.put(Coinche.Contract.Type.AA, "AA");
        _colorContract.put(Coinche.Contract.Type.NA, "NA");
        _color.put(Coinche.Card.Type.HEARTS, "HEARTS");
        _color.put(Coinche.Card.Type.DIAMONDS, "DIAMONDS");
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
        _announces.put(Coinche.Announce.Type.CARRE, "CARRE");
        _announces.put(Coinche.Announce.Type.CENT, "CENT");
        _announces.put(Coinche.Announce.Type.CINQUANTE, "CINQUENTE");
        _announces.put(Coinche.Announce.Type.TIERCE, "TIERCE");

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
            case 3: {
                Contract(_string);
                break;
            }
            case 4: {
                Pass();
                break;
            }
            case 5: {
                CoincheFct();
                break;
            }
            case 6: {
                Surcoinche();
                break;
            }
            case 7: {
                Play(_string);
                break;
            }
            case 8: {
                Last();
                break;
            }
            case 9: {
                Announce(_string);
                break;
            }
            case 10: {
                Belote();
                break;
            }
            case 11: {
                Rebelote();
                break;
            }
            case 12: {
                PrintHelp();
                break;
            }
        }
    }

    private void Rebelote() {
        Coinche.Message.newBuilder().setType(Coinche.Message.Type.EVENT).setEvent(Coinche.Event.newBuilder().setType(Coinche.Event.Type.REBELOTE).build()).build();
    }

    private void Belote() {
        Coinche.Message.newBuilder().setType(Coinche.Message.Type.EVENT).setEvent(Coinche.Event.newBuilder().setType(Coinche.Event.Type.BELOTE).build()).build();
    }

    private void Last() {

    }

    private void CoincheFct() {
        Coinche.Message.newBuilder().setType(Coinche.Message.Type.EVENT).setEvent(Coinche.Event.newBuilder().setType(Coinche.Event.Type.COINCHE).build()).build();

    }

    private void Surcoinche() {
        Coinche.Message.newBuilder().setType(Coinche.Message.Type.EVENT).setEvent(Coinche.Event.newBuilder().setType(Coinche.Event.Type.SURCOINCHE).build()).build();
    }

    private void Pass() {
        Coinche.Message.newBuilder().setType(Coinche.Message.Type.EVENT).setEvent(Coinche.Event.newBuilder().setType(Coinche.Event.Type.PASS).build()).build();
    }

    private void Contract(String string) {

        if (!Utils.hasArguments(string)) {
            System.err.println("CONTRACT [score] [CLUBS-DIAMONDS-HEARTS-SPADES-AA-NA]");
            return;
        }
        string = Utils.getArguments(string);

        Integer score = null;
        Coinche.Contract.Type ContractType = null;
        String[] input = string.split(" ");
        System.out.println(input[0]);
        System.out.println(input[1]);
        try {
            score = Integer.parseInt(input[0]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        for (Map.Entry<Coinche.Contract.Type, String> e : _colorContract.entrySet()) {
            if (Objects.equals(input[1], e.getValue())) {

                ContractType = e.getKey();
            }
        }
        if (ContractType == null || score == null) {
            System.err.println("Syntax error, type CONTRACT to have the correc syntax");
        }
        else {
            Coinche.Message m = Coinche.Message.newBuilder().setType(Coinche.Message.Type.EVENT).setEvent(Coinche.Event.newBuilder().setType(Coinche.Event.Type.CONTRACT).setContract(Coinche.Contract.newBuilder().setScore(score).setTypeValue(ContractType.getNumber()).build()).build()).build();
            _channel.writeAndFlush(m);
        }
    }

    private void Announce(String string) {
        String[] input = string.split(" ");

        Coinche.Card.Type CardType = null;
        Coinche.Announce.Type AnnounceType = null;
        Coinche.Card.Value CardValue = null;
        if (!Utils.hasArguments(string)) {
            System.err.println("PLAY [CARRE-CENT-CINQUANTE-TIERCE] [SEVEN-EIGHT-NINE-TEN-JACK-QUEEN-KING-ACE] [CLUBS-DIAMONDS-HEARTS-SPADES]");
            return;
        }
        for (Map.Entry<Coinche.Announce.Type, String> e : _announces.entrySet()) {
            if (Objects.equals(input[0], e.getValue())) {

                AnnounceType = e.getKey();
            }
        }
        for (Map.Entry<Coinche.Card.Type, String> e : _color.entrySet()) {
            if (Objects.equals(input[1], e.getValue())) {
                CardType = e.getKey();
            }
        }
        for (Map.Entry<Coinche.Card.Value, String> e : _number.entrySet()) {
            if (Objects.equals(input[1], e.getValue())) {
                CardValue = e.getKey();
            }
        }
        if (AnnounceType == null || CardType == null) {
            System.err.println("Syntax error, type ANNOUNCE to have the correc syntax");
        }
        else {
            assert CardValue != null;
            Coinche.Message m = Coinche.Message.newBuilder().setType(Coinche.Message.Type.EVENT).setEvent(
                    Coinche.Event.newBuilder().setType(Coinche.Event.Type.ANNOUNCE).setAnnounce(
                            Coinche.Announce.newBuilder().setTypeValue(AnnounceType.getNumber()).setCard(
                                    Coinche.Card.newBuilder().setValueValue(CardValue.getNumber()).setTypeValue(CardType.getNumber()).build())).build()).build();
            _channel.writeAndFlush(m);
        }
    }



    private void Play(String string) {


        if (!Utils.hasArguments(string)) {
            System.err.println("PLAY [SEVEN-EIGHT-NINE-TEN-JACK-QUEEN-KING-ACE] [CLUBS-DIAMONDS-HEARTS-SPADES]");
            return;
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
        } else {
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
