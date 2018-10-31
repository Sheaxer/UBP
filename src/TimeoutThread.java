public class TimeoutThread extends Thread {

	private InfoDisplay display;

	public TimeoutThread(InfoDisplay displayToTimeout) {
		this.display = displayToTimeout;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(5000);

			if(!this.isInterrupted()) {
				display.hideDisplay();
			}

		} catch (InterruptedException e) {
			// InterruptedException doesn't get thrown automatically
			// If it were, this is still OK, because the display would remain visible
		}
	}

}
