import javax.swing.*;
import java.awt.*;

public class InfoDisplay extends JPanel {

	// class constants
	public static final int INFO = 0;
	public static final int ERROR = 1;

	// class variables
	private JLabel label;
	private TimeoutThread thread = null;

	public InfoDisplay(int displayType) {
		super();

		this.label = new JLabel();
		this.add(this.label);

		// set font based on type
		switch (displayType) {
			case INFO:
				// dirty
				String defaultFontName = this.label.getFont().getFamily();
				int defaultFontSize = this.label.getFont().getSize();

				Font infoFont = new Font(defaultFontName, Font.ITALIC, defaultFontSize);
				this.label.setFont(infoFont);
				break;

			case ERROR:
				this.label.setForeground(Color.RED);

				break;
		}

		this.setVisible(false);
	}

	public void hideDisplay() {
		this.setVisible(false);
	}

	public void display(String message, boolean fade) {
		// interupt the timeout thread, if we have any
		if(this.thread != null) {
			this.thread.interrupt();
		}

		// display message
		this.setVisible(true);
		this.label.setText(message);

		// start a new thread, if we want to fade the text
		if(fade) {
			this.thread = new TimeoutThread(this);
			this.thread.start();
		}
	}
}
