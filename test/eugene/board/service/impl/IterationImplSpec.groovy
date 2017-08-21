package eugene.board.service.impl

import eugene.board.model.Card
import eugene.board.model.Column
import spock.lang.Specification

import javax.naming.LimitExceededException

class IterationImplSpec extends Specification {

    IterationImpl iteration = new IterationImpl(new LinkedList<>())

    def setup(){}

    def "iterationImpl constructor"() {
        given:
        LinkedList<Column> columns = new LinkedList<>()
        columns.add(new Column("development", 10))

        when:
        new IterationImpl(columns)

        then:
        columns.getFirst().getName() == "starting"
        columns.get(1).getName() == "development"
        columns.get(1).getLimit() == 10
        columns.getLast().getName() == "done"
    }

    def "createCard should create card and add to allCardsMap"() {
        when:
        def result = iteration.createCard("My Card", "My Card Explained", 5)

        then:
        result.title == "My Card"
        result.description == "My Card Explained"
        result.points == 5

        and:
        iteration.allCardsMap.get("My Card").title == "My Card"
        iteration.allCardsMap.get("My Card").description == "My Card Explained"
        iteration.allCardsMap.get("My Card").points == 5
    }

    def "add should add the card to starting column in iteration"() {
        given:
        def someCard = new Card("some card", "some card description", 3)

        when:
        iteration.add(someCard)

        then:
        iteration.allCardsMap.size() == 1
        iteration.allCardsMap.get("some card").title == "some card"
        iteration.allCardsMap.get("some card").description == "some card description"
        iteration.allCardsMap.get("some card").points == 3
        iteration.cardToColumnMap.get("some card") == "starting"
    }

    def "moveCard should move card to column and save the previous column the card belonged to"() {
        given:
        def someCard = new Card("some card", "some card description", 7)
        iteration.allCardsMap.put(someCard.title, someCard)
        iteration.cardToColumnMap.put(someCard.title, "starting")

        when:
        iteration.moveCard(someCard, "done")

        then:
        iteration.cardToColumnMap.size() == 1
        iteration.cardToColumnMap.get("some card") == "done"
        iteration.lastCardMoved == "some card"
        iteration.lastStateOfCard == "starting"
    }

    def "moveCard to non-existing column should do nothing"() {
        given:
        def someCard = new Card("some card", "some card description", 3)

        when:
        iteration.moveCard(someCard, "development")

        then:
        iteration.cardToColumnMap.size() == 0
    }

    def "moveCard to a column should throw exception when column limit exceeds"() {
        given:
        LinkedList<Column> columns = new LinkedList<>()
        columns.add(new Column("development", 10))
        IterationImpl iterationWithColumns = new IterationImpl(columns)
        def someCard = new Card("some card", "some card description", 7)
        def myCard = new Card("My Card", "My Card Explained", 5)
        iterationWithColumns.allCardsMap.put(someCard.getTitle(), someCard)
        iterationWithColumns.allCardsMap.put(myCard.getTitle(), myCard)

        when:
        iterationWithColumns.moveCard(someCard, "development")
        iterationWithColumns.moveCard(myCard, "development")

        then:
        thrown(LimitExceededException)
    }

    def "undoLastMove should do nothing if there has not been any movements yet"() {
        when:
        iteration.undoLastMove()

        then:
        iteration.lastCardMoved == ""
        iteration.lastStateOfCard == ""
    }

    def "undoLastMove should undo last moveCard call"() {
        given:
        def someCard = new Card("some card", "some card description", 7)
        iteration.allCardsMap.put(someCard.title, someCard)
        iteration.cardToColumnMap.put(someCard.title, "done")
        iteration.lastCardMoved = "some card"
        iteration.lastStateOfCard = "starting"

        when:
        iteration.undoLastMove()

        then:
        iteration.cardToColumnMap.get("some card") == "starting"
    }

    def "velocity should return the total number of points in done column"() {
        given:
        def someCard = new Card("some card", "some card description", 7)
        def myCard = new Card("My Card", "My Card Explained", 5)
        def anotherCard = new Card("Another Card", "Another Card Explained", 6)
        iteration.allCardsMap.put(someCard.title, someCard)
        iteration.allCardsMap.put(myCard.title, myCard)
        iteration.allCardsMap.put(anotherCard.title, anotherCard)
        iteration.cardToColumnMap.put(someCard.title, "done")
        iteration.cardToColumnMap.put(myCard.title, "starting")
        iteration.cardToColumnMap.put(anotherCard.title, "done")

        when:
        def result = iteration.velocity()

        then:
        result == 13
    }

    def "velocity should return 0 if there are no cards in done column"() {
        when:
        def result = iteration.velocity()

        then:
        result == 0
    }

    def "getTotalPointsForColumn should return the total number of points in the given column"() {
        given:
        LinkedList<Column> columns = new LinkedList<>()
        columns.add(new Column("development", 10))
        IterationImpl iterationWithColumns = new IterationImpl(columns)
        def someCard = new Card("some card", "some card description", 7)
        def myCard = new Card("My Card", "My Card Explained", 5)
        def anotherCard = new Card("Another Card", "Another Card Explained", 6)
        iterationWithColumns.allCardsMap.put(someCard.title, someCard)
        iterationWithColumns.allCardsMap.put(myCard.title, myCard)
        iterationWithColumns.allCardsMap.put(anotherCard.title, anotherCard)
        iterationWithColumns.cardToColumnMap.put(someCard.title, "development")
        iterationWithColumns.cardToColumnMap.put(myCard.title, "development")
        iterationWithColumns.cardToColumnMap.put(anotherCard.title, "done")

        when:
        def result = iterationWithColumns.getTotalPointsForColumn("development")

        then:
        result == 12
    }

    def "getTotalPointsForColumn should return the 0 if given column name is unknown"() {
        given:
        LinkedList<Column> columns = new LinkedList<>()
        columns.add(new Column("development", 10))
        IterationImpl iterationWithColumns = new IterationImpl(columns)
        def someCard = new Card("some card", "some card description", 7)
        def myCard = new Card("My Card", "My Card Explained", 5)
        def anotherCard = new Card("Another Card", "Another Card Explained", 6)
        iterationWithColumns.allCardsMap.put(someCard.title, someCard)
        iterationWithColumns.allCardsMap.put(myCard.title, myCard)
        iterationWithColumns.allCardsMap.put(anotherCard.title, anotherCard)
        iterationWithColumns.cardToColumnMap.put(someCard.title, "development")
        iterationWithColumns.cardToColumnMap.put(myCard.title, "development")
        iterationWithColumns.cardToColumnMap.put(anotherCard.title, "done")

        when:
        def result = iterationWithColumns.getTotalPointsForColumn("random")

        then:
        result == 0
    }

    def "getCardsInColumn should return all the cards belonging to given column"() {
        given:
        LinkedList<Column> columns = new LinkedList<>()
        columns.add(new Column("development", 10))
        IterationImpl iterationWithColumns = new IterationImpl(columns)
        def someCard = new Card("some card", "some card description", 7)
        def myCard = new Card("My Card", "My Card Explained", 5)
        def anotherCard = new Card("Another Card", "Another Card Explained", 6)
        iterationWithColumns.allCardsMap.put(someCard.title, someCard)
        iterationWithColumns.allCardsMap.put(myCard.title, myCard)
        iterationWithColumns.allCardsMap.put(anotherCard.title, anotherCard)
        iterationWithColumns.cardToColumnMap.put(someCard.title, "development")
        iterationWithColumns.cardToColumnMap.put(myCard.title, "development")
        iterationWithColumns.cardToColumnMap.put(anotherCard.title, "done")

        when:
        def result = iterationWithColumns.getCardsInColumn("development")

        then:
        result.size() == 2
        result.contains(myCard.title)
        result.contains(someCard.title)
    }

}
