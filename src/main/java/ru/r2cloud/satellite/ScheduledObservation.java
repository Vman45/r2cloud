package ru.r2cloud.satellite;

import java.util.concurrent.Future;

import ru.r2cloud.model.ObservationRequest;

public class ScheduledObservation implements ScheduleEntry {

	private final ObservationRequest req;
	private final Future<?> future;
	private final Future<?> completeTaskFuture;
	private final Future<?> rotatorFuture;
	private final Runnable completeTask;
	
	private boolean cancelled = false;

	ScheduledObservation(ObservationRequest req, Future<?> future, Future<?> completeTaskFuture, Runnable completeTask, Future<?> rotatorFuture) {
		this.req = req;
		this.future = future;
		this.completeTaskFuture = completeTaskFuture;
		this.completeTask = completeTask;
		this.rotatorFuture = rotatorFuture;
	}

	public ObservationRequest getReq() {
		return req;
	}

	public Future<?> getFuture() {
		return future;
	}

	public Future<?> getReaperFuture() {
		return completeTaskFuture;
	}

	@Override
	public String getId() {
		return req.getSatelliteId();
	}

	@Override
	public long getStartTimeMillis() {
		return req.getStartTimeMillis();
	}

	@Override
	public long getEndTimeMillis() {
		return req.getEndTimeMillis();
	}

	public Runnable getCompleteTask() {
		return completeTask;
	}

	@Override
	public void cancel() {
		if (future != null) {
			future.cancel(true);
		}
		if (completeTaskFuture != null) {
			completeTaskFuture.cancel(true);
		}
		if (completeTask != null) {
			completeTask.run();
		}
		if (rotatorFuture != null) {
			rotatorFuture.cancel(true);
		}
		cancelled = true;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
}
