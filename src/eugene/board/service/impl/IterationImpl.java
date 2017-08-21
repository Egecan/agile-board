package eugene.board.service.impl;

import eugene.board.model.Card;
import eugene.board.model.Column;
import eugene.board.service.Iteration;

import javax.naming.LimitExceededException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class IterationImpl implements Iteration {

    private static final String STARTING = "starting";
    private static final String DONE = "done";

    private LinkedList<Column> columns;
    private HashMap<String, Card> allCardsMap = new HashMap<>();
    private HashMap<String, String> cardToColumnMap = new HashMap<>();

    private String lastCardMoved = "";
    private String lastStateOfCard = "";

    public IterationImpl(LinkedList<Column> columns) {
        this.columns = columns;
        if (! checkFirstColumn(STARTING)) {
            this.columns.addFirst(new Column(STARTING));
        }
        if (! checkLastColumn(DONE)) {
            this.columns.addLast(new Column(DONE));
        }
    }

    public Card createCard(String title, String description, int points) {
        Card newCard = new Card(title, description, points);
        allCardsMap.put(title, newCard);
        return newCard;
    }

    public void add(Card card) {
        if (!allCardsMap.containsKey(card.getTitle())) {
            createCard(card.getTitle(), card.getDescription(), card.getPoints());
        }
        cardToColumnMap.put(card.getTitle(), STARTING);
    }

    public void moveCard(Card card, String toColumn) throws LimitExceededException {
        String cardTitle = card.getTitle();

        Optional<Column> result = columns.stream()
                .filter(e -> e.getName().equals(toColumn))
                .findFirst();

        if (result.isPresent()) {
            int wipLimit = result.get().getLimit();

            if (getTotalPointsForColumn(toColumn) + card.getPoints() > wipLimit) {
                throw new LimitExceededException("Work In Progress limit exceeded for column: " + toColumn);
            }

            lastCardMoved = cardTitle;
            lastStateOfCard = cardToColumnMap.getOrDefault(cardTitle, "");

            cardToColumnMap.put(cardTitle, toColumn);
        }
    }

    public void undoLastMove() {
        if (!"".equalsIgnoreCase(lastCardMoved) && cardToColumnMap.containsKey(lastCardMoved)) {
            cardToColumnMap.put(lastCardMoved, lastStateOfCard);
        }
    }

    public int velocity() {
        return getTotalPointsForColumn(DONE);
    }

    public int getTotalPointsForColumn(String columnName) {
        return getCardsInColumn(columnName).stream()
                .filter(cardName -> allCardsMap.get(cardName) != null)
                .mapToInt(cardName -> allCardsMap.get(cardName).getPoints())
                .sum();
    }

    public List<String> getCardsInColumn(String columnName) {
        return cardToColumnMap.entrySet().stream()
                .filter(e -> e.getValue().equals(columnName))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private boolean checkFirstColumn(String columnName) {
        if (columns.peekFirst() == null) {
            return false;
        }
        return columnName.equals(columns.peekFirst().getName());
    }

    private boolean checkLastColumn(String columnName) {
        if (columns.peekLast() == null) {
            return false;
        }
        return columnName.equals(columns.peekLast().getName());
    }
}
