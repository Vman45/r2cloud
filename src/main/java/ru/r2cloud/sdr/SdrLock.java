package ru.r2cloud.sdr;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import ru.r2cloud.Lifecycle;

public class SdrLock {

	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SdrLock.class);

	private final Map<Class<?>, Integer> priorities = new HashMap<>();
	private final Map<Class<?>, Lifecycle> listeners = new HashMap<>();
	private Lifecycle lockedBy = null;

	public void register(Class<?> clazz, int priority) {
		priorities.put(clazz, priority);
	}

	public synchronized boolean tryLock(Lifecycle listener) {
		Integer newPriority = priorities.get(listener.getClass());
		if (newPriority == null) {
			LOG.error("not registered: {}", listener.getClass());
			return false;
		}

		listeners.put(listener.getClass(), listener);
		if (lockedBy == null) {
			lockedBy = listener;
			return true;
		}
		Integer currentPriority = priorities.get(lockedBy.getClass());
		if (newPriority > currentPriority) {
			lockedBy.stop();
			lockedBy = listener;
			return true;
		} else {
			return false;
		}
	}

	public synchronized void unlock(Lifecycle listener) {
		if (lockedBy != listener) {
			return;
		}
		listeners.remove(listener.getClass());
		lockedBy = null;
		int maxPriority = 0;
		Lifecycle listenerToResume = null;
		for (Lifecycle cur : listeners.values()) {
			Integer curPriority = priorities.get(cur.getClass());
			if (curPriority > maxPriority) {
				maxPriority = curPriority;
				listenerToResume = cur;
			}
		}
		if (listenerToResume != null) {
			try {
				listenerToResume.start();
			} catch (Exception e) {
				LOG.error("unable to notify resume", e);
			}
		}
	}

}
