package dependent;

import Prism.Description;
import Prism.ICallable;

@Description(description = "Callable 4 - a type in another assembly")
public final class Callable4 implements ICallable {
    @Override
    public int generate(int number1, int number2) {
        return 3*number2 + 2*number1 + number1*number2;
    }
}
