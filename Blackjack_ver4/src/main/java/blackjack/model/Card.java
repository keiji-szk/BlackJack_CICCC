package blackjack.model;

import java.util.Objects;

public class Card {
    private SUIT suit;
    private RANK rank;

    public Card(SUIT suit, RANK rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public SUIT getSuit() {
        return suit;
    }

    public RANK getRank() {
        return rank;
    }

    public int getValue(){
        if(10 < rank.getValue()){
            return 10;
        }
        return rank.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return suit == card.suit && rank == card.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, rank);
    }
}
