package dependent;

import Prism.Description;
import Prism.ICallable;

@Description(description = "Callable 2 - a type in another assembly")
public final class Callable2 implements ICallable {
    @Override
    public int generate(int number1, int number2) {
        return number1 + 2*number2;
    }
}
