package dependent;

import Prism.Description;
import Prism.ICallable;

@Description(description = "2 * arg1 + arg2")
public final class Callable1 implements ICallable {
    @Override
    public int generate(int number1, int number2) {
        return 2*number1 + number2;
    }
}
