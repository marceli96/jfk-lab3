package dependent;

import Prism.Description;
import Prism.ICallable;

@Description(description = "arg1 + 2 * arg2")
public final class Callable2 implements ICallable {
    @Override
    public int generate(int number1, int number2) {
        return number1 + 2*number2;
    }
}
