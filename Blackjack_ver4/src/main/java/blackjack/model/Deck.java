package blackjack.model;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    ArrayList<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
    }

    public void initialize(){
        cards.clear();
        for( SUIT s : SUIT.values()){
            for(RANK r : RANK.values()){
                cards.add(new Card(s, r));
            }
        }
        Collections.shuffle(cards);
    }

    public Card draw(){
        Card ret = cards.get(0);
        cards.remove(0);
        return ret;
    }
}
