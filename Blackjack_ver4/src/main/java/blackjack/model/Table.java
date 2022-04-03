package blackjack.model;

import java.util.ArrayDeque;

public class Table {
    public static int BLACK_JACK_NUMBER = 21;
    public static int ACE_BIG_VALUE = 11;
    public static int ACE_SMALL_VALUE = 1;
    public static int DEALER_MUST_DRAW_TO = 16;

    private Deck deck;
    private ArrayDeque<Card> userCards;
    private ArrayDeque<Card> dealerCards;

    public Table() {
        deck = new Deck();
        userCards = new ArrayDeque<>();
        dealerCards = new ArrayDeque<>();
    }

    public void initialize(){
        deck.initialize();
        userCards.clear();
        userCards.add(deck.draw());
        userCards.add(deck.draw());
        dealerCards.clear();
        dealerCards.add(deck.draw());
    }

    public ArrayDeque<Card> getUserCards() {
        return userCards;
    }

    public ArrayDeque<Card> getDealerCards() {
        return dealerCards;
    }

    public void hit(){
        userCards.add(deck.draw());
        if(BLACK_JACK_NUMBER <= getUserCount()){
            dealerCards.add(deck.draw());
        }
    }

    public void stand(){
        while(getDealerCount() < DEALER_MUST_DRAW_TO){
            dealerCards.add(deck.draw());
        }
    }

    public int getUserCount(){
        return getCardsCount(userCards);
    }

    public int getDealerCount(){
        return getCardsCount(dealerCards);
    }

    private int getCardsCount(ArrayDeque<Card> cards){
        int ret = 0;
        int aceCount = 0;
        for(Card c : cards){
            if(c.getRank() == RANK.ACE){
                aceCount++;
                continue;
            }
            ret += c.getValue();
        }

        while(1 < aceCount){
            ret += ACE_SMALL_VALUE;
            --aceCount;
        }

        if(0 < aceCount){
            ret += (BLACK_JACK_NUMBER < ret + ACE_BIG_VALUE) ?
                    ACE_SMALL_VALUE : ACE_BIG_VALUE;
        }

        return ret;
    }
}
