package eugene.board;

import eugene.board.model.Card;
import eugene.board.model.Column;
import eugene.board.service.Iteration;
import eugene.board.service.impl.IterationImpl;

import javax.naming.LimitExceededException;
import java.util.LinkedList;

public class Board {

    private Iteration iterationService;

    private Board(LinkedList<Column> columns) {

        iterationService = new IterationImpl(columns);

    }

    /**
     * Agile Board mainly does the following:
     * <ol>
     *     <li>creates a board and an iteration belonging to it with given columns</li>
     *     <li>cards can be assigned to iteration and columns</li>
     *     <li>cards can be moved to other columns</li>
     *     <li>last card movement can be reverted</li>
     *     <li>velocity of the iteration can be calculated</li>
     *     <li>work in progress limit can be enforced for columns</li>
     *     <li>list of all cards belonging to a column can be retrieved</li>
     * </ol>
     * Unit tests are in IterationImplSpec, and this main method is used for functional testing, to show functionality
     * @param args
     */
    public static void main(String[] args) {

        LinkedList<Column> columns = new LinkedList<>();
        columns.add(new Column("analysis", 15));
        columns.add(new Column("development", 20));
        columns.add(new Column("testing", 17));

        Board board = new Board(columns);
        Iteration iteration = board.iterationService;

        Card firstCard = iteration.createCard("Create User", "Create User Feature", 7);
        Card secondCard = iteration.createCard("Modify User", "Modify User Feature", 5);
        Card thirdCard = iteration.createCard("Delete User", "Delete User Feature", 6);

        iteration.add(firstCard);
        System.out.println("Cards in starting column after only first card is added: "
                + iteration.getCardsInColumn("starting"));
        iteration.add(secondCard);
        System.out.println("Cards in starting column after second card is added: "
                + iteration.getCardsInColumn("starting"));
        iteration.add(thirdCard);
        System.out.println("Cards in starting column after third card is added: "
                + iteration.getCardsInColumn("starting"));

        System.out.println("Total number of points in starting column now: "
                + iteration.getTotalPointsForColumn("starting"));

        System.out.println("Total number of points in done column now(velocity): "
                + iteration.velocity());

        try {
            iteration.moveCard(thirdCard, "done");
            iteration.moveCard(firstCard, "analysis");
            iteration.moveCard(secondCard, "analysis");
            iteration.moveCard(thirdCard, "analysis");
        } catch (LimitExceededException lee) {
            System.out.println("Move card operation aborted with the following root cause: " + lee.getMessage());
        }
        System.out.println("Total number of points in analysis column after third card failed to move: "
                + iteration.getTotalPointsForColumn("analysis"));
        System.out.println("Cards in analysis column after third card failed to move: "
                + iteration.getCardsInColumn("analysis"));
        iteration.undoLastMove();
        System.out.println("Total number of points in analysis column after undo last move: "
                + iteration.getTotalPointsForColumn("analysis"));
        System.out.println("Cards in analysis column after undo last move: "
                + iteration.getCardsInColumn("analysis"));

        System.out.println("Cards in starting column now: "
                + iteration.getCardsInColumn("starting"));
        System.out.println("Cards in done column now: "
                + iteration.getCardsInColumn("done"));
        System.out.println("Total number of points in done column now(velocity): "
                + iteration.velocity());
    }
}
