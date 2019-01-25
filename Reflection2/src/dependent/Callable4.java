package dependent;

import Prism.Description;
import Prism.ICallable;

@Description(description = "3 * arg2 + 2 * arg1 + arg1 * arg2")
public final class Callable4 implements ICallable {
    @Override
    public int generate(int number1, int number2) {
        return 3*number2 + 2*number1 + number1*number2;
    }
}
