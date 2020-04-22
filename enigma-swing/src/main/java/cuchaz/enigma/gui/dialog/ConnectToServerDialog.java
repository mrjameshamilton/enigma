package cuchaz.enigma.gui.dialog;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.*;

import cuchaz.enigma.gui.elements.VerifiableTextField;
import cuchaz.enigma.network.EnigmaServer;
import cuchaz.enigma.utils.I18n;
import cuchaz.enigma.utils.ServerAddress;

public class ConnectToServerDialog extends JDialog {

	private final JTextField usernameField;
	private final VerifiableTextField ipField;
	private final JPasswordField passwordField;
	private boolean success = false;

	public ConnectToServerDialog(Frame owner) {
		super(owner, I18n.translate("prompt.connect.title"), true);

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		Container inputContainer = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		usernameField = new JTextField(System.getProperty("user.name"));
		ipField = new VerifiableTextField();
		passwordField = new JPasswordField();

		List<JLabel> labels = Stream.of("prompt.connect.username", "prompt.connect.address", "prompt.password")
				.map(I18n::translate)
				.map(JLabel::new)
				.collect(Collectors.toList());
		List<JTextField> inputs = Arrays.asList(usernameField, ipField, passwordField);

		for (int i = 0; i < inputs.size(); i += 1) {
			c.gridy = i;
			c.insets = new Insets(4, 4, 4, 4);

			c.gridx = 0;
			c.weightx = 0.0;
			c.anchor = GridBagConstraints.LINE_END;
			c.fill = GridBagConstraints.NONE;
			inputContainer.add(labels.get(i), c);

			c.gridx = 1;
			c.weightx = 1.0;
			c.anchor = GridBagConstraints.LINE_START;
			c.fill = GridBagConstraints.HORIZONTAL;
			inputs.get(i).addActionListener(event -> confirm());
			inputContainer.add(inputs.get(i), c);
		}
		contentPane.add(inputContainer, BorderLayout.CENTER);
		Container buttonContainer = new JPanel(new GridBagLayout());
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.insets = new Insets(4, 4, 4, 4);
		c.anchor = GridBagConstraints.LINE_END;
		JButton connectButton = new JButton(I18n.translate("prompt.connect.confirm"));
		connectButton.addActionListener(event -> confirm());
		buttonContainer.add(connectButton, c);
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.CENTER;
		JButton abortButton = new JButton(I18n.translate("prompt.connect.cancel"));
		abortButton.addActionListener(event -> cancel());
		buttonContainer.add(abortButton, c);
		contentPane.add(buttonContainer, BorderLayout.SOUTH);

		setLocationRelativeTo(owner);
		setSize(new Dimension(400, 185));
	}

	private void confirm() {
		if (validateInputs()) {
			success = true;
			setVisible(false);
		}
	}

	private void cancel() {
			success = false;
			setVisible(false);
	}

	public boolean validateInputs() {
		boolean error = false;
		ipField.setErrorState(false);

		if (ServerAddress.from(ipField.getText(), EnigmaServer.DEFAULT_PORT) == null) {
			ipField.setErrorState(true);
			error = true;
		}

		return !error;
	}

	public Result getResult() {
		if (!success) return null;
		return new Result(
				usernameField.getText(),
				Objects.requireNonNull(ServerAddress.from(ipField.getText(), EnigmaServer.DEFAULT_PORT)),
				passwordField.getPassword()
		);
	}

	public static Result show(Frame parent) {
		ConnectToServerDialog d = new ConnectToServerDialog(parent);

		d.setVisible(true);
		Result r = d.getResult();

		d.dispose();
		return r;
	}

	public static class Result {
		private final String username;
		private final ServerAddress address;
		private final char[] password;

		public Result(String username, ServerAddress address, char[] password) {
			this.username = username;
			this.address = address;
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public ServerAddress getAddress() {
			return address;
		}

		public char[] getPassword() {
			return password;
		}
	}

}
