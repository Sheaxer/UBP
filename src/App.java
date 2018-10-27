import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

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

    public App() {
        super();
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

        // Mode
        JPanel modePanel = new JPanel();
        this.add(modePanel);
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
        this.add(inFilePanel);
        inFilePanel.add(new JLabel("Vstupný súbor:"));

        this.inFileSelector = new FileSelector(false, true);
        inFilePanel.add(this.inFileSelector);

        // Public key
        this.publicKeyPanel = new JPanel();
        this.add(this.publicKeyPanel);
        this.publicKeyPanel.add(new JLabel("Verejný kľúč (.DER):"));

        this.publicKeyFileSelector = new FileSelector(true, true);
        this.publicKeyPanel.add(this.publicKeyFileSelector);
        this.publicKeyPanel.setVisible(false);

        // Private key
        this.privateKeyPanel = new JPanel();
        this.add(this.privateKeyPanel);
        this.privateKeyPanel.add(new JLabel("Súkromný kľúč (.DER):"));

        this.privateKeyFileSelector = new FileSelector(true, true);
        this.privateKeyPanel.add(this.privateKeyFileSelector);

        // Output file
        JPanel outFilePanel = new JPanel();
        this.add(outFilePanel);
        outFilePanel.add(new JLabel("Výstupný súbor:"));

        this.outFileSelector = new FileSelector(false, false);
        outFilePanel.add(this.outFileSelector);

        // Button
        JPanel runBtnPanel = new JPanel();
        this.add(runBtnPanel);
        this.runBtn = new JButton("Spustiť");
        runBtnPanel.add(this.runBtn);
        this.runBtn.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if(source.equals(this.modeEnc)) {
            // show public key field
            this.publicKeyPanel.setVisible(true);
            this.privateKeyPanel.setVisible(false);
        }
        else if(source.equals(this.modeDec)) {
            // show private key field
            this.publicKeyPanel.setVisible(false);
            this.privateKeyPanel.setVisible(true);
        }
        else if(source.equals(this.runBtn)) {
            // do encryption/decryption
            if(this.modeEnc.isSelected()) {
                // Encryption
                File inputFile = new File(this.inFileSelector.getFilePath());
				if( !inputFile.exists()) {
					// neexistuje vstupny subor
					// TODO
					System.err.println("Neexistuje vstupny subor");
				}

				File outputFile = new File(this.outFileSelector.getFilePath());

				// ziskam si kluc z der suboru
				try {
					PublicKey key = CryptoUtils.getPublicKeyFromDER(this.publicKeyFileSelector.getFilePath());

					// zasifrujem vstupny subor
					try {
						CryptoUtils.encryptAsymetric(key, inputFile, outputFile);
					} catch (Exception eInner) {
						// TODO
						eInner.printStackTrace();
					}

				} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException eOuter) {
					// TODO
					eOuter.printStackTrace();
				}
			}
            else {
                // Decryption
				File inputFile = new File(this.inFileSelector.getFilePath());
				if( !inputFile.exists()) {
					// neexistuje vstupny subor
					// TODO
					System.err.println("Neexistuje vstupny subor");
				}

				File outputFile = new File(this.outFileSelector.getFilePath());

				// ziskam si kluc z der suboru
				try {
					PrivateKey key = CryptoUtils.getPrivateKeyFromDER(this.privateKeyFileSelector.getFilePath());

					// desifrujem vstupny subor
					try {
						CryptoUtils.decryptAsymetric(key, inputFile, outputFile);
					} catch (Exception eInner) {
						// TODO
						eInner.printStackTrace();
					}

				} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException eOuter) {
					// TODO
					eOuter.printStackTrace();
				}
            }
        }
    }

    public static void main(String args[]) {
        App a = new App();
        a.setSize(640, 260);
        a.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        a.setVisible(true);
    }
}
