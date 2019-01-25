package dependent;

import Prism.Description;
import Prism.ICallable;

@Description(description = "arg1 * arg2 - 3 * arg2")
public final class Callable3 implements ICallable {
    @Override
    public int generate(int number1, int number2) {
        return number1*number2 - 3*number2;
    }
}
