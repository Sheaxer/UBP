import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileSelector extends JPanel implements ActionListener {

    private JTextField fileTF;
    private JButton selectBtn;

    private boolean keySelector;
    private boolean readMode;

    public FileSelector(boolean keySelector, boolean readMode) {
        super();

        this.fileTF = new JTextField(40);
        this.selectBtn = new JButton("Vybrať súbor");

        this.add(this.fileTF);
        this.add(this.selectBtn);

        this.keySelector = keySelector;
        this.readMode = readMode;

        this.selectBtn.addActionListener(this);
    }

    public String getFilePath() {
        return this.fileTF.getText();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        // should always be the button but being safe is never bad
        if(src.equals(this.selectBtn)) {
            // open the FileChooser window
            JFrame frame = new JFrame();
            JFileChooser chooser = new JFileChooser();

            // set the directory to the selected one
            if(this.fileTF.getText().length() > 0) {
                File currentFile = new File(this.fileTF.getText());

                if(currentFile.exists() && currentFile.isFile()) {
                    File currentDir = currentFile.getParentFile();
                    if(currentDir != null) {
                        chooser.setCurrentDirectory(currentDir);
                    }
                }
            }

            // set the file filter if this is a key selector
            if(this.keySelector) {
                FileNameExtensionFilter filter = new FileNameExtensionFilter("RSA kľúč v .der formáte", "der");
                chooser.setFileFilter(filter);
            }

            // display the chooser and process user input
            int returnValue;
            if(this.readMode) {
                returnValue = chooser.showOpenDialog(frame);

            }
            else {
                returnValue = chooser.showSaveDialog(frame);
            }

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                this.fileTF.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }
    }
}
