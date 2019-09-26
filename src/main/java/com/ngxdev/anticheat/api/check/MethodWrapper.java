package com.ngxdev.anticheat.api.check;

import com.ngxdev.anticheat.FireflyXCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

import static com.ngxdev.anticheat.Firefly.devServer;

@Getter
@AllArgsConstructor
public class MethodWrapper {
    private Check check;
    private Method method;
    private int priority;

    public void call(Object argument) throws Exception {
        if (check.check == null || check.check.alert() || check.check.cancel() || check.check.ban() || devServer) {
            if (method.getParameterTypes()[0] == argument.getClass()) {
                long start = System.nanoTime();
                method.invoke(check, argument);
                FireflyXCommand.profiler.stopCustom(argument.getClass().getSimpleName().replace("WrappedPacket", "").replace("Event", ""), System.nanoTime() - start);
            }
        }
    }
}
