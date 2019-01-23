package dependent;

import Prism.Description;
import Prism.ICallable;

@Description(description = "Callable 1 - a type in another assembly")
public final class Callable1 implements ICallable {
    @Override
    public int generate(int number1, int number2) {
        return 2*number1 + number2;
    }
}
