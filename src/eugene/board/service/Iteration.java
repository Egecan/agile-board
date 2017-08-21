package eugene.board.service;

import eugene.board.model.Card;

import javax.naming.LimitExceededException;
import java.util.List;


public interface Iteration {

    Card createCard(String title, String description, int points);

    void add(Card card);

    void moveCard(Card card, String toColumn) throws LimitExceededException;

    void undoLastMove();

    int velocity();

    int getTotalPointsForColumn(String columnName);

    List<String> getCardsInColumn(String columnName);
}
