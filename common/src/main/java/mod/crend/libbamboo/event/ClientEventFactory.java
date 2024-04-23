package mod.crend.libbamboo.event;

import com.google.common.reflect.AbstractInvocationHandler;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/*
 * Implementation based on architectury-api's source for event handler implementation.
 * See https://github.com/architectury/architectury-api/blob/1.20.4/common/src/main/java/dev/architectury/event/EventFactory.java
 */
public class ClientEventFactory {
	private static <T> void invokeMethod(T listener, Method method, Object[] args) throws Throwable {
		MethodHandles.lookup().unreflect(method)
				.bindTo(listener).invokeWithArguments(args);
	}

	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <T> ClientEvent<T> createArrayBacked(T... type) {
		if (type.length != 0) throw new AssertionError();
		return createArrayBacked((Class<T>) type.getClass().getComponentType());
	}

	@SuppressWarnings("unchecked")
	public static <T> ClientEvent<T> createArrayBacked(Class<? super T> clazz) {
		return new ArrayBackedClientEvent<>(listeners -> (T) Proxy.newProxyInstance(ClientEventFactory.class.getClassLoader(), new Class[]{clazz}, new AbstractInvocationHandler() {
			@Override
			protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
				for (var listener : listeners) {
					invokeMethod(listener, method, args);
				}
				return null;
			}
		}));
	}

	public static class ArrayBackedClientEvent<T> implements ClientEvent<T> {

		private final Function<List<T>, T> invokerFactory;
		private T invoker;
		private final ArrayList<T> handlers;

		ArrayBackedClientEvent(Function<List<T>, T> invokerFactory) {
			this.invokerFactory = invokerFactory;
			this.handlers = new ArrayList<>();
			update();
		}

		@Override
		public T invoker() {
			return invoker;
		}

		@Override
		public void register(T listener) {
			handlers.add(listener);
			update();
		}

		@Override
		public boolean isRegistered() {
			return !handlers.isEmpty();
		}

		void update() {
			if (handlers.size() == 1) {
				invoker = handlers.get(0);
			} else {
				invoker = invokerFactory.apply(handlers);
			}
		}
	}
}
