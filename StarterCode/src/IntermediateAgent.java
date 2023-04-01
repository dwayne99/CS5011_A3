import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Formula;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;

public class IntermediateAgent extends Agent {
    public IntermediateAgent(Game game, int startX, int startY, boolean verbose) {
        super(game, startX, startY, verbose);
    }

    public void playGame() {
        System.out.println("Hello from Intermediate agent");
    }
}
