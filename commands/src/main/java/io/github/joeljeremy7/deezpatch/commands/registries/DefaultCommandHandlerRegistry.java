package io.github.joeljeremy7.deezpatch.commands.registries;

import io.github.joeljeremy7.deezpatch.commands.CommandHandler;
import io.github.joeljeremy7.deezpatch.commands.CommandHandlerInstanceProvider;
import io.github.joeljeremy7.deezpatch.commands.CommandHandlerProvider;
import io.github.joeljeremy7.deezpatch.commands.CommandHandlerRegistry;
import io.github.joeljeremy7.deezpatch.commands.RegisteredCommandHandler;
import io.github.joeljeremy7.deezpatch.commands.WeakConcurrentHashMap;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import static java.util.Objects.requireNonNull;

/**
 * The default command handler registry.
 */
public class DefaultCommandHandlerRegistry 
        implements CommandHandlerRegistry, CommandHandlerProvider {
    
    private final ConcurrentMap<Class<?>, RegisteredCommandHandler<?>> commandHandlersByCommandType 
        = new WeakConcurrentHashMap<>();
    private final CommandHandlerInstanceProvider commandHandlerInstanceProvider;

    /**
     * Constructor.
     * 
     * @param commandHandlerInstanceProvider The command handler instance provider.
     */
    public DefaultCommandHandlerRegistry(
            CommandHandlerInstanceProvider commandHandlerInstanceProvider
    ) {
        this.commandHandlerInstanceProvider = requireNonNull(commandHandlerInstanceProvider);
    }

    /** {@inheritDoc} */
    @Override
    public DefaultCommandHandlerRegistry scan(Class<?> commandHandlerClass) {
        requireNonNull(commandHandlerClass);

        // Register all methods marked with @CommandHandler.
        Arrays.stream(commandHandlerClass.getMethods())
            .filter(method -> 
                method.isAnnotationPresent(CommandHandler.class) &&
                // Has atleast 1 parameter. First parameter must be the command.
                hasAtLeastOneParameter(method)
            )
            .forEach(commandHandlerMethod -> register(
                // First parameter in the method is the command object.
                commandHandlerMethod.getParameterTypes()[0],
                commandHandlerMethod
            ));

        return this;
    }

    /** {@inheritDoc} */
    @Override
    public <T> Optional<RegisteredCommandHandler<T>> getCommandHandlerFor(Class<T> commandType) {
        requireNonNull(commandType);

        @SuppressWarnings("unchecked")
        RegisteredCommandHandler<T> handler = 
            (RegisteredCommandHandler<T>)commandHandlersByCommandType.get(commandType);
         
        return Optional.ofNullable(handler);
    }

    private void register(Class<?> commandType, Method commandHandlerMethod) {
        requireNonNull(commandType);
        requireNonNull(commandHandlerMethod);

        RegisteredCommandHandler<?> existing = commandHandlersByCommandType.putIfAbsent(
            commandType, 
            buildCommandHandler(commandHandlerMethod, commandHandlerInstanceProvider)
        );

        if (existing != null) {
            throw new UnsupportedOperationException(
                "Duplicate command handler registration for command: " + commandType
            );
        }
    }

    private static RegisteredCommandHandler<?> buildCommandHandler(
            Method commandHandlerMethod,
            CommandHandlerInstanceProvider commandHandlerInstanceProvider
    ) {
        requireNonNull(commandHandlerMethod);
        requireNonNull(commandHandlerInstanceProvider);
        
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle commandHandlerMethodHandle = lookup
                .in(commandHandlerMethod.getDeclaringClass())
                .unreflect(commandHandlerMethod);

            MethodType methodType = commandHandlerMethodHandle.type();
            Class<?> commandHandlerClass = methodType.parameterType(0);
            Class<?> commandType = methodType.parameterType(1);

            CallSite callSite = LambdaMetafactory.metafactory(
                lookup, 
                "invoke", 
                MethodType.methodType(CommandHandlerMethod.class),
                MethodType.methodType(void.class, Object.class, Object.class), 
                commandHandlerMethodHandle, 
                MethodType.methodType(void.class, commandHandlerClass, commandType)
            );

            CommandHandlerMethod commandHandlerMethodLambda = 
                (CommandHandlerMethod)callSite.getTarget().invoke();

            // Only request command handler instance when invoked instead of during registration time.
            return new RegisteredCommandHandler<Object>() {
                @Override
                public void invoke(Object command) {
                    commandHandlerMethodLambda.invoke(
                        commandHandlerInstanceProvider.getInstance(commandHandlerClass), 
                        command
                    );
                }

                @Override
                public String toString() {
                    return commandHandlerMethod.toGenericString();
                }
            };
        } catch (Throwable e) {
            throw new IllegalStateException(
                "Failed to build handler for method: " + commandHandlerMethod.toGenericString()
            );
        }
    }

    private static boolean hasAtLeastOneParameter(Method method) {
        if (method.getParameterCount() < 1) {
            throw new IllegalArgumentException(
                "Methods marked with @CommandHandler must accept a single parameter which is the command object."
            );
        }
        return true;
    }

    /**
     * Used in building lambdas via {@link LambdaMetafactory}.
     */
    private static interface CommandHandlerMethod {
        /**
         * Invoke the actual method annotated with {@link CommandHandler}.
         * 
         * @param commandHandlerInstance The command handler instance.
         * @param command The dispatched command.
         */
        void invoke(Object commandHandlerInstance, Object command);
    }
}
