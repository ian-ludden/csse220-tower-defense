package gameEngine;

import com.formdev.flatlaf.FlatLightLaf;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.BorderLayout;

import javax.swing.*;

public class TowerDefenseMain {
	public static final Dimension GAME_WINDOW_SIZE = new Dimension(800, 640);
	private static final int CONTROLS_PANEL_HEIGHT = 60;

    public static final String DEFAULT_TOWER_TYPE = "Archer";

	public static void main(String[] args) {
		// Set look and feel to FlatLaf
		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (UnsupportedLookAndFeelException e) {
			JOptionPane.showMessageDialog(null, "Failed to set look and feel to FlatLaf!", "Error", JOptionPane.ERROR_MESSAGE);
		}

		// Construct frame, component, and control panel
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GameComponent gameComponent = new GameComponent();
		gameComponent.setPreferredSize(GAME_WINDOW_SIZE);
		frame.add(gameComponent, BorderLayout.CENTER);

		JPanel controlPanel = new JPanel();
		controlPanel.setPreferredSize(new Dimension(GAME_WINDOW_SIZE.width, CONTROLS_PANEL_HEIGHT));
		JButton startWaveButton = new JButton("Start Wave");
        startWaveButton.addActionListener(gameComponent);
		controlPanel.add(startWaveButton);
		JButton upgradeTowerButton = new JButton("Upgrade Tower");
        upgradeTowerButton.addActionListener(gameComponent);
		controlPanel.add(upgradeTowerButton);
		controlPanel.add(new JLabel("Select Tower Type: "));
        ArrayList<String> towerTypes = new ArrayList<String>(Arrays.asList("Archer", "Missile", "Laser"));
		JComboBox<String> towerTypeComboBox = new JComboBox<String>(towerTypes.toArray(new String[0]));
		towerTypeComboBox.setSelectedIndex(towerTypes.indexOf(DEFAULT_TOWER_TYPE));
		towerTypeComboBox.addActionListener(gameComponent);
		controlPanel.add(towerTypeComboBox);

		frame.add(controlPanel, BorderLayout.SOUTH);

		frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}