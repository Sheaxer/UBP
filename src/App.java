import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class App extends JFrame implements ActionListener {

    private JRadioButton modeEnc;
    private JRadioButton modeDec;

    private FileSelector inFileSelector;

    private FileSelector publicKeyFileSelector;
    private JPanel publicKeyPanel;

    private FileSelector privateKeyFileSelector;
    private JPanel privateKeyPanel;

    private FileSelector outFileSelector;

    private JButton runBtn;

    private InfoDisplay infoDisplay;
    private InfoDisplay errorDisplay;

    private boolean decryptionSelected = true;

    public App() {
        super("ÚPB 2018 - Zadanie 3");
        this.setLayout(new BorderLayout());

        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.PAGE_AXIS));
        this.add(bodyPanel, "Center");

        // Mode
        JPanel modePanel = new JPanel();
        bodyPanel.add(modePanel);
        modePanel.add(new JLabel("Mód:"));

        ButtonGroup rbModeGroup = new ButtonGroup();

        this.modeDec = new JRadioButton("Dešifrovanie");
        rbModeGroup.add(this.modeDec);
        modePanel.add(this.modeDec);
        this.modeDec.addActionListener(this);
        this.modeDec.setSelected(true);

        this.modeEnc = new JRadioButton("Šifrovanie");
        rbModeGroup.add(this.modeEnc);
        modePanel.add(this.modeEnc);
        this.modeEnc.addActionListener(this);

        // Input File
        JPanel inFilePanel = new JPanel();
        bodyPanel.add(inFilePanel);
        inFilePanel.add(new JLabel("Vstupný súbor:"));

        this.inFileSelector = new FileSelector(false, true);
        inFilePanel.add(this.inFileSelector);

        // Public key
        this.publicKeyPanel = new JPanel();
        bodyPanel.add(this.publicKeyPanel);
        this.publicKeyPanel.add(new JLabel("Verejný kľúč (.DER):"));

        this.publicKeyFileSelector = new FileSelector(true, true);
        this.publicKeyPanel.add(this.publicKeyFileSelector);
        this.publicKeyPanel.setVisible(false);

        // Private key
        this.privateKeyPanel = new JPanel();
        bodyPanel.add(this.privateKeyPanel);
        this.privateKeyPanel.add(new JLabel("Súkromný kľúč (.DER):"));

        this.privateKeyFileSelector = new FileSelector(true, true);
        this.privateKeyPanel.add(this.privateKeyFileSelector);

        // Output file
        JPanel outFilePanel = new JPanel();
        bodyPanel.add(outFilePanel);
        outFilePanel.add(new JLabel("Výstupný súbor:"));

        this.outFileSelector = new FileSelector(false, false);
        outFilePanel.add(this.outFileSelector);

        // Button
        JPanel runBtnPanel = new JPanel();
        bodyPanel.add(runBtnPanel);
        this.runBtn = new JButton("Spustiť");
        runBtnPanel.add(this.runBtn);
        this.runBtn.addActionListener(this);

        // Bottom Panel
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));
		this.add(bottomPanel, "South");

		this.infoDisplay = new InfoDisplay( InfoDisplay.INFO );
		bottomPanel.add(this.infoDisplay);

		this.errorDisplay = new InfoDisplay( InfoDisplay.ERROR );
		bottomPanel.add(this.errorDisplay);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if(source.equals(this.modeEnc)) {
            // show public key field
            this.publicKeyPanel.setVisible(true);
            this.privateKeyPanel.setVisible(false);

            this.decryptionSelected = false;
        }
        else if(source.equals(this.modeDec)) {
            // show private key field
            this.publicKeyPanel.setVisible(false);
            this.privateKeyPanel.setVisible(true);

            // QoL feature
			if( !this.decryptionSelected) {
				// user switched from encryption to decryption
				// we help them by swapping the output field to the input and clearing the output
				this.inFileSelector.setFilePath(this.outFileSelector.getFilePath());
				this.outFileSelector.setFilePath("");

				// let the user know to avoid confusion
				this.infoDisplay.display("Výstupný súbor šifrovania bol automaticky nastavený ako vstupný súbor dešifrovania.", true);

				// reset the flag
				this.decryptionSelected = true;
			}
        }
        else if(source.equals(this.runBtn)) {
        	// hide the InfoDisplays
			this.infoDisplay.hideDisplay();
			this.errorDisplay.hideDisplay();

            // get the input file
			File inputFile = new File(this.inFileSelector.getFilePath());
			if( !inputFile.exists()) {
				this.errorDisplay.display("Vstupný súbor neexistuje.", false);
				return;
			}
			else if( !inputFile.canRead() ) {
				this.errorDisplay.display("Zo vstupného súboru sa nedá čítať.", false);
				return;
			}

			// create the output file
			File outputFile = new File(this.outFileSelector.getFilePath());

			if(!outputFile.exists()) {
				try {
					outputFile.createNewFile();
				} catch (IOException | SecurityException ex) {
					this.errorDisplay.display("Výstupný súbor nie je možné vytvoriť.", false);
					return;
				}
			}
			else if(!outputFile.canWrite()) {
				this.errorDisplay.display("Do výstupného súboru sa nedá zapisovať.", false);
				return;
			}

        	// do encryption/decryption
            if(this.modeEnc.isSelected()) {
                // Encryption

				PublicKey key;
				// get key from .der file
				try {
					File publicKeyFile = new File(this.publicKeyFileSelector.getFilePath());

					key = CryptoUtils.readPublicKey(publicKeyFile);

				} catch (Exception ex) {
					this.errorDisplay.display("Chyba pri načítavaní kľúča. Uistite sa, že súbor existuje, že je v .der formáte a že sa jedná o VEREJNÝ kľúč.", false);
					return;
				}

				this.infoDisplay.display("Šifrujem...", false);

				// encrypt the file
				try {
					CryptoUtils.encryptAsymetric(key, inputFile, outputFile);
					this.infoDisplay.display("Hotovo", true);
				} catch (Exception ex) {
					this.infoDisplay.hideDisplay();
					this.errorDisplay.display("Chyba pri šifrovaní súboru. Uistite sa, že súbor exituje a že používate správny VEREJNÝ kľúč", false);
				}
			}
            else {
                // Decryption

				// get key from .der file
				PrivateKey key;
				try {
					File privateKeyFile = new File(this.privateKeyFileSelector.getFilePath());

					key = CryptoUtils.readPrivateKey(privateKeyFile);

				} catch (Exception ex) {
					this.errorDisplay.display("Chyba pri načítavaní kľúča. Uistite sa, že súbor existuje, že je v .der formáte a že sa jedná o SÚKROMNÝ kľúč.", false);
					return;
				}

				this.infoDisplay.display("Dešifrujem...", false); // swing doesn't display this for some reason

				// decrypt the file
				try {
					CryptoUtils.decryptAsymetric(key, inputFile, outputFile);
					this.infoDisplay.display("Hotovo", true);
				} catch (Exception ex) {
					this.infoDisplay.hideDisplay();
					this.errorDisplay.display("Chyba pri dešifrovaní súboru. Uistite sa, že súbor exituje, že používate správny SÚKROMNÝ kľúč a že dešifrujete správny súbor.", false);
				}
            }
        }
    }

    public static void main(String args[]) {
        App a = new App();
        a.setSize(740, 300);
        a.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        a.setVisible(true);
    }
}

